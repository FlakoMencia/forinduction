package sv.gob.induction.portal.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import sv.gob.induction.portal.commons.ServiceResponse;
import sv.gob.induction.portal.domain.CcEstadoSolicitud;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesInput;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesOutput;

public interface CcEstadoSolicitudService {
    Optional<CcEstadoSolicitud> findById(Integer skEstado);
    Optional<CcEstadoSolicitud> findBySkEstado(Integer skEstado);
    ServiceResponse saveValidated(CcEstadoSolicitud ccEstadoSolicitud);
    ServiceResponse delete(Integer skEstado);
    List<CcEstadoSolicitud> findAll();
	
	// metodos para obtener data como lista
    Slice<CcEstadoSolicitud> getList(Integer page, Integer rows);
    Slice<CcEstadoSolicitud> getListByQ(String q, Pageable page);
	
    // metodos para DatatTable
    DataTablesOutput<CcEstadoSolicitud> findAll(DataTablesInput input);
}
