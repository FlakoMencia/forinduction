package sv.gob.induction.portal.service;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import sv.gob.induction.portal.domain.CcPersona;
import sv.gob.induction.portal.domain.RegDocumentoAnexo;
import sv.gob.induction.portal.domain.RegEmpresa;
import sv.gob.induction.portal.domain.RegSolicitud;
import sv.gob.induction.portal.dto.WebServicePersonaDTO;
import sv.gob.induction.portal.repository.*;
import sv.gob.induction.portal.config.SecurityHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sv.gob.induction.portal.enums.EstadoSolicitudEnum;
import sv.gob.induction.portal.enums.TipoDocumentoEnum;
import sv.gob.induction.portal.enums.TipoSolicitudEmun;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import sv.gob.induction.portal.commons.Constants;
import sv.gob.induction.portal.commons.ServiceResponse;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesInput;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesOutput;
import sv.gob.induction.portal.commons.exception.EntidadNoEncontradaException;

import java.io.IOException;
import java.util.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class RegEmpresaServiceImpl implements RegEmpresaService {

    @Autowired
    private RegEmpresaRepository regEmpresaRepository;
    @Autowired
    private CcTipoDocumentoRepository ccTipoDocumentoRepository;
    @Autowired
    private RegDocumentoAnexoRepository regDocumentoAnexoRepository;

    @Autowired
    private CcEstadoSolicitudRepository ccEstadoSolicitudRepository;

    @Autowired
    private CcPersonaService ccPersonaService;

    @Autowired
    private SecUserRepository secUserRepository;

    @Autowired
    private RegSolicitudRepository regSolicitudRepository;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<RegEmpresa> findById(Integer skEmpresa) {
            return regEmpresaRepository.findById(skEmpresa);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RegEmpresa> findBySkEmpresa(Integer skEmpresa) {
            return regEmpresaRepository.findBySkEmpresa(skEmpresa);
    }

    @Override
    public ServiceResponse saveValidated(RegEmpresa regDataEmpresa) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            RegEmpresa savedRegDataEmpresa = regEmpresaRepository.save(regDataEmpresa);
            serviceResponse.setMessage(Constants.MSG_GUARDADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(savedRegDataEmpresa);

            return serviceResponse;
    }

    @Override
    public ServiceResponse delete(Integer skEmpresa) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            RegEmpresa regDataEmpresa = findById(skEmpresa)
                    .orElseThrow(() -> new EntidadNoEncontradaException(skEmpresa.toString()));
            regEmpresaRepository.delete(regDataEmpresa);

            serviceResponse.setMessage(Constants.MSG_ELIMINADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(null);
            return serviceResponse;
    }

    @Override
    public List<RegEmpresa> findAll() {
        return regEmpresaRepository.findAll();
    }

	// metodos para obtener data como lista
    @Override
    @Transactional(readOnly = true)
    public Slice<RegEmpresa> getList(Integer page, Integer rows) {
            return regEmpresaRepository.findAll(PageRequest.of(page - 1, rows));
    }
	
    @Override
    @Transactional(readOnly = true)
    public Slice<RegEmpresa> getListByQ(String q, Pageable page) {
            return regEmpresaRepository.findBySkEmpresaIgnoreCaseContaining(q, page);
    }
	
    // metodos para DataTable
    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<RegEmpresa> findAll(DataTablesInput input) {
            return regEmpresaRepository.findAll(input);
    }

    @Override
    public RegEmpresa findOrCreateRegEmpresaBySolicitud(RegSolicitud sol) {
        List<RegEmpresa> registrosPrevios = regEmpresaRepository.findByRegSolicitud(sol);
        if(registrosPrevios != null && !registrosPrevios.isEmpty()){
            return registrosPrevios.get(0);
        }
        RegEmpresa nuevoRegistroEmpresa = new RegEmpresa();
        nuevoRegistroEmpresa.setRegSolicitud(sol);
        nuevoRegistroEmpresa.setCdAprobado(0);
        nuevoRegistroEmpresa.setCdActuaComo("D");
        nuevoRegistroEmpresa.setCdPropietarioInm(0);
        nuevoRegistroEmpresa.setCdContaFormal(1);
        nuevoRegistroEmpresa = regEmpresaRepository.save(nuevoRegistroEmpresa);
        return nuevoRegistroEmpresa;
    }

    @Override
    public RegEmpresa findOrCreateSolicitudByUserCCCSolicitante(WebServicePersonaDTO personaByCC, String username) {
        List<RegEmpresa> regEmpresaSolicitud = regEmpresaRepository.findByCCCAndEstadoSolicitud(personaByCC.getCcc(), EstadoSolicitudEnum.CREADO.getCodigo()).getContent();
        if (regEmpresaSolicitud != null && !regEmpresaSolicitud.isEmpty()) {
            return regEmpresaSolicitud.get(0);
        }
        RegSolicitud nuevaSolicitud = new RegSolicitud();
        RegEmpresa nuevaEmpresa = new RegEmpresa();

        if (personaByCC != null) { //ASUMIENDO que el objeto datosConCCC trae estos datos
            nuevaSolicitud.setCdTipoSolicitud(TipoSolicitudEmun.EMPRESA.getCodigo());
            nuevaSolicitud.setCcEstadoSolicitud(ccEstadoSolicitudRepository.findByCdEstado(EstadoSolicitudEnum.CREADO.getCodigo()));
            nuevaSolicitud.setCdTipoPersona(personaByCC.getTipoPersona());
            nuevaSolicitud.setFcCreaFecha(new Date());
            nuevaSolicitud.setSecUserSolicita(secUserRepository.findByCodigoUsuario(username).get());
            nuevaSolicitud.setCdAceptaTerminos(1); //Validacion de aceptar terminos o no se realiza antes de este punto
            nuevaSolicitud = regSolicitudRepository.save(nuevaSolicitud);
            nuevaEmpresa.setRegSolicitud(nuevaSolicitud);
            nuevaEmpresa.setCdCcc(personaByCC.getCcc());
            nuevaEmpresa.setStRazonSocial(personaByCC.getRazonSocial());
            nuevaEmpresa.setStNombres(personaByCC.getNombres());
            nuevaEmpresa.setStApellidos(personaByCC.getApellidos());
            nuevaEmpresa.setStDireccion("Sin Direccion "); //fixme no existe campo en emulacion de WB campo direccion
            nuevaEmpresa.setDui(personaByCC.getDui());
            nuevaEmpresa.setNit(personaByCC.getNit());
            nuevaEmpresa.setNrc(personaByCC.getNrc());  // fixme sustituir por campo nrc
            nuevaEmpresa.setPasaporte(personaByCC.getPasaporte());
            nuevaEmpresa.setStEmail("sin@correo.com");  // fixme NO existe campo en emulacion de WB campo direccion
            nuevaEmpresa.setExtranjero(0);  //fixme no existe campo en emulacion de WB campo extranjero
            nuevaEmpresa.setCdAprobado(0); // Default para cuando se crea
            nuevaEmpresa.setCdActuaComo("D"); // Default para cuando se crea
            nuevaEmpresa.setCdPropietarioInm(0);  // Default para cuando se crea
            nuevaEmpresa.setCdContaFormal(1);  // Default para cuando se crea
            nuevaEmpresa.setCdTipoActivo("I");  // Default para cuando se crea
            nuevaEmpresa.setStDireccionCobros(personaByCC.getDireccionCobro());
            nuevaEmpresa = regEmpresaRepository.save(nuevaEmpresa);
        }
        return nuevaEmpresa;
    }

    @Override
    public RegEmpresa findOrCreateRegEmpresaByUserSolicitante(String username) {
        List<RegEmpresa> regEmpresaSolicitud = regEmpresaRepository.findByUserAndEstadoSolicitud(username, EstadoSolicitudEnum.CREADO.getCodigo()).getContent();
        if (regEmpresaSolicitud != null && !regEmpresaSolicitud.isEmpty()) {
            return regEmpresaSolicitud.get(0);
        }
        RegSolicitud nuevaSolicitud = new RegSolicitud();
        RegEmpresa nuevaEmpresa = new RegEmpresa();
        CcPersona per = ccPersonaService.findBySecUser(SecurityHelper.getLoggedInUserDetails().getUsername());

        if (per != null) {
            nuevaSolicitud.setCdTipoSolicitud(TipoSolicitudEmun.EMPRESA.getCodigo());
            nuevaSolicitud.setCcEstadoSolicitud(ccEstadoSolicitudRepository.findByCdEstado(EstadoSolicitudEnum.CREADO.getCodigo()));
            nuevaSolicitud.setCdTipoPersona(per.getCdTipoPersona());
            nuevaSolicitud.setFcCreaFecha(new Date());
            nuevaSolicitud.setSecUserSolicita(secUserRepository.findByCodigoUsuario(username).get());
            nuevaSolicitud.setCdAceptaTerminos(1); //Validacion de aceptar terminos o no se realiza antes de este punto
            nuevaSolicitud = regSolicitudRepository.save(nuevaSolicitud);
            nuevaEmpresa.setRegSolicitud(nuevaSolicitud);
            nuevaEmpresa.setStNombres(per.getStNombre());
//            nuevaEmpresa.setstApellidos(per.getApellidos());
//            nuevaEmpresa.setStDireccion("Sin Direccion "); // No Existe campo em CcPersona
            nuevaEmpresa.setDui(per.getCdDui());
            nuevaEmpresa.setNit(per.getCdNit());
//            nuevaEmpresa.setNrc(per.getCdNrc());  // No Existe campo em CcPersona
            nuevaEmpresa.setPasaporte(per.getCdPasaporte());
//            nuevaEmpresa.setStEmail("sin@correo.com");  // // No Existe campo em CcPersona
            nuevaEmpresa.setExtranjero(0);  // Default para cuando se crea
            nuevaEmpresa.setCdAprobado(0); // Default para cuando se crea
            nuevaEmpresa.setCdActuaComo("D"); // Default para cuando se crea
            nuevaEmpresa.setCdPropietarioInm(0);  // Default para cuando se crea
            nuevaEmpresa.setCdContaFormal(1);  // Default para cuando se crea
            nuevaEmpresa.setCdTipoActivo("I");  // Default para cuando se crea
            nuevaEmpresa = regEmpresaRepository.save(nuevaEmpresa);
        }
        return nuevaEmpresa;
    }

    @Override
    public RegEmpresa findByIdAndSolicitante(Integer skRegEmpresa, String username) {
      return regEmpresaRepository.findBySkEmpresaAndSolicitante(skRegEmpresa, username);
    }

    @Override
    public RegEmpresa findByIdAndSolicitanteAndEstadoCre(Integer skRegEmpresa, String username) {
        return regEmpresaRepository.findBySkEmpresaAndSolicitanteAndEstado(skRegEmpresa, username, EstadoSolicitudEnum.CREADO.getCodigo());
    }

    @Override
    public ServiceResponse saveValidatedCompanyAndAttachment(RegEmpresa company, MultipartFile duiFile, MultipartFile nitFile, MultipartFile nrcFile, MultipartFile pasaFile) {
        ServiceResponse response = saveValidated(company);
        try {
            guardarDocAnexo(company, duiFile , TipoDocumentoEnum.DUI.getTipo());
            guardarDocAnexo(company, nitFile , TipoDocumentoEnum.NIT.getTipo());
            guardarDocAnexo(company, nrcFile , TipoDocumentoEnum.NRC.getTipo());
            guardarDocAnexo(company, pasaFile , TipoDocumentoEnum.PAS.getTipo());
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error en guardar archivo adjunto");
            throw new RuntimeException(e);
        }
        return response;
    }

    @Override
    public ServiceResponse saveValidatedCompanyAndAttachment(RegEmpresa company, MultipartFile arrFile, MultipartFile balFile, MultipartFile invFile) {
        ServiceResponse response = saveValidated(company);
        try {
            guardarDocAnexo(company, arrFile , TipoDocumentoEnum.ARE.getTipo());
            guardarDocAnexo(company, balFile , TipoDocumentoEnum.BAL.getTipo());
            guardarDocAnexo(company, invFile , TipoDocumentoEnum.INV.getTipo());
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error en guardar archivo adjunto");
            throw new RuntimeException(e);
        }
        return response;
    }

    @Override
    public ServiceResponse sendAndValidatedSolWithCredencials(RegEmpresa company, List<MultipartFile> files) {
        company.getRegSolicitud().setCcEstadoSolicitud(ccEstadoSolicitudRepository.findByCdEstado(EstadoSolicitudEnum.ENVIADO.getCodigo()));
        ServiceResponse response = saveValidated(company);

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                try {
                    guardarDocAnexo(company, file, TipoDocumentoEnum.CRED.getTipo());
                } catch (Exception e) {
                    response.setSuccess(false);
                    response.setMessage("Error en guardar archivo adjunto enviando solicitud");
                    throw new RuntimeException(e);
                }
            }
        }
        return response;

    }

    @Override
    public ServiceResponse saveValidatedCompanyPJAndAttachment(RegEmpresa company, MultipartFile escFile, MultipartFile nitFile, MultipartFile nrcFile) {
        ServiceResponse response = saveValidated(company);
        try {
            guardarDocAnexo(company, escFile , TipoDocumentoEnum.ESC.getTipo());
            guardarDocAnexo(company, nitFile , TipoDocumentoEnum.NIT.getTipo());
            guardarDocAnexo(company, nrcFile , TipoDocumentoEnum.NRC.getTipo());
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error en guardar archivo adjunto");
            throw new RuntimeException(e);
        }
        return response;
    }

    @Override
    public ServiceResponse saveValidatedCompanyPJAndAttachment(RegEmpresa company, MultipartFile duiFile, MultipartFile nitFile, MultipartFile creFile, MultipartFile pasFile) {
        ServiceResponse response = saveValidated(company);
        try {
            guardarDocAnexo(company, duiFile , TipoDocumentoEnum.DUI.getTipo());
            guardarDocAnexo(company, nitFile , TipoDocumentoEnum.NIT.getTipo());
            guardarDocAnexo(company, creFile , TipoDocumentoEnum.CRED.getTipo());
            guardarDocAnexo(company, pasFile , TipoDocumentoEnum.PAS.getTipo());
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error en guardar archivo adjunto");
            throw new RuntimeException(e);
        }
        return response;
    }

    private void guardarDocAnexo(RegEmpresa company, MultipartFile file, String tipoDoc) throws IOException {
        if(file != null && !file.isEmpty() && file.getBytes() != null && !file.getOriginalFilename().equals("file_dummie.pdf")){
            RegDocumentoAnexo doc = new RegDocumentoAnexo();
            doc.setCcTipoDocumento(ccTipoDocumentoRepository.findCcTipoDocumentoByCdTipoDoc(tipoDoc));
            doc.setRegEmpresa(company);
            doc.setRegSolicitud(company.getRegSolicitud());
            doc.setStNombreDoc(StringUtils.cleanPath(file.getOriginalFilename()));
            doc.setTypeFile(file.getContentType());
            doc.setByteFile(file.getBytes());
            regDocumentoAnexoRepository.save(doc);
        }

    }
}
