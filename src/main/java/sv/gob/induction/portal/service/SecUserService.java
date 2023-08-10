package sv.gob.induction.portal.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import sv.gob.induction.portal.commons.ServiceResponse;
import sv.gob.induction.portal.domain.SecUser;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesInput;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesOutput;
import sv.gob.induction.portal.dto.RoleDTO;

public interface SecUserService {
    Optional<SecUser> findById(Integer skUser);
    Optional<SecUser> findBySkUser(Integer skUser);
    ServiceResponse saveValidated(SecUser secUser);
    ServiceResponse delete(Integer skUser);
    List<SecUser> findAll();
	
	// metodos para obtener data como lista
    Slice<SecUser> getList(Integer page, Integer rows);
    Slice<SecUser> getListByQ(String q, Pageable page);
	
    // metodos para DatatTable
    DataTablesOutput<SecUser> findAll(DataTablesInput input);

    Optional<SecUser> findByCodigoUsuario(String userName);

    List<RoleDTO> getRolesDeUsuario(String userName);

    public ServiceResponse saveValidatedConRol(SecUser secUser, Integer skRol);

    boolean isPasswordEquivalente(String password, String hashViejoPassword);
}
