package sv.gob.induction.portal.repository;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import sv.gob.induction.portal.commons.datatables.repository.DataTablesRepository;
import sv.gob.induction.portal.domain.SecRole;

//@JaversSpringDataAuditable
public interface SecRoleRepository extends DataTablesRepository<SecRole, Integer> {

    Optional<SecRole> findBySkRole(Integer skRole);

    @Query("select r from SecRole r where r.cdRole = :codigo")
    SecRole findByCodRole(String codigo);

    Slice<SecRole> findBySkRoleIgnoreCaseContaining(String q, Pageable page);
}
