package sv.gob.induction.portal.service;

import sv.gob.induction.portal.repository.CcEstadoSolicitudRepository;
import sv.gob.induction.portal.domain.CcEstadoSolicitud;
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
public class CcEstadoSolicitudServiceImpl implements CcEstadoSolicitudService {

    @Autowired
    private CcEstadoSolicitudRepository ccEstadoSolicitudRepository;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<CcEstadoSolicitud> findById(Integer skEstado) {
            return ccEstadoSolicitudRepository.findById(skEstado);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CcEstadoSolicitud> findBySkEstado(Integer skEstado) {
            return ccEstadoSolicitudRepository.findBySkEstado(skEstado);
    }

    @Override
    public ServiceResponse saveValidated(CcEstadoSolicitud ccEstadoSolicitud) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            CcEstadoSolicitud savedCcEstadoSolicitud = ccEstadoSolicitudRepository.save(ccEstadoSolicitud);
            serviceResponse.setMessage(Constants.MSG_GUARDADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(savedCcEstadoSolicitud);

            return serviceResponse;
    }

    @Override
    public ServiceResponse delete(Integer skEstado) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            CcEstadoSolicitud ccEstadoSolicitud = findById(skEstado)
                    .orElseThrow(() -> new EntidadNoEncontradaException(skEstado.toString()));
            ccEstadoSolicitudRepository.delete(ccEstadoSolicitud);

            serviceResponse.setMessage(Constants.MSG_ELIMINADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(null);
            return serviceResponse;
    }

    @Override
    public List<CcEstadoSolicitud> findAll() {
        return ccEstadoSolicitudRepository.findAll();
    }

	// metodos para obtener data como lista
    @Override
    @Transactional(readOnly = true)
    public Slice<CcEstadoSolicitud> getList(Integer page, Integer rows) {
            return ccEstadoSolicitudRepository.findAll(PageRequest.of(page - 1, rows));
    }
	
    @Override
    @Transactional(readOnly = true)
    public Slice<CcEstadoSolicitud> getListByQ(String q, Pageable page) {
            return ccEstadoSolicitudRepository.findBySkEstadoIgnoreCaseContaining(q, page);
    }
	
    // metodos para DataTable
    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<CcEstadoSolicitud> findAll(DataTablesInput input) {
            return ccEstadoSolicitudRepository.findAll(input);
    }
}
