package sv.gob.induction.portal.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sv.gob.induction.portal.commons.datatables.repository.DataTablesRepository;
import sv.gob.induction.portal.domain.SecUserResetPassword;

//@JaversSpringDataAuditable
public interface SecUserResetPasswordRepository extends DataTablesRepository<SecUserResetPassword, Integer> {
   
	Optional<SecUserResetPassword> findBySkUserResetPassword(Integer skUserResetPassword);
	
	Slice<SecUserResetPassword> findBySkUserResetPasswordIgnoreCaseContaining(String q, Pageable page);

        @Query(value = "select r from SecUserResetPassword r join r.secUser u where u.skUser = :skUser")
        List<SecUserResetPassword> findBySkUser(@Param("skUser") Integer skUser);

        @Query(value = "select r from SecUserResetPassword r join r.secUser u where u.cdUser = :stCorreoElectronico and r.stHash = :stHash")
        Optional<SecUserResetPassword> findByStCorreoAndHash(@Param("stCorreoElectronico") String stCorreoElectronico, @Param("stHash") String stHash);
        
}
