package sv.gob.induction.portal.repository;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import sv.gob.induction.portal.commons.datatables.repository.DataTablesRepository;
import sv.gob.induction.portal.domain.CcEstadoSolicitud;

//@JaversSpringDataAuditable
public interface CcEstadoSolicitudRepository extends DataTablesRepository<CcEstadoSolicitud, Integer> {
   
	Optional<CcEstadoSolicitud> findBySkEstado(Integer skEstado);
	
	Slice<CcEstadoSolicitud> findBySkEstadoIgnoreCaseContaining(String q, Pageable page);

    CcEstadoSolicitud findByCdEstado(String cre);
}
