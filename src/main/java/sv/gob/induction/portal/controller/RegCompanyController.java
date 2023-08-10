package sv.gob.induction.portal.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import sv.gob.induction.portal.domain.*;
import sv.gob.induction.portal.dto.WebServicePersonaDTO;
import sv.gob.induction.portal.repository.RegDocDetailsDTORepository;
import sv.gob.induction.portal.commons.Constants;
import sv.gob.induction.portal.commons.S2;
import sv.gob.induction.portal.commons.S2Response;
import sv.gob.induction.portal.commons.ServiceResponse;
import sv.gob.induction.portal.config.SecurityHelper;
import sv.gob.induction.portal.service.*;
import sv.gob.induction.portal.dto.RegDocDetailsDTO;
import sv.gob.induction.portal.enums.TipoDocumentoEnum;
import sv.gob.induction.portal.enums.TipoTelefonoEnum;
import sv.gob.induction.portal.enums.TitularTelefonoEnum;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/user/reg/")
public class RegCompanyController {

    private static final String REDIRECT_index = "redirect:/user/index";

    @Autowired
    private CcPersonaService ccPersonaService;

    @Autowired
    private GenGeneroService genGeneroService;

    @Autowired
    private RegEmpresaService regEmpresaService;

    @Autowired
    private RegSolicitudService regSolicitudService;

    @Autowired
    private RegTelefonosService regTelefonosService;

    @Autowired
    private RegDocumentoAnexoService regDocumentoAnexoService;

    @Autowired
    private RegDocDetailsDTORepository regDocDetailsDTORepository;

    @Autowired
    private AceActividadEconomicaService aceActividadEconomicaService;

    @Autowired
    private GirGiroService girGiroService;

    @RequestMapping("/company")
    public ModelAndView regCompany(@RequestParam(required = false) String back) {
        ModelAndView mv = new ModelAndView();
        String user = SecurityHelper.getLoggedInUserDetails().getUsername();
        CcPersona per = ccPersonaService.findBySecUser(user);
        Integer aceptoTerminos = regSolicitudService.aceptoTerminosOnThisSol(user);
        mv.addObject("tipoPersona", (per != null && per.getCdTipoPersona().equals("J")) ? "Jur\u00eddica" : "Natural");
        mv.addObject("nav", (back != null && back.equals("1")) ? "back" : "new");
        mv.addObject("aceptoTerminos", aceptoTerminos);
        mv.setViewName("pages/regCompany/begin");
        if(per != null && per.getCdTipoPersona().equals("J")){
            mv.setViewName("pages/regCompany/begin_PJ");
        }
        return mv;
    }

    @PostMapping(value = "/company/postdata", produces = Constants.APPLICATION_JSON)
    public @ResponseBody ModelAndView regCompanyPostData(HttpServletRequest request,
                                                         @RequestParam(value = "chkboxTerminosYcond") String chkboxTerminosYcond) {
        ModelAndView mv = new ModelAndView();
        if (chkboxTerminosYcond == null || !chkboxTerminosYcond.equalsIgnoreCase("on")) {
            mv.setViewName(REDIRECT_index);
            return mv;
        }
        RegEmpresa dataCompany = new RegEmpresa();
        CcPersona per = ccPersonaService.findBySecUser(SecurityHelper.getLoggedInUserDetails().getUsername());
        WebServicePersonaDTO personaByCC = new WebServicePersonaDTO();
        if (personaByCC != null && personaByCC.getCcc() != null) {// tiene datos con CodigoCuentaCorriente
//        FIXME  PENDING  dataCompany = regEmpresaService.findOrCreateSolicitudByUserCCCSolicitante(personaByCC , SecurityHelper.getLoggedInUserDetails().getUsername() );
        }else{
            dataCompany = regEmpresaService.findOrCreateRegEmpresaByUserSolicitante(SecurityHelper.getLoggedInUserDetails().getUsername());
        }
        mv.setViewName("redirect:/user/reg/company/data/" + dataCompany.getSkEmpresa());
        return mv;
    }

