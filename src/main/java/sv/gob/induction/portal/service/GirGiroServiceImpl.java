package sv.gob.induction.portal.service;

import sv.gob.induction.portal.repository.GirGiroRepository;
import sv.gob.induction.portal.domain.GirGiro;
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
public class GirGiroServiceImpl implements GirGiroService {

    @Autowired
    private GirGiroRepository girGiroRepository;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<GirGiro> findById(Integer girId) {
            return girGiroRepository.findById(girId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GirGiro> findByGirId(Integer girId) {
            return girGiroRepository.findByGirId(girId);
    }

    @Override
    public ServiceResponse saveValidated(GirGiro girGiro) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            GirGiro savedGirGiro = girGiroRepository.save(girGiro);
            serviceResponse.setMessage(Constants.MSG_GUARDADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(savedGirGiro);

            return serviceResponse;
    }

    @Override
    public ServiceResponse delete(Integer girId) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            GirGiro girGiro = findById(girId)
                    .orElseThrow(() -> new EntidadNoEncontradaException(girId.toString()));
            girGiroRepository.delete(girGiro);

            serviceResponse.setMessage(Constants.MSG_ELIMINADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(null);
            return serviceResponse;
    }

    @Override
    public List<GirGiro> findAll() {
        return girGiroRepository.findAll();
    }

	// metodos para obtener data como lista
    @Override
    @Transactional(readOnly = true)
    public Slice<GirGiro> getList(Integer page, Integer rows) {
            return girGiroRepository.findAll(PageRequest.of(page - 1, rows));
    }
	
    @Override
    @Transactional(readOnly = true)
    public Slice<GirGiro> getListByQ(String q, Pageable page) {
            return girGiroRepository.findByGirNombreIgnoreCaseContaining(q, page);
    }
	
    // metodos para DataTable
    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<GirGiro> findAll(DataTablesInput input) {
            return girGiroRepository.findAll(input);
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<GirGiro> getListByParent(Integer idParent, Pageable page) {
        return girGiroRepository.findByAceActividadEconomica_AceId(idParent, page );
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<GirGiro> getListByParentAndContainigOnGirNombre(Integer idParent,String q, Pageable page) {
        return girGiroRepository.findByAceActividadEconomica_AceIdAndGirNombreContainingIgnoreCase(idParent, q, page );
    }
}
