package sv.gob.induction.portal.repository;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import sv.gob.induction.portal.commons.datatables.repository.DataTablesRepository;
import sv.gob.induction.portal.domain.GenGenero;


public interface GenGeneroRepository extends DataTablesRepository<GenGenero, Integer> {
   
	Optional<GenGenero> findByGenId(Integer genId);
	
	Slice<GenGenero> findByGenIdIgnoreCaseContaining(String q, Pageable page);
}
