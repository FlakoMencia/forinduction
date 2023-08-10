package sv.gob.induction.portal.service;

import sv.gob.induction.portal.repository.GenGeneroRepository;
import sv.gob.induction.portal.domain.GenGenero;
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
public class GenGeneroServiceImpl implements GenGeneroService {

    @Autowired
    private GenGeneroRepository genGeneroRepository;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<GenGenero> findById(Integer genId) {
            return genGeneroRepository.findById(genId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GenGenero> findByGenId(Integer genId) {
            return genGeneroRepository.findByGenId(genId);
    }

    @Override
    public ServiceResponse saveValidated(GenGenero genGenero) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            GenGenero savedGenGenero = genGeneroRepository.save(genGenero);
            serviceResponse.setMessage(Constants.MSG_GUARDADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(savedGenGenero);

            return serviceResponse;
    }

    @Override
    public ServiceResponse delete(Integer genId) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            GenGenero genGenero = findById(genId)
                    .orElseThrow(() -> new EntidadNoEncontradaException(genId.toString()));
            genGeneroRepository.delete(genGenero);

            serviceResponse.setMessage(Constants.MSG_ELIMINADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(null);
            return serviceResponse;
    }

    @Override
    public List<GenGenero> findAll() {
        return genGeneroRepository.findAll();
    }

	// metodos para obtener data como lista
    @Override
    @Transactional(readOnly = true)
    public Slice<GenGenero> getList(Integer page, Integer rows) {
            return genGeneroRepository.findAll(PageRequest.of(page - 1, rows));
    }
	
    @Override
    @Transactional(readOnly = true)
    public Slice<GenGenero> getListByQ(String q, Pageable page) {
            return genGeneroRepository.findByGenIdIgnoreCaseContaining(q, page);
    }
	
    // metodos para DataTable
    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<GenGenero> findAll(DataTablesInput input) {
            return genGeneroRepository.findAll(input);
    }
}
