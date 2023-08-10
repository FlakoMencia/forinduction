package sv.gob.induction.portal.repository;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import sv.gob.induction.portal.commons.datatables.repository.DataTablesRepository;
import sv.gob.induction.portal.domain.RegDocumentoAnexo;

//@JaversSpringDataAuditable
public interface RegDocumentoAnexoRepository extends DataTablesRepository<RegDocumentoAnexo, Integer> {
   
	Optional<RegDocumentoAnexo> findBySkDocAnexo(Integer skDocAnexo);
	
	Slice<RegDocumentoAnexo> findBySkDocAnexoIgnoreCaseContaining(String q, Pageable page);
}
