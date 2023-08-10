package sv.gob.induction.portal.repository;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import sv.gob.induction.portal.commons.datatables.repository.DataTablesRepository;
import sv.gob.induction.portal.domain.AceActividadEconomica;


public interface AceActividadEconomicaRepository extends DataTablesRepository<AceActividadEconomica, Integer> {
   
	Optional<AceActividadEconomica> findByAceId(Integer aceId);
	
	Slice<AceActividadEconomica> findByAceIdIgnoreCaseContaining(String q, Pageable page);

	Slice<AceActividadEconomica> findByAceNombreContainingIgnoreCase(String q, Pageable page);
}
