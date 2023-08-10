package sv.gob.induction.portal.service;

import sv.gob.induction.portal.repository.AceActividadEconomicaRepository;
import sv.gob.induction.portal.domain.AceActividadEconomica;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import sv.gob.induction.portal.commons.Constants;
import sv.gob.induction.portal.commons.ServiceResponse;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesInput;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesOutput;
import sv.gob.induction.portal.commons.exception.EntidadNoEncontradaException;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class AceActividadEconomicaServiceImpl implements AceActividadEconomicaService {

    @Autowired
    private AceActividadEconomicaRepository aceActividadEconomicaRepository;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<AceActividadEconomica> findById(Integer aceId) {
            return aceActividadEconomicaRepository.findById(aceId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AceActividadEconomica> findByAceId(Integer aceId) {
            return aceActividadEconomicaRepository.findByAceId(aceId);
    }

    @Override
    public ServiceResponse saveValidated(AceActividadEconomica aceActividadEconomica) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            AceActividadEconomica savedAceActividadEconomica = aceActividadEconomicaRepository.save(aceActividadEconomica);
            serviceResponse.setMessage(Constants.MSG_GUARDADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(savedAceActividadEconomica);

            return serviceResponse;
    }

    @Override
    public ServiceResponse delete(Integer aceId) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            AceActividadEconomica aceActividadEconomica = findById(aceId)
                    .orElseThrow(() -> new EntidadNoEncontradaException(aceId.toString()));
            aceActividadEconomicaRepository.delete(aceActividadEconomica);

            serviceResponse.setMessage(Constants.MSG_ELIMINADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(null);
            return serviceResponse;
    }

    @Override
    public List<AceActividadEconomica> findAll() {
        return aceActividadEconomicaRepository.findAll();
    }

	// metodos para obtener data como lista
    @Override
    @Transactional(readOnly = true)
    public Slice<AceActividadEconomica> getList(Integer page, Integer rows) {
            return aceActividadEconomicaRepository.findAll(PageRequest.of(page - 1, rows));
    }
	
    @Override
    @Transactional(readOnly = true)
    public Slice<AceActividadEconomica> getListByQ(String q, Pageable page) {
            return aceActividadEconomicaRepository.findByAceNombreContainingIgnoreCase(q, page);
    }
	
    // metodos para DataTable
    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<AceActividadEconomica> findAll(DataTablesInput input) {
            return aceActividadEconomicaRepository.findAll(input);
    }
}
