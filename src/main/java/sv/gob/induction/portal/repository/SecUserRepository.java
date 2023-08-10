package sv.gob.induction.portal.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sv.gob.induction.portal.commons.datatables.repository.DataTablesRepository;
import sv.gob.induction.portal.domain.SecUser;
import sv.gob.induction.portal.dto.RoleDTO;

//@JaversSpringDataAuditable
public interface SecUserRepository extends DataTablesRepository<SecUser, Integer> {
   
	Optional<SecUser> findBySkUser(Integer skUser);
	
	Slice<SecUser> findBySkUserIgnoreCaseContaining(String q, Pageable page);
        
        @Query(value = "select u from SecUser u where u.cdUser = :codigoUsuario")
        Optional<SecUser> findByCodigoUsuario(@Param("codigoUsuario") String userName);
        
        @Query(value = "select r.cd_role as nombreRol\n" +
                        "	, r.st_descripcion as descripcionRol \n" +
                        "from sec_user 		u \n" +
                        "join sec_user_role ur on (ur.fk_user = u.sk_user) \n" +
                        "join sec_role 		r on (r.sk_role = ur.fk_role)\n" +
                        "where u.cd_user = :codigoUsuario"
                , nativeQuery = true
        )
        List<RoleDTO> getRolesDeUsuario(@Param("codigoUsuario") String userName);
}
