package sv.gob.induction.portal.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import sv.gob.induction.portal.commons.ServiceResponse;
import sv.gob.induction.portal.domain.RegDocumentoAnexo;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesInput;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesOutput;

public interface RegDocumentoAnexoService {
    Optional<RegDocumentoAnexo> findById(Integer skDocAnexo);
    Optional<RegDocumentoAnexo> findBySkDocAnexo(Integer skDocAnexo);
    ServiceResponse saveValidated(RegDocumentoAnexo regDocumentoAnexo);
    ServiceResponse delete(Integer skDocAnexo);
    List<RegDocumentoAnexo> findAll();
	
	// metodos para obtener data como lista
    Slice<RegDocumentoAnexo> getList(Integer page, Integer rows);
    Slice<RegDocumentoAnexo> getListByQ(String q, Pageable page);
	
    // metodos para DatatTable
    DataTablesOutput<RegDocumentoAnexo> findAll(DataTablesInput input);
}
