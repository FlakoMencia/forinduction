package sv.gob.induction.portal.repository;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import sv.gob.induction.portal.commons.datatables.repository.DataTablesRepository;
import sv.gob.induction.portal.domain.CcTelefonoTitular;

//@JaversSpringDataAuditable
public interface CcTelefonoTitularRepository extends DataTablesRepository<CcTelefonoTitular, Integer> {
   
	Optional<CcTelefonoTitular> findBySkTelTitular(Integer skTelTitular);
	
	Slice<CcTelefonoTitular> findBySkTelTitularIgnoreCaseContaining(String q, Pageable page);

	CcTelefonoTitular findByCdTelefonoTitular(String cdTelefonoTitular);
}
