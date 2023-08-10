package sv.gob.induction.portal.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import sv.gob.induction.portal.commons.ServiceResponse;
import sv.gob.induction.portal.domain.RegSolicitud;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesInput;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesOutput;
import sv.gob.induction.portal.dto.WebServicePersonaDTO;

public interface RegSolicitudService {
    Optional<RegSolicitud> findById(Integer skSolicitud);
    Optional<RegSolicitud> findBySkSolicitud(Integer skSolicitud);
    ServiceResponse saveValidated(RegSolicitud regSolicitud);
    ServiceResponse delete(Integer skSolicitud);
    List<RegSolicitud> findAll();
	
	// metodos para obtener data como lista
    Slice<RegSolicitud> getList(Integer page, Integer rows);
    Slice<RegSolicitud> getListByQ(String q, Pageable page);
	
    // metodos para DatatTable
    DataTablesOutput<RegSolicitud> findAll(DataTablesInput input);


    Integer aceptoTerminosOnThisSol(String username);

    RegSolicitud findOrCreateSolicitudByUserSolicitante(String username);

    RegSolicitud findOrCreateSolicitudByUserCCCSolicitante(String username, WebServicePersonaDTO personaByCC);
}
