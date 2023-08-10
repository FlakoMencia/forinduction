package sv.gob.induction.portal.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import sv.gob.induction.portal.commons.ServiceResponse;
import sv.gob.induction.portal.domain.CcTelefonoTitular;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesInput;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesOutput;

public interface CcTelefonoTitularService {
    Optional<CcTelefonoTitular> findById(Integer skTelTitular);
    Optional<CcTelefonoTitular> findBySkTelTitular(Integer skTelTitular);
    ServiceResponse saveValidated(CcTelefonoTitular ccTelefonoTitular);
    ServiceResponse delete(Integer skTelTitular);
    List<CcTelefonoTitular> findAll();
	
	// metodos para obtener data como lista
    Slice<CcTelefonoTitular> getList(Integer page, Integer rows);
    Slice<CcTelefonoTitular> getListByQ(String q, Pageable page);
	
    // metodos para DatatTable
    DataTablesOutput<CcTelefonoTitular> findAll(DataTablesInput input);
}
