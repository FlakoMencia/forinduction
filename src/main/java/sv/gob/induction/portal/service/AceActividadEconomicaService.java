package sv.gob.induction.portal.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import sv.gob.induction.portal.commons.ServiceResponse;
import sv.gob.induction.portal.domain.AceActividadEconomica;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesInput;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesOutput;

public interface AceActividadEconomicaService {
    Optional<AceActividadEconomica> findById(Integer aceId);
    Optional<AceActividadEconomica> findByAceId(Integer aceId);
    ServiceResponse saveValidated(AceActividadEconomica aceActividadEconomica);
    ServiceResponse delete(Integer aceId);
    List<AceActividadEconomica> findAll();
	
	// metodos para obtener data como lista
    Slice<AceActividadEconomica> getList(Integer page, Integer rows);
    Slice<AceActividadEconomica> getListByQ(String q, Pageable page);
	
    // metodos para DatatTable
    DataTablesOutput<AceActividadEconomica> findAll(DataTablesInput input);
}
