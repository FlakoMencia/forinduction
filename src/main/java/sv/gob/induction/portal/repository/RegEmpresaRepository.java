package sv.gob.induction.portal.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sv.gob.induction.portal.commons.datatables.repository.DataTablesRepository;
import sv.gob.induction.portal.domain.RegEmpresa;
import sv.gob.induction.portal.domain.RegSolicitud;

//@JaversSpringDataAuditable
public interface RegEmpresaRepository extends DataTablesRepository<RegEmpresa, Integer> {
   
	Optional<RegEmpresa> findBySkEmpresa(Integer skEmpresa);
	
	Slice<RegEmpresa> findBySkEmpresaIgnoreCaseContaining(String q, Pageable page);

    List<RegEmpresa> findByRegSolicitud(RegSolicitud sol);

	@Query(value = "SELECT x "
			+ " FROM RegEmpresa x "
			+ " WHERE "
			+ " x.skEmpresa = :skRegEmpresa "
			+ " AND x.regSolicitud.secUserSolicita.cdUser = :cdUser")
	RegEmpresa findBySkEmpresaAndSolicitante(@Param("skRegEmpresa") Integer skRegEmpresa, @Param("cdUser") String username);

	@Query(value = "SELECT x "
			+ " FROM RegEmpresa x "
			+ " WHERE "
			+ " x.skEmpresa = :skRegEmpresa "
			+ " AND x.regSolicitud.secUserSolicita.cdUser = :cdUser"
			+ " AND x.regSolicitud.ccEstadoSolicitud.cdEstado = :cdEstado"
	)
	RegEmpresa findBySkEmpresaAndSolicitanteAndEstado(@Param("skRegEmpresa") Integer skRegEmpresa,
													  @Param("cdUser") String username,
													  @Param("cdEstado") String cdEstado
	);


	@Query(value = "SELECT x "
			+ " FROM RegEmpresa x "
			+ " WHERE "
			+ " x.regSolicitud.secUserSolicita.cdUser = :cdUser"
			+ " AND x.regSolicitud.ccEstadoSolicitud.cdEstado = :cdEstado"
	)
	Slice<RegEmpresa> findByUserAndEstadoSolicitud(@Param("cdUser") String cdCcc, @Param("cdEstado") String cdEstado);


	@Query(value = "SELECT x "
			+ " FROM RegEmpresa x "
			+ " WHERE "
			+ " x.cdCcc = :cdCcc "
			+ " AND x.regSolicitud.ccEstadoSolicitud.cdEstado = :cdEstado"
	)
	Slice<RegEmpresa> findByCCCAndEstadoSolicitud(@Param("cdCcc") String cdCcc, @Param("cdEstado") String cdEstado);
}
