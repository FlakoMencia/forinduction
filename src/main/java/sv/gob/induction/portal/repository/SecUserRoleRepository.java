package sv.gob.induction.portal.repository;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sv.gob.induction.portal.commons.datatables.repository.DataTablesRepository;
import sv.gob.induction.portal.domain.SecUserRole;

//@JaversSpringDataAuditable
public interface SecUserRoleRepository extends DataTablesRepository<SecUserRole, Integer> {
   
	Optional<SecUserRole> findBySkUserRole(Integer skUserRole);
	
	Slice<SecUserRole> findBySkUserRoleIgnoreCaseContaining(String q, Pageable page);

        @Query(value = "select ur from SecUserRole ur join ur.secUser u join ur.secRole r where u.skUser = :skUser and r.skRole = :skRole")
        Optional<SecUserRole> findBySkUserAndSkRol(@Param("skUser") Integer skUser, @Param("skRole") Integer skRole);
}
