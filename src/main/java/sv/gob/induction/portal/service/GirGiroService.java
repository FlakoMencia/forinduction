package sv.gob.induction.portal.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import sv.gob.induction.portal.commons.ServiceResponse;
import sv.gob.induction.portal.domain.GirGiro;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesInput;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesOutput;

public interface GirGiroService {
    Optional<GirGiro> findById(Integer girId);
    Optional<GirGiro> findByGirId(Integer girId);
    ServiceResponse saveValidated(GirGiro girGiro);
    ServiceResponse delete(Integer girId);
    List<GirGiro> findAll();
	
	// metodos para obtener data como lista
    Slice<GirGiro> getList(Integer page, Integer rows);
    Slice<GirGiro> getListByQ(String q, Pageable page);
	
    // metodos para DatatTable
    DataTablesOutput<GirGiro> findAll(DataTablesInput input);

    Slice<GirGiro> getListByParent(Integer idParent, Pageable page);

    Slice<GirGiro>  getListByParentAndContainigOnGirNombre(Integer idParent,String q, Pageable page);
}
