package sv.gob.induction.portal.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import sv.gob.induction.portal.commons.ServiceResponse;
import sv.gob.induction.portal.domain.GenGenero;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesInput;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesOutput;

public interface GenGeneroService {
    Optional<GenGenero> findById(Integer genId);
    Optional<GenGenero> findByGenId(Integer genId);
    ServiceResponse saveValidated(GenGenero genGenero);
    ServiceResponse delete(Integer genId);
    List<GenGenero> findAll();
	
	// metodos para obtener data como lista
    Slice<GenGenero> getList(Integer page, Integer rows);
    Slice<GenGenero> getListByQ(String q, Pageable page);
	
    // metodos para DatatTable
    DataTablesOutput<GenGenero> findAll(DataTablesInput input);
}
