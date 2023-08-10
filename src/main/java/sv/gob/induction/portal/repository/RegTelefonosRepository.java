package sv.gob.induction.portal.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sv.gob.induction.portal.commons.datatables.repository.DataTablesRepository;
import sv.gob.induction.portal.domain.RegTelefonos;

//@JaversSpringDataAuditable
public interface RegTelefonosRepository extends DataTablesRepository<RegTelefonos, Integer> {
   
	Optional<RegTelefonos> findBySkTel(Integer skTel);
	
	Slice<RegTelefonos> findBySkTelIgnoreCaseContaining(String q, Pageable page);
	@Query(value = "SELECT x "
			+ " FROM RegTelefonos x "
			+ " WHERE "
			+ " x.regEmpresa.skEmpresa = :skRegEmpresa "
			+ " AND x.ccTelefonoTitular.cdTelefonoTitular =  :tipoTitular"
	)
    List<RegTelefonos> findBySkEmpresaAndTipoTitular(@Param("skRegEmpresa")Integer skRegEmpresa, @Param("tipoTitular")String tipoTitular);
}
