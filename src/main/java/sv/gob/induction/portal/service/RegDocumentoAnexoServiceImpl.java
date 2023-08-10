package sv.gob.induction.portal.service;

import sv.gob.induction.portal.repository.RegDocumentoAnexoRepository;
import sv.gob.induction.portal.domain.RegDocumentoAnexo;
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
public class RegDocumentoAnexoServiceImpl implements RegDocumentoAnexoService {

    @Autowired
    private RegDocumentoAnexoRepository regDocumentoAnexoRepository;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<RegDocumentoAnexo> findById(Integer skDocAnexo) {
            return regDocumentoAnexoRepository.findById(skDocAnexo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RegDocumentoAnexo> findBySkDocAnexo(Integer skDocAnexo) {
            return regDocumentoAnexoRepository.findBySkDocAnexo(skDocAnexo);
    }

    @Override
    public ServiceResponse saveValidated(RegDocumentoAnexo regDocumentoAnexo) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            RegDocumentoAnexo savedRegDocumentoAnexo = regDocumentoAnexoRepository.save(regDocumentoAnexo);
            serviceResponse.setMessage(Constants.MSG_GUARDADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(savedRegDocumentoAnexo);

            return serviceResponse;
    }

    @Override
    public ServiceResponse delete(Integer skDocAnexo) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            RegDocumentoAnexo regDocumentoAnexo = findById(skDocAnexo)
                    .orElseThrow(() -> new EntidadNoEncontradaException(skDocAnexo.toString()));
            regDocumentoAnexoRepository.delete(regDocumentoAnexo);

            serviceResponse.setMessage(Constants.MSG_ELIMINADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(null);
            return serviceResponse;
    }

    @Override
    public List<RegDocumentoAnexo> findAll() {
        return regDocumentoAnexoRepository.findAll();
    }

	// metodos para obtener data como lista
    @Override
    @Transactional(readOnly = true)
    public Slice<RegDocumentoAnexo> getList(Integer page, Integer rows) {
            return regDocumentoAnexoRepository.findAll(PageRequest.of(page - 1, rows));
    }
	
    @Override
    @Transactional(readOnly = true)
    public Slice<RegDocumentoAnexo> getListByQ(String q, Pageable page) {
            return regDocumentoAnexoRepository.findBySkDocAnexoIgnoreCaseContaining(q, page);
    }
	
    // metodos para DataTable
    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<RegDocumentoAnexo> findAll(DataTablesInput input) {
            return regDocumentoAnexoRepository.findAll(input);
    }
}