    @RequestMapping("/company/data/{skRegEmpresa}")
    public ModelAndView regCompanyData(@PathVariable("skRegEmpresa") Integer skRegEmpresa) {
        ModelAndView mv = new ModelAndView();
        RegEmpresa dataCompany = regEmpresaService.findByIdAndSolicitanteAndEstadoCre(skRegEmpresa, SecurityHelper.getLoggedInUserDetails().getUsername());
        if (dataCompany == null || dataCompany.getSkEmpresa() == null) {
            mv.setViewName(REDIRECT_index);
            return mv;
        }
        CcPersona per = ccPersonaService.findBySecUser(SecurityHelper.getLoggedInUserDetails().getUsername());
        WebServicePersonaDTO personaByCC = new WebServicePersonaDTO(); // fixme!
        if (personaByCC != null && personaByCC.getCcc() != null) {// tiene datos con CodigoCuentaCorriente
            String firstPhone = ((dataCompany.getRegTelefonos() != null && !dataCompany.getRegTelefonos().isEmpty()) ? dataCompany.getRegTelefonos().stream().findFirst().get().getStNumeroTelefono() : "No Ingresado aun");
            mv.addObject("dataPerson", per);
            mv.addObject("sol", dataCompany.getRegSolicitud());
            mv.addObject("dataCompany", dataCompany);
            mv.addObject("firstPhone", firstPhone);
            mv.setViewName("pages/regCompany/dataCCC");
            return mv;
        }
        return regCompanyRqst(dataCompany);
    }

//    @RequestMapping("/company/rqst")
    public ModelAndView regCompanyRqst(RegEmpresa dataCompany) {
        ModelAndView mv = new ModelAndView();List<String> telefonosTitular = obtenerTelsListDeSolicitudByTipoTitular(dataCompany, TitularTelefonoEnum.PROPIETARIO.getCodigo());
        Set<RegDocDetailsDTO> docsDto = regDocDetailsDTORepository.findBySkEmpresa(dataCompany.getSkEmpresa());
        mv.addObject("duiDoc", docsDto.stream().filter(d ->d.getCdTipoDoc().equals(TipoDocumentoEnum.DUI.getTipo())).findFirst().orElse(new RegDocDetailsDTO()));
        mv.addObject("nitDoc", docsDto.stream().filter(d ->d.getCdTipoDoc().equals(TipoDocumentoEnum.NIT.getTipo())).findFirst().orElse(new RegDocDetailsDTO()));
        mv.addObject("nrcDoc", docsDto.stream().filter(d ->d.getCdTipoDoc().equals(TipoDocumentoEnum.NRC.getTipo())).findFirst().orElse(new RegDocDetailsDTO()));
        mv.addObject("pasDoc", docsDto.stream().filter(d ->d.getCdTipoDoc().equals(TipoDocumentoEnum.PAS.getTipo())).findFirst().orElse(new RegDocDetailsDTO()));
        mv.addObject("sol", dataCompany.getRegSolicitud());
        mv.addObject("dataCompany", dataCompany);
        mv.addObject("telefonosTitular", telefonosTitular);
        mv.setViewName("pages/regCompany/dataCompany");
        return mv;
    }

    @PostMapping("/company/cancelRqst")
    public @ResponseBody ServiceResponse cancelRqst(@RequestParam Integer skSolicitud) {
        return regSolicitudService.delete(skSolicitud);
    }

    @RequestMapping("/company/finan/{skRegEmpresa}")
    public ModelAndView regCompanyFinan(@PathVariable("skRegEmpresa") Integer skRegEmpresa) {
        ModelAndView mv = new ModelAndView();
        RegEmpresa dataCompany = regEmpresaService.findByIdAndSolicitanteAndEstadoCre(skRegEmpresa, SecurityHelper.getLoggedInUserDetails().getUsername());
        if (dataCompany == null || dataCompany.getSkEmpresa() == null) {
            mv.setViewName(REDIRECT_index);
            return mv;
        }
        List<String> telefonosEstablecimiento = obtenerTelsListDeSolicitudByTipoTitular(dataCompany, TitularTelefonoEnum.ESTABLECIMIENTO.getCodigo());
        Set<RegDocDetailsDTO> docsDto = regDocDetailsDTORepository.findBySkEmpresa(dataCompany.getSkEmpresa());
        mv.addObject("sol", dataCompany.getRegSolicitud());
        mv.addObject("dataCompany", dataCompany);
        mv.addObject("actEco", dataCompany.getGirGiro());
        mv.addObject("telefonosEstablecimiento", telefonosEstablecimiento);
        mv.addObject("arrDoc", docsDto.stream().filter(d ->d.getCdTipoDoc().equals(TipoDocumentoEnum.ARE.getTipo())).findFirst().orElse(new RegDocDetailsDTO()));
        mv.addObject("balDoc", docsDto.stream().filter(d ->d.getCdTipoDoc().equals(TipoDocumentoEnum.BAL.getTipo())).findFirst().orElse(new RegDocDetailsDTO()));
        mv.addObject("invDoc", docsDto.stream().filter(d ->d.getCdTipoDoc().equals(TipoDocumentoEnum.INV.getTipo())).findFirst().orElse(new RegDocDetailsDTO()));

        mv.setViewName("pages/regCompany/dataFinancials");
        return mv;
    }

