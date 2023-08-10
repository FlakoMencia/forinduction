package sv.gob.induction.portal.repository;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import sv.gob.induction.portal.commons.datatables.repository.DataTablesRepository;
import sv.gob.induction.portal.domain.CcTipoDocumento;

//@JaversSpringDataAuditable
public interface CcTipoDocumentoRepository extends DataTablesRepository<CcTipoDocumento, Integer> {
   
	Optional<CcTipoDocumento> findBySkTipoDoc(Integer skTipoDoc);
	
	Slice<CcTipoDocumento> findBySkTipoDocIgnoreCaseContaining(String q, Pageable page);

	CcTipoDocumento findCcTipoDocumentoByCdTipoDoc(String cdTipoDoc);
}
