package sv.gob.induction.portal.service;

import sv.gob.induction.portal.repository.SecRoleRepository;
import sv.gob.induction.portal.domain.SecRole;
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
public class SecRoleServiceImpl implements SecRoleService {

    @Autowired
    private SecRoleRepository secRoleRepository;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<SecRole> findById(Integer skRole) {
            return secRoleRepository.findById(skRole);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SecRole> findBySkRole(Integer skRole) {
            return secRoleRepository.findBySkRole(skRole);
    }

    @Override
    public ServiceResponse saveValidated(SecRole secRole) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            SecRole savedSecRole = secRoleRepository.save(secRole);
            serviceResponse.setMessage(Constants.MSG_GUARDADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(savedSecRole);

            return serviceResponse;
    }

    @Override
    public ServiceResponse delete(Integer skRole) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            SecRole secRole = findById(skRole)
                    .orElseThrow(() -> new EntidadNoEncontradaException(skRole.toString()));
            secRoleRepository.delete(secRole);

            serviceResponse.setMessage(Constants.MSG_ELIMINADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(null);
            return serviceResponse;
    }

    @Override
    public List<SecRole> findAll() {
        return secRoleRepository.findAll();
    }

	// metodos para obtener data como lista
    @Override
    @Transactional(readOnly = true)
    public Slice<SecRole> getList(Integer page, Integer rows) {
            return secRoleRepository.findAll(PageRequest.of(page - 1, rows));
    }
	
    @Override
    @Transactional(readOnly = true)
    public Slice<SecRole> getListByQ(String q, Pageable page) {
            return secRoleRepository.findBySkRoleIgnoreCaseContaining(q, page);
    }
	
    // metodos para DataTable
    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<SecRole> findAll(DataTablesInput input) {
            return secRoleRepository.findAll(input);
    }
}