    @RequestMapping("/company/creden/{skRegEmpresa}")
    public ModelAndView regCompanyCreden(@PathVariable("skRegEmpresa") Integer skRegEmpresa) {
        ModelAndView mv = new ModelAndView();
        RegEmpresa dataCompany = regEmpresaService.findByIdAndSolicitanteAndEstadoCre(skRegEmpresa, SecurityHelper.getLoggedInUserDetails().getUsername());
        if (dataCompany == null || dataCompany.getSkEmpresa() == null) {
            mv.setViewName(REDIRECT_index);
            return mv;
        }
        Set<RegDocDetailsDTO> docsDto = regDocDetailsDTORepository.findBySkEmpresa(dataCompany.getSkEmpresa());
        mv.addObject("sol", dataCompany.getRegSolicitud());
        mv.addObject("dataCompany", dataCompany);
        mv.addObject("creDoc", docsDto.stream().filter(d ->d.getCdTipoDoc().equals(TipoDocumentoEnum.CRED.getTipo())).findFirst().orElse(new RegDocDetailsDTO()));

        mv.setViewName("pages/regCompany/dataCredencials");
        return mv;
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)
    @GetMapping(value = {"/cboGenderFilterS2"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody
    S2Response<S2> cboGenderFilterS2(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "rows", required = false) Integer rows) {
        S2Response<S2> response;
        if (rows != 0) {
            Slice<GenGenero> list;
            if (q == null || q.equals("")) {
                list = genGeneroService.getList(page, rows);
            } else {
                list = genGeneroService.getListByQ(q, PageRequest.of(page - 1, rows));
            }
            List<S2> results = new ArrayList<>();
            list.getContent().stream().map(u -> new S2(u.getGenId().toString(), u.getGenNombre().toString(), null)).forEachOrdered(results::add);
            response = new S2Response<>(results, list.hasNext());
        } else {
            response = new S2Response<>();
        }
        return response;
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)
    @GetMapping(value = {"/cboSectorEcoFilterS2"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody
    S2Response<S2> cboSectorEcoFilterS2(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "rows", required = false) Integer rows) {
        S2Response<S2> response;
        if (rows != 0) {
            Slice<AceActividadEconomica> list;
            if (q == null || q.equals("")) {
                list = aceActividadEconomicaService.getList(page, rows);
            } else {
                list = aceActividadEconomicaService.getListByQ(q, PageRequest.of(page - 1, rows));
            }
            List<S2> results = new ArrayList<>();
            list.getContent().stream().map(u -> new S2(u.getAceId().toString(), u.getAceNombre().toString(), null)).forEachOrdered(results::add);
            response = new S2Response<>(results, list.hasNext());
        } else {
            response = new S2Response<>();
        }
        return response;
    }

  @GetMapping(value = {"/cboActEcoFilterS2/{id}"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody
    S2Response<S2> cboActEcoFilterS2(
            @PathVariable(value = "id",required = true) Integer idParent,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "rows", required = false) Integer rows) {
        S2Response<S2> response;
        if (rows != 0) {
            Slice<GirGiro> list;
            if (q == null || q.equals("")) {
                list = girGiroService.getListByParent(idParent, PageRequest.of(page - 1, rows));
            } else {
                list = girGiroService.getListByParentAndContainigOnGirNombre(idParent, q, PageRequest.of(page - 1, rows));
            }
            List<S2> results = new ArrayList<>();
            list.getContent().stream().map(u -> new S2(u.getGirId().toString(), u.getGirNombre().toString(), null)).forEachOrdered(results::add);
            response = new S2Response<>(results, list.hasNext());
        } else {
            response = new S2Response<>();
        }
        return response;
    }

    @GetMapping(value = {"/cboTypePhoneFilterS2"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody
    S2Response<S2> cboTypePhoneFilterS2(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "rows", required = false) Integer rows) {
        S2Response<S2> response;
        if (rows != 0) {
            List<TipoTelefonoEnum> tipos = null;
            if (q != null && !q.isEmpty()) {
                tipos = Collections.singletonList(Arrays.stream(TipoTelefonoEnum.values())
                        .filter(e -> e.getDescripcion().contains(q))
                        .findFirst()
                        .orElse(TipoTelefonoEnum.OTRO));
            } else {
                tipos = Arrays.asList(TipoTelefonoEnum.class.getEnumConstants());
            }
            List<S2> results = new ArrayList<>();
            tipos.stream().map(u -> new S2(u.getCodigo().toString(), u.getDescripcion().toString(), null)).forEachOrdered(results::add);
            response = new S2Response<>(results, false);
        } else {
            response = new S2Response<>();
        }
        return response;
    }

    private List<String> obtenerTelsListDeSolicitudByTipoTitular(RegEmpresa dataCompany, String codigoTitular) {
        List<String> tels = new ArrayList<>();
        if (dataCompany.getRegTelefonos() != null && dataCompany.getRegTelefonos().size() > 0) {
            tels.addAll(dataCompany.getRegTelefonos().stream()
                    .filter(tel -> (tel.getSkTel() != null && tel.getCcTelefonoTitular().getCdTelefonoTitular().equals(codigoTitular)))
                    .map(RegTelefonos::getStNumeroTelefono).collect(Collectors.toList()));
        }
        return tels;
    }

    @PostMapping(value = {"/company/saveTelephoneTitular"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody ServiceResponse saveTelephoneTitular(
            @RequestParam Integer skEmpresa,
            @RequestParam String tipoTel,
            @RequestParam String newtel) {
        ServiceResponse response = regTelefonosService.saveTelephoneForOwner(skEmpresa, tipoTel, newtel, SecurityHelper.getLoggedInUserDetails().getCuser());
        return response;
    }


    @PostMapping(value = {"/company/saveTelephoneOther"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody ServiceResponse saveTelephoneOther(
            @RequestParam Integer skEmpresa,
            @RequestParam String tipoTel,
            @RequestParam String newtel,
            @RequestParam String tipoTitular) {
        String cdtipoTitular = Arrays.stream(TitularTelefonoEnum.values())
                .filter(titu -> titu.getCodigo().equals(tipoTitular))
                .findFirst().map(TitularTelefonoEnum::getCodigo).orElse(TitularTelefonoEnum.OTRO.getCodigo());

        ServiceResponse response = regTelefonosService.saveTelephoneOther(skEmpresa, tipoTel, newtel, SecurityHelper.getLoggedInUserDetails().getCuser(), cdtipoTitular);
        return response;
    }


    @PostMapping(value = {"/company/removeTelephone"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody ServiceResponse removeTelephone(
            @RequestParam Integer skEmpresa,
            @RequestParam String phone,
            @RequestParam String tipoTitular) {
        ServiceResponse response = regTelefonosService.removeTelephone(skEmpresa, phone, tipoTitular);
        return response;
    }

    @PostMapping(value = {"/company/saveData"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody ServiceResponse saveDataForCompany(
            @RequestParam Integer skSolicitud,
            @RequestParam Integer skEmpresa,
            @RequestParam String stNombres,
            @RequestParam String stApellidos,
            @RequestParam String stDireccion,
            @RequestParam Integer genGeneroID,
            @RequestParam Integer extranjero,
            @RequestParam String dui,
            @RequestParam String nit,
            @RequestParam String nrc,
            @RequestParam String pasaporte,
            @RequestParam String stEmail,
            @RequestParam String stDireccionCobros,
            @RequestParam(required = false) MultipartFile duiFile,
            @RequestParam(required = false) MultipartFile nitFile,
            @RequestParam(required = false) MultipartFile nrcFile,
            @RequestParam(required = false) MultipartFile pasFile

    ) {
        Optional<RegEmpresa> res = regEmpresaService.findById(skEmpresa);
        RegEmpresa provisional = res.get();
        provisional.setStNombres(stNombres);
        provisional.setStApellidos(stApellidos);
        provisional.setStDireccion(stDireccion);
        provisional.setGenGenero(genGeneroService.findByGenId(genGeneroID).get());
        provisional.setExtranjero(extranjero);
        provisional.setDui(dui);
        provisional.setNit(nit);
        provisional.setNrc(nrc);
        provisional.setPasaporte(pasaporte);
        provisional.setStEmail(stEmail);
        provisional.setStDireccionCobros(stDireccionCobros);
//        ServiceResponse response = regEmpresaService.saveValidated(provisional);
        ServiceResponse response = regEmpresaService.saveValidatedCompanyAndAttachment(provisional, duiFile, nitFile, nrcFile, pasFile);

        return response;

    }
    @PostMapping(value = {"/company/saveDataFinancials"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody ServiceResponse saveDataFinancials(
            @RequestParam Integer skSolicitud,
            @RequestParam Integer skEmpresa,
            @RequestParam String stDenominacion,
            @RequestParam String stDireccionInmueble,
            @RequestParam Integer cdPropietarioInm,
            @RequestParam String cdTipoActivo,
            @RequestParam Integer cdContaFormal,
            @RequestParam String stPropietarioInm,
            @RequestParam Integer sectorEcoDelegate,
            @RequestParam Integer actEcoDelegate,
            @RequestParam String fcInicioOperaciones,
            @RequestParam String activoInicial,
            @RequestParam String activoNeto,
            @RequestParam String stMatricula,
            @RequestParam String stNombreContador,
            @RequestParam String stApellidoContador,
            @RequestParam Integer genGeneroContadorDelegate,
            @RequestParam String stEmailContador,
            @RequestParam String comentario,
            @RequestParam(required = false) MultipartFile arrFile,
            @RequestParam(required = false) MultipartFile balFile,
            @RequestParam(required = false) MultipartFile invFile

    ) throws ParseException {
        Optional<RegEmpresa> res = regEmpresaService.findById(skEmpresa);
        SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy");
        Date fechaInicioOper = df.parse(fcInicioOperaciones);
        RegEmpresa provisional = res.get();
        provisional.setStDenominacion(stDenominacion);
        provisional.setStDireccionInmueble(stDireccionInmueble);
        provisional.setCdPropietarioInm(cdPropietarioInm);
        provisional.setCdTipoActivo(cdTipoActivo);
        provisional.setCdContaFormal(cdContaFormal);
        provisional.setStPropietarioInm(stPropietarioInm);
        provisional.setGirGiro(girGiroService.findById(actEcoDelegate).get());
        provisional.setFcInicioOperaciones(fechaInicioOper.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        provisional.setActivoInicial(obtenerActivo((activoInicial != null && !activoInicial.equals("")) ? activoInicial : activoNeto));
        provisional.setStMatricula(stMatricula);
        provisional.setStNombreContador(stNombreContador);
        provisional.setStApellidoContador(stApellidoContador);
        provisional.setGenGeneroContador(genGeneroService.findByGenId(genGeneroContadorDelegate).get());
        provisional.setStEmailContador(stEmailContador);
        provisional.setComentario(comentario);

        ServiceResponse response = regEmpresaService.saveValidatedCompanyAndAttachment(provisional, arrFile, balFile, invFile);

        return response;

    }

    private Double obtenerActivo(String activo) {
        try {
            return Double.parseDouble(activo.replaceAll("\\$", ""));
        } catch (NumberFormatException e) {
            return 0.00;
        }
    }

    @PostMapping("/company/deleteFileFromSol")
    public @ResponseBody ServiceResponse deleteFileFromSol(@RequestParam Integer skElemento) {
        return regDocumentoAnexoService.delete(skElemento);
    }

      @PostMapping("/company/sendSolicitud")
    public @ResponseBody ServiceResponse sendSolicitud(
              @RequestParam Integer skSolicitud,
              @RequestParam Integer skEmpresa,
              @RequestParam(required = false) MultipartFile creFile) {
          Optional<RegEmpresa> res = regEmpresaService.findById(skEmpresa);
          RegEmpresa provisional = res.get();;
          List<MultipartFile> files = new ArrayList<>();
          files.add(creFile);
        ServiceResponse response = regEmpresaService.sendAndValidatedSolWithCredencials(provisional, files);

        return response;
    }



}
