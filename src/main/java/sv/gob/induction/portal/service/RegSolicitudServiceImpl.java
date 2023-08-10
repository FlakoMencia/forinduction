package sv.gob.induction.portal.service;

import sv.gob.induction.portal.domain.CcPersona;
import sv.gob.induction.portal.domain.RegEmpresa;
import sv.gob.induction.portal.domain.RegSolicitud;
import sv.gob.induction.portal.dto.WebServicePersonaDTO;
import sv.gob.induction.portal.repository.CcEstadoSolicitudRepository;
import sv.gob.induction.portal.repository.RegSolicitudRepository;
import sv.gob.induction.portal.repository.SecUserRepository;
import sv.gob.induction.portal.config.SecurityHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sv.gob.induction.portal.enums.EstadoSolicitudEnum;
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

import java.util.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class RegSolicitudServiceImpl implements RegSolicitudService {

    @Autowired
    private RegSolicitudRepository regSolicitudRepository;

    @Autowired
    private CcPersonaService ccPersonaService;

    @Autowired
    private CcEstadoSolicitudRepository ccEstadoSolicitudRepository;

    @Autowired
    private SecUserRepository secUserRepository;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<RegSolicitud> findById(Integer skSolicitud) {
            return regSolicitudRepository.findById(skSolicitud);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RegSolicitud> findBySkSolicitud(Integer skSolicitud) {
            return regSolicitudRepository.findBySkSolicitud(skSolicitud);
    }

    @Override
    public ServiceResponse saveValidated(RegSolicitud regSolicitud) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            RegSolicitud savedRegSolicitud = regSolicitudRepository.save(regSolicitud);
            serviceResponse.setMessage(Constants.MSG_GUARDADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(savedRegSolicitud);

            return serviceResponse;
    }

    @Override
    public ServiceResponse delete(Integer skSolicitud) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            RegSolicitud regSolicitud = findById(skSolicitud)
                    .orElseThrow(() -> new EntidadNoEncontradaException(skSolicitud.toString()));
            regSolicitudRepository.delete(regSolicitud);

            serviceResponse.setMessage(Constants.MSG_ELIMINADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(null);
            return serviceResponse;
    }

    @Override
    public List<RegSolicitud> findAll() {
        return regSolicitudRepository.findAll();
    }

	// metodos para obtener data como lista
    @Override
    @Transactional(readOnly = true)
    public Slice<RegSolicitud> getList(Integer page, Integer rows) {
            return regSolicitudRepository.findAll(PageRequest.of(page - 1, rows));
    }
	
    @Override
    @Transactional(readOnly = true)
    public Slice<RegSolicitud> getListByQ(String q, Pageable page) {
            return regSolicitudRepository.findBySkSolicitudIgnoreCaseContaining(q, page);
    }
	
    // metodos para DataTable
    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<RegSolicitud> findAll(DataTablesInput input) {
            return regSolicitudRepository.findAll(input);
    }

    @Override
    public Integer aceptoTerminosOnThisSol(String username) {
        Integer ret = regSolicitudRepository.findFirstByUsernameAndEstadoSolicitud(username,  EstadoSolicitudEnum.CREADO.getCodigo());
        return (ret == null ? 0 : ret);
    }


    @Override
    public RegSolicitud findOrCreateSolicitudByUserSolicitante(String username) {
        List<RegSolicitud> solicitudes = regSolicitudRepository.findByUsernameAndEstadoSolicitud(username , EstadoSolicitudEnum.CREADO.getCodigo()).getContent();
        if(solicitudes != null && !solicitudes.isEmpty()){
            return solicitudes.get(0);
        }
        RegSolicitud nuevaSolicitud = new RegSolicitud();
        CcPersona per = ccPersonaService.findBySecUser(SecurityHelper.getLoggedInUserDetails().getUsername());
        if (per != null) {
            nuevaSolicitud.setCdTipoSolicitud(TipoSolicitudEmun.EMPRESA.getCodigo());
            nuevaSolicitud.setCcEstadoSolicitud(ccEstadoSolicitudRepository.findByCdEstado(EstadoSolicitudEnum.CREADO.getCodigo()));
            nuevaSolicitud.setCdTipoPersona(per.getCdTipoPersona());
            nuevaSolicitud.setFcCreaFecha(new Date());
            nuevaSolicitud.setSecUserSolicita(per.getSecUser());
            nuevaSolicitud= regSolicitudRepository.save(nuevaSolicitud);
        }
        return nuevaSolicitud;
    }

    @Override
    public RegSolicitud findOrCreateSolicitudByUserCCCSolicitante(String username, WebServicePersonaDTO personaByCC) { //Fixme
//        personaByCC <---  ASUMIENDO que el objeto trae estos datos necesarios
        List<RegSolicitud> solicitudes = regSolicitudRepository.findByCCCAndEstadoSolicitud(personaByCC.getCcc() , EstadoSolicitudEnum.CREADO.getCodigo()).getContent();
        if(solicitudes != null && !solicitudes.isEmpty()){
            return solicitudes.get(0);
        }
        RegSolicitud nuevaSolicitud = new RegSolicitud();
        RegEmpresa nuevaEmpresa = new RegEmpresa();

        if (personaByCC != null) { //ASUMIENDO que el objeto datosConCCC trae estos datos
            nuevaSolicitud.setCdTipoSolicitud(TipoSolicitudEmun.EMPRESA.getCodigo());
            nuevaSolicitud.setCcEstadoSolicitud(ccEstadoSolicitudRepository.findByCdEstado(EstadoSolicitudEnum.CREADO.getCodigo()));
            nuevaSolicitud.setCdTipoPersona(personaByCC.getTipoPersona());
            nuevaSolicitud.setFcCreaFecha(new Date());
            nuevaSolicitud.setSecUserSolicita(secUserRepository.findByCodigoUsuario(username).get());
            nuevaEmpresa.setCdCcc(personaByCC.getCcc());
            nuevaEmpresa.setStNombres(personaByCC.getNombres());
            nuevaEmpresa.setStApellidos(personaByCC.getApellidos());
            nuevaEmpresa.setStDireccion("Sin Direccion "); //fixme no existe campo en emulacion de WB campo direccion
//            nuevaEmpresa.setExtranjero(personaByCC.get);  //fixme no existe campo en emulacion de WB campo extranjero
            nuevaEmpresa.setDui(personaByCC.getDui());
            nuevaEmpresa.setNit(personaByCC.getNit());
            nuevaEmpresa.setNrc(personaByCC.getNrc());  // fixme sustituir por campo nrc
            nuevaEmpresa.setPasaporte(personaByCC.getPasaporte());
            nuevaEmpresa.setStEmail("sin@correo.com");  // fixme NO existe campo en emulacion de WB campo direccion
            nuevaEmpresa.setStDireccionCobros(personaByCC.getDireccionCobro());
            Set<RegEmpresa> empresas = new HashSet<>();
            empresas.add(nuevaEmpresa);
            nuevaSolicitud.setRegEmpresas(empresas);
            nuevaSolicitud= regSolicitudRepository.save(nuevaSolicitud);
        }
        return nuevaSolicitud;
    }
}
