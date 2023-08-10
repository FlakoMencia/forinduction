package sv.gob.induction.portal.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import sv.gob.induction.portal.commons.ServiceResponse;
import sv.gob.induction.portal.domain.SecRole;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesInput;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesOutput;

public interface SecRoleService {
    Optional<SecRole> findById(Integer skRole);
    Optional<SecRole> findBySkRole(Integer skRole);
    ServiceResponse saveValidated(SecRole secRole);
    ServiceResponse delete(Integer skRole);
    List<SecRole> findAll();
	
	// metodos para obtener data como lista
    Slice<SecRole> getList(Integer page, Integer rows);
    Slice<SecRole> getListByQ(String q, Pageable page);
	
    // metodos para DatatTable
    DataTablesOutput<SecRole> findAll(DataTablesInput input);
}
