package sv.gob.induction.portal.repository;

import java.util.List;

import sv.gob.induction.portal.commons.datatables.repository.DataTablesRepository;
import sv.gob.induction.portal.dto.VwUserRole;

public interface VwUserRoleRepository extends DataTablesRepository<VwUserRole, Integer> {
    List<VwUserRole> findByCuser(String cuser);
}
