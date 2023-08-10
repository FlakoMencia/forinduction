package sv.gob.induction.portal.repository;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import sv.gob.induction.portal.commons.datatables.repository.DataTablesRepository;
import sv.gob.induction.portal.domain.GirGiro;


public interface GirGiroRepository extends DataTablesRepository<GirGiro, Integer> {
   
	Optional<GirGiro> findByGirId(Integer girId);
	
	Slice<GirGiro> findByGirIdIgnoreCaseContaining(String q, Pageable page);

	Slice<GirGiro> findByAceActividadEconomica_AceId(Integer AceActividadEconomicaAceId, Pageable page);

	Slice<GirGiro> findByGirNombreIgnoreCaseContaining(String q, Pageable page);

	Slice<GirGiro> findByAceActividadEconomica_AceIdAndGirNombreContainingIgnoreCase(Integer id, String q, Pageable page);
}
