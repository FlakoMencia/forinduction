package sv.gob.induction.portal.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import sv.gob.induction.portal.domain.CcPersona;
import sv.gob.induction.portal.domain.RegEmpresa;
import sv.gob.induction.portal.domain.RegTelefonos;
import sv.gob.induction.portal.dto.WebServicePersonaDTO;
import sv.gob.induction.portal.repository.RegDocDetailsDTORepository;
import sv.gob.induction.portal.commons.Constants;
import sv.gob.induction.portal.commons.ServiceResponse;
import sv.gob.induction.portal.config.SecurityHelper;
import sv.gob.induction.portal.service.*;
import sv.gob.induction.portal.dto.RegDocDetailsDTO;
import sv.gob.induction.portal.enums.TipoDocumentoEnum;
import sv.gob.induction.portal.enums.TitularTelefonoEnum;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/user/reg/")
public class RegCompanyPJController {

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


    @RequestMapping("/companyPJ")
    public ModelAndView regCompany(@RequestParam(required = false) String back) {
        ModelAndView mv = new ModelAndView();
        CcPersona per = ccPersonaService.findBySecUser(SecurityHelper.getLoggedInUserDetails().getUsername());
        mv.addObject("tipoPersona", (per != null && per.getCdTipoPersona().equals("J")) ? "Jur\u00eddica" : "Natural");
        mv.addObject("nav", (back != null && back.equals("1")) ? "back" : "new");
        mv.setViewName("pages/regCompany/begin");
        if(per != null && per.getCdTipoPersona().equals("J")){
            mv.setViewName("pages/regCompany/begin_PJ");
        }
        return mv;
    }
    @PostMapping(value = "/company/postdataPJ", produces = Constants.APPLICATION_JSON)
    public @ResponseBody ModelAndView regCompanyPostDataPJ(HttpServletRequest request,
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
//       fixme!!     dataCompany = regEmpresaService.findOrCreateSolicitudByUserCCCSolicitante(personaByCC , SecurityHelper.getLoggedInUserDetails().getUsername() );
        }else{
            dataCompany = regEmpresaService.findOrCreateRegEmpresaByUserSolicitante(SecurityHelper.getLoggedInUserDetails().getUsername());
        }
        mv.setViewName("redirect:/user/reg/company/dataPJ/" + dataCompany.getSkEmpresa());
        return mv;
    }


    @RequestMapping("/company/dataPJ/{skRegEmpresa}")
    public ModelAndView regCompanyData(@PathVariable("skRegEmpresa") Integer skRegEmpresa) {
        ModelAndView mv = new ModelAndView();
        RegEmpresa dataCompany = regEmpresaService.findByIdAndSolicitanteAndEstadoCre(skRegEmpresa, SecurityHelper.getLoggedInUserDetails().getUsername());
        if (dataCompany == null || dataCompany.getSkEmpresa() == null) {
            mv.setViewName(REDIRECT_index);
            return mv;
        }
        CcPersona per = ccPersonaService.findBySecUser(SecurityHelper.getLoggedInUserDetails().getUsername());
        WebServicePersonaDTO personaByCC = new WebServicePersonaDTO(); // fixme!!
        if (personaByCC != null && personaByCC.getCcc() != null ) {// tiene datos con CodigoCuentaCorriente
            String firstPhone = (dataCompany.getRegTelefonos() != null && dataCompany.getRegTelefonos().size() > 0 ? dataCompany.getRegTelefonos().stream().findFirst().get().getStNumeroTelefono() : "No Ingresado aun");
            mv.addObject("dataPerson", per);
            mv.addObject("sol", dataCompany.getRegSolicitud());
            mv.addObject("dataCompany", dataCompany);
            mv.addObject("firstPhone", firstPhone);
            mv.setViewName("pages/regCompany/dataCCC_PJ");
            return mv;
        }
        return regCompanyRqstPJ(dataCompany);
    }
    @RequestMapping("/company/dataPJ2/{skEmpresa}")
    public ModelAndView dataPJ2(@PathVariable("skEmpresa") Integer skRegEmpresa) {
        ModelAndView mv = new ModelAndView();
        RegEmpresa dataCompany = regEmpresaService.findByIdAndSolicitanteAndEstadoCre(skRegEmpresa, SecurityHelper.getLoggedInUserDetails().getUsername());
        if (dataCompany == null || dataCompany.getSkEmpresa() == null) {
            mv.setViewName(REDIRECT_index);
            return mv;
        }
        WebServicePersonaDTO personaByCC = new WebServicePersonaDTO(); // fixme!!
        if (personaByCC != null && personaByCC.getCcc() != null ) {// tiene datos con CodigoCuentaCorriente
            String firstPhone = (dataCompany.getRegTelefonos() != null && dataCompany.getRegTelefonos().size() > 0 ? dataCompany.getRegTelefonos().stream().findFirst().get().getStNumeroTelefono() : "No Ingresado aun");
            mv.addObject("sol", dataCompany.getRegSolicitud());
            mv.addObject("dataCompany", dataCompany);
            mv.setViewName("pages/regCompany/dataCCC_PJ2");
            return mv;
        }
        return regCompanyRqstPJ2(skRegEmpresa);
    }


    public ModelAndView regCompanyRqstPJ(RegEmpresa dataCompany) {
        ModelAndView mv = new ModelAndView();
        List<String> telefonosTitular = obtenerTelsListDeSolicitudByTipoTitular(dataCompany, TitularTelefonoEnum.PROPIETARIO.getCodigo()) ;
        Set<RegDocDetailsDTO> docsDto = regDocDetailsDTORepository.findBySkEmpresa(dataCompany.getSkEmpresa());

        mv.addObject("escDoc", docsDto.stream().filter(d ->d.getCdTipoDoc().equals(TipoDocumentoEnum.ESC.getTipo())).findFirst().orElse(new RegDocDetailsDTO()));
        mv.addObject("nitDoc", docsDto.stream().filter(d ->d.getCdTipoDoc().equals(TipoDocumentoEnum.NIT.getTipo())).findFirst().orElse(new RegDocDetailsDTO()));
        mv.addObject("nrcDoc", docsDto.stream().filter(d ->d.getCdTipoDoc().equals(TipoDocumentoEnum.NRC.getTipo())).findFirst().orElse(new RegDocDetailsDTO()));
        mv.addObject("sol", dataCompany.getRegSolicitud());
        mv.addObject("dataCompany", dataCompany);
        mv.addObject("telefonosTitular", telefonosTitular);
        mv.setViewName("pages/regCompany/dataCompany_PJ");
        return mv;
    }

    @RequestMapping("/company/rqstPJ2/{skRegEmpresa}")
    public ModelAndView regCompanyRqstPJ2(@PathVariable("skRegEmpresa") Integer skRegEmpresa){
        ModelAndView mv = new ModelAndView();
        RegEmpresa dataCompany = regEmpresaService.findByIdAndSolicitanteAndEstadoCre(skRegEmpresa, SecurityHelper.getLoggedInUserDetails().getUsername());
        if (dataCompany == null || dataCompany.getSkEmpresa() == null) {
            mv.setViewName(REDIRECT_index);
            return mv;
        }
//        List<String> telefonosTitular = obtenerTelsListDeSolicitudByTipoTitular(dataCompany, TitularTelefonoEnum.PROPIETARIO.getCodigo()) ;
        Set<RegDocDetailsDTO> docsDto = regDocDetailsDTORepository.findBySkEmpresa(dataCompany.getSkEmpresa());

        mv.addObject("duiDoc", docsDto.stream().filter(d ->d.getCdTipoDoc().equals(TipoDocumentoEnum.DUI.getTipo())).findFirst().orElse(new RegDocDetailsDTO()));
        mv.addObject("nitDoc", docsDto.stream().filter(d ->d.getCdTipoDoc().equals(TipoDocumentoEnum.NIT.getTipo())).findFirst().orElse(new RegDocDetailsDTO()));
        mv.addObject("creDoc", docsDto.stream().filter(d ->d.getCdTipoDoc().equals(TipoDocumentoEnum.CRED.getTipo())).findFirst().orElse(new RegDocDetailsDTO()));
        mv.addObject("pasDoc", docsDto.stream().filter(d ->d.getCdTipoDoc().equals(TipoDocumentoEnum.PAS.getTipo())).findFirst().orElse(new RegDocDetailsDTO()));
        mv.addObject("sol", dataCompany.getRegSolicitud());
        mv.addObject("dataCompany", dataCompany);
//        mv.addObject("telefonosTitular", telefonosTitular);
        mv.setViewName("pages/regCompany/dataCompany_PJ2");
        return mv;
    }

    @RequestMapping("/company/finanPJ/{skEmpresa}")
    public ModelAndView regCompanyFinanPJ(@PathVariable("skEmpresa") Integer skRegEmpresa) {
        ModelAndView mv = new ModelAndView();
        RegEmpresa dataCompany = regEmpresaService.findByIdAndSolicitanteAndEstadoCre(skRegEmpresa, SecurityHelper.getLoggedInUserDetails().getUsername());
        if (dataCompany == null || dataCompany.getSkEmpresa() == null) {
            mv.setViewName(REDIRECT_index);
            return mv;
        }
        List<String> telefonosEstablecimiento =obtenerTelsListDeSolicitudByTipoTitular(dataCompany, TitularTelefonoEnum.ESTABLECIMIENTO.getCodigo());        Set<RegDocDetailsDTO> docsDto = regDocDetailsDTORepository.findBySkEmpresa(dataCompany.getSkEmpresa());
        mv.addObject("sol", dataCompany.getRegSolicitud());
        mv.addObject("dataCompany", dataCompany);
        mv.addObject("actEco", dataCompany.getGirGiro());
        mv.addObject("telefonosEstablecimiento", telefonosEstablecimiento);
        mv.addObject("arrDoc", docsDto.stream().filter(d ->d.getCdTipoDoc().equals(TipoDocumentoEnum.ARE.getTipo())).findFirst().orElse(new RegDocDetailsDTO()));
        mv.addObject("balDoc", docsDto.stream().filter(d ->d.getCdTipoDoc().equals(TipoDocumentoEnum.BAL.getTipo())).findFirst().orElse(new RegDocDetailsDTO()));
        mv.addObject("invDoc", docsDto.stream().filter(d ->d.getCdTipoDoc().equals(TipoDocumentoEnum.INV.getTipo())).findFirst().orElse(new RegDocDetailsDTO()));

        mv.setViewName("pages/regCompany/dataFinancials_PJ");
        return mv;
    }

    @RequestMapping("/company/credenPJ/{skRegEmpresa}")
    public ModelAndView regCompanyCredenPJ(@PathVariable("skRegEmpresa") Integer skRegEmpresa) {
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

        mv.setViewName("pages/regCompany/dataCredencials_PJ");
        return mv;
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

    @PostMapping(value = {"/company/saveDataPJ"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody ServiceResponse saveDataForCompanyPJ(
            @RequestParam Integer skSolicitud,
            @RequestParam Integer skEmpresa,
            @RequestParam String stRazonSocial,
            @RequestParam String stDireccion,
            @RequestParam String nit,
            @RequestParam String nrc,
            @RequestParam String stEmail,
            @RequestParam String stDireccionCobros,
            @RequestParam(required = false) MultipartFile escFile,
            @RequestParam(required = false) MultipartFile nitFile,
            @RequestParam(required = false) MultipartFile nrcFile
    ) {
        Optional<RegEmpresa> res = regEmpresaService.findById(skEmpresa);
        RegEmpresa provisional = res.get();
        provisional.setStRazonSocial(stRazonSocial);
        provisional.setStDireccion(stDireccion);
        provisional.setNit(nit);
        provisional.setNrc(nrc);
        provisional.setStEmail(stEmail);
        provisional.setStDireccionCobros(stDireccionCobros);
//        ServiceResponse response = regEmpresaService.saveValidated(provisional);
        ServiceResponse response = regEmpresaService.saveValidatedCompanyPJAndAttachment(provisional, escFile, nitFile, nrcFile);

        return response;

    }
    @PostMapping(value = {"/company/saveDataPJ2"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody ServiceResponse saveDataForCompanyPJ2(
            @RequestParam Integer skSolicitud,
            @RequestParam Integer skEmpresa,
            @RequestParam String stNombres,
            @RequestParam String stApellidos,
            @RequestParam Integer genGeneroID,
            @RequestParam Integer extranjero,
            @RequestParam String dui,
            @RequestParam String nit,
            @RequestParam String pasaporte,
            @RequestParam(required = false) MultipartFile duiFile,
            @RequestParam(required = false) MultipartFile nitFile,
            @RequestParam(required = false) MultipartFile creFile,
            @RequestParam(required = false) MultipartFile pasFile

    ) {
        Optional<RegEmpresa> res = regEmpresaService.findById(skEmpresa);
        RegEmpresa provisional = res.get();
        provisional.setStNombres(stNombres);
        provisional.setStApellidos(stApellidos);
        provisional.setGenGenero(genGeneroService.findByGenId(genGeneroID).get());
        provisional.setExtranjero(extranjero);
        provisional.setDui(dui);
        provisional.setNit(nit);
        provisional.setPasaporte(pasaporte);
//        ServiceResponse response = regEmpresaService.saveValidated(provisional);
        ServiceResponse response = regEmpresaService.saveValidatedCompanyPJAndAttachment(provisional, duiFile, nitFile, creFile, pasFile);

        return response;

    }

}
