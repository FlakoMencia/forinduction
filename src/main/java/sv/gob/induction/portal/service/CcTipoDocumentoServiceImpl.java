package sv.gob.induction.portal.service;

import sv.gob.induction.portal.repository.CcTipoDocumentoRepository;
import sv.gob.induction.portal.domain.CcTipoDocumento;
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
public class CcTipoDocumentoServiceImpl implements CcTipoDocumentoService {

    @Autowired
    private CcTipoDocumentoRepository ccTipoDocumentoRepository;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<CcTipoDocumento> findById(Integer skTipoDoc) {
            return ccTipoDocumentoRepository.findById(skTipoDoc);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CcTipoDocumento> findBySkTipoDoc(Integer skTipoDoc) {
            return ccTipoDocumentoRepository.findBySkTipoDoc(skTipoDoc);
    }

    @Override
    public ServiceResponse saveValidated(CcTipoDocumento ccTipoDocumento) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            CcTipoDocumento savedCcTipoDocumento = ccTipoDocumentoRepository.save(ccTipoDocumento);
            serviceResponse.setMessage(Constants.MSG_GUARDADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(savedCcTipoDocumento);

            return serviceResponse;
    }

    @Override
    public ServiceResponse delete(Integer skTipoDoc) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            CcTipoDocumento ccTipoDocumento = findById(skTipoDoc)
                    .orElseThrow(() -> new EntidadNoEncontradaException(skTipoDoc.toString()));
            ccTipoDocumentoRepository.delete(ccTipoDocumento);

            serviceResponse.setMessage(Constants.MSG_ELIMINADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(null);
            return serviceResponse;
    }

    @Override
    public List<CcTipoDocumento> findAll() {
        return ccTipoDocumentoRepository.findAll();
    }

	// metodos para obtener data como lista
    @Override
    @Transactional(readOnly = true)
    public Slice<CcTipoDocumento> getList(Integer page, Integer rows) {
            return ccTipoDocumentoRepository.findAll(PageRequest.of(page - 1, rows));
    }
	
    @Override
    @Transactional(readOnly = true)
    public Slice<CcTipoDocumento> getListByQ(String q, Pageable page) {
            return ccTipoDocumentoRepository.findBySkTipoDocIgnoreCaseContaining(q, page);
    }
	
    // metodos para DataTable
    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<CcTipoDocumento> findAll(DataTablesInput input) {
            return ccTipoDocumentoRepository.findAll(input);
    }
}
