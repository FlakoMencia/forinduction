package sv.gob.induction.portal.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sv.gob.induction.portal.commons.datatables.repository.DataTablesRepository;
import sv.gob.induction.portal.domain.CcPersona;
import sv.gob.induction.portal.domain.SecUser;


public interface CcPersonaRepository extends DataTablesRepository<CcPersona, Integer> {

    @Query(value = "select u from CcPersona r join r.secUser u where u.cdUser = :stCorreo and r.stHash = :stHash")
    Optional<SecUser> findByEmailAndHash(@Param("stCorreo") String stCorreo, @Param("stHash") String stHash);

    @Query(value = "select count(*) from CcPersona p inner join p.secUser u where p.cdCcc = :ccc and u.cdUser = :user")
    Integer countRegistersByUserAndCcc(@Param("user") String user, @Param("ccc") String ccc);

    Optional<CcPersona> findByCdCcc(String cdCcc);

    @Query(value = "select p from CcPersona p inner join p.secUser u where u.cdUser = :user")
    CcPersona findPersonaByUsername(@Param("user") String user);

    @Query(value = "SELECT x"
            + " FROM CcPersona x "
            + " WHERE "
            + " x.secUser.cdUser = :username "
    //            + " AND x.secUser.habilitado = 1 "  // Se consulto y desean ver deshabilitados tambien
    )
    CcPersona findByUser(@Param("username") String username);
}
