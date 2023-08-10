package sv.gob.induction.portal.service;

import sv.gob.induction.portal.repository.CcTelefonoTitularRepository;
import sv.gob.induction.portal.domain.CcTelefonoTitular;
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
public class CcTelefonoTitularServiceImpl implements CcTelefonoTitularService {

    @Autowired
    private CcTelefonoTitularRepository ccTelefonoTitularRepository;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<CcTelefonoTitular> findById(Integer skTelTitular) {
            return ccTelefonoTitularRepository.findById(skTelTitular);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CcTelefonoTitular> findBySkTelTitular(Integer skTelTitular) {
            return ccTelefonoTitularRepository.findBySkTelTitular(skTelTitular);
    }

    @Override
    public ServiceResponse saveValidated(CcTelefonoTitular ccTelefonoTitular) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            CcTelefonoTitular savedCcTelefonoTitular = ccTelefonoTitularRepository.save(ccTelefonoTitular);
            serviceResponse.setMessage(Constants.MSG_GUARDADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(savedCcTelefonoTitular);

            return serviceResponse;
    }

    @Override
    public ServiceResponse delete(Integer skTelTitular) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            CcTelefonoTitular ccTelefonoTitular = findById(skTelTitular)
                    .orElseThrow(() -> new EntidadNoEncontradaException(skTelTitular.toString()));
            ccTelefonoTitularRepository.delete(ccTelefonoTitular);

            serviceResponse.setMessage(Constants.MSG_ELIMINADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(null);
            return serviceResponse;
    }

    @Override
    public List<CcTelefonoTitular> findAll() {
        return ccTelefonoTitularRepository.findAll();
    }

	// metodos para obtener data como lista
    @Override
    @Transactional(readOnly = true)
    public Slice<CcTelefonoTitular> getList(Integer page, Integer rows) {
            return ccTelefonoTitularRepository.findAll(PageRequest.of(page - 1, rows));
    }
	
    @Override
    @Transactional(readOnly = true)
    public Slice<CcTelefonoTitular> getListByQ(String q, Pageable page) {
            return ccTelefonoTitularRepository.findBySkTelTitularIgnoreCaseContaining(q, page);
    }
	
    // metodos para DataTable
    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<CcTelefonoTitular> findAll(DataTablesInput input) {
            return ccTelefonoTitularRepository.findAll(input);
    }
}
