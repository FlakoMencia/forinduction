package sv.gob.induction.portal.repository;

import sv.gob.induction.portal.commons.datatables.repository.DataTablesRepository;
import sv.gob.induction.portal.dto.RegDocDetailsDTO;

import java.util.Set;

public interface RegDocDetailsDTORepository  extends DataTablesRepository<RegDocDetailsDTO, Integer > {

    Set<RegDocDetailsDTO> findBySkEmpresa(Integer skEmpresa);
}
