package sv.gob.induction.portal.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import sv.gob.induction.portal.commons.ServiceResponse;
import sv.gob.induction.portal.domain.CcTipoDocumento;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesInput;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesOutput;

public interface CcTipoDocumentoService {
    Optional<CcTipoDocumento> findById(Integer skTipoDoc);
    Optional<CcTipoDocumento> findBySkTipoDoc(Integer skTipoDoc);
    ServiceResponse saveValidated(CcTipoDocumento ccTipoDocumento);
    ServiceResponse delete(Integer skTipoDoc);
    List<CcTipoDocumento> findAll();
	
	// metodos para obtener data como lista
    Slice<CcTipoDocumento> getList(Integer page, Integer rows);
    Slice<CcTipoDocumento> getListByQ(String q, Pageable page);
	
    // metodos para DatatTable
    DataTablesOutput<CcTipoDocumento> findAll(DataTablesInput input);
}
