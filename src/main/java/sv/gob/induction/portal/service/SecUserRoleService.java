package sv.gob.induction.portal.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import sv.gob.induction.portal.commons.ServiceResponse;
import sv.gob.induction.portal.domain.SecUserRole;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesInput;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesOutput;

public interface SecUserRoleService {
    Optional<SecUserRole> findById(Integer skUserRole);
    Optional<SecUserRole> findBySkUserRole(Integer skUserRole);
    ServiceResponse saveValidated(SecUserRole secUserRole);
    ServiceResponse delete(Integer skUserRole);
    List<SecUserRole> findAll();
	
	// metodos para obtener data como lista
    Slice<SecUserRole> getList(Integer page, Integer rows);
    Slice<SecUserRole> getListByQ(String q, Pageable page);
	
    // metodos para DatatTable
    DataTablesOutput<SecUserRole> findAll(DataTablesInput input);

    Optional<SecUserRole> findBySkUserAndSkRol(Integer skUser, Integer skRol);
}
