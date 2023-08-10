package sv.gob.induction.portal.repository;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sv.gob.induction.portal.commons.datatables.repository.DataTablesRepository;
import sv.gob.induction.portal.domain.RegSolicitud;

//@JaversSpringDataAuditable
public interface RegSolicitudRepository extends DataTablesRepository<RegSolicitud, Integer> {
   
	Optional<RegSolicitud> findBySkSolicitud(Integer skSolicitud);
	
	Slice<RegSolicitud> findBySkSolicitudIgnoreCaseContaining(String q, Pageable page);

	@Query(value = "SELECT x "
			+ " FROM RegSolicitud x "
			+ " WHERE "
			+ " x.secUserSolicita.cdUser = :cdUser "
			+ " AND x.ccEstadoSolicitud.cdEstado = :cdEstado"
	)
	Slice<RegSolicitud> findByUsernameAndEstadoSolicitud(@Param("cdUser") String username, @Param("cdEstado") String cdEstado);

	@Query(value = "SELECT x.regSolicitud "
			+ " FROM RegEmpresa x "
			+ " WHERE "
			+ " x.cdCcc = :cdCcc "
			+ " AND x.regSolicitud.ccEstadoSolicitud.cdEstado = :cdEstado"
	)
	Slice<RegSolicitud> findByCCCAndEstadoSolicitud(@Param("cdCcc") String cdCcc, @Param("cdEstado") String cdEstado);

	@Query(value = "SELECT x.cdAceptaTerminos "
			+ " FROM RegSolicitud x "
			+ " WHERE "
			+ " x.secUserSolicita.cdUser = :cdUser "
			+ " AND x.ccEstadoSolicitud.cdEstado = :cdEstado "
			+ " ORDER BY x.skSolicitud "
	)
	Integer findFirstByUsernameAndEstadoSolicitud(@Param("cdUser") String username, @Param("cdEstado") String cdEstado)	;
}
