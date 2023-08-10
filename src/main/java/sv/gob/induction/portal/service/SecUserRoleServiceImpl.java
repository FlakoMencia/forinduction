package sv.gob.induction.portal.service;

import sv.gob.induction.portal.repository.SecUserRoleRepository;
import sv.gob.induction.portal.domain.SecUserRole;
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
public class SecUserRoleServiceImpl implements SecUserRoleService {

    @Autowired
    private SecUserRoleRepository secUserRoleRepository;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<SecUserRole> findById(Integer skUserRole) {
            return secUserRoleRepository.findById(skUserRole);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SecUserRole> findBySkUserRole(Integer skUserRole) {
            return secUserRoleRepository.findBySkUserRole(skUserRole);
    }

    @Override
    public ServiceResponse saveValidated(SecUserRole secUserRole) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            SecUserRole savedSecUserRole = secUserRoleRepository.save(secUserRole);
            serviceResponse.setMessage(Constants.MSG_GUARDADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(savedSecUserRole);

            return serviceResponse;
    }

    @Override
    public ServiceResponse delete(Integer skUserRole) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            SecUserRole secUserRole = findById(skUserRole)
                    .orElseThrow(() -> new EntidadNoEncontradaException(skUserRole.toString()));
            secUserRoleRepository.delete(secUserRole);

            serviceResponse.setMessage(Constants.MSG_ELIMINADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(null);
            return serviceResponse;
    }

    @Override
    public List<SecUserRole> findAll() {
        return secUserRoleRepository.findAll();
    }

	// metodos para obtener data como lista
    @Override
    @Transactional(readOnly = true)
    public Slice<SecUserRole> getList(Integer page, Integer rows) {
            return secUserRoleRepository.findAll(PageRequest.of(page - 1, rows));
    }
	
    @Override
    @Transactional(readOnly = true)
    public Slice<SecUserRole> getListByQ(String q, Pageable page) {
            return secUserRoleRepository.findBySkUserRoleIgnoreCaseContaining(q, page);
    }
	
    // metodos para DataTable
    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<SecUserRole> findAll(DataTablesInput input) {
            return secUserRoleRepository.findAll(input);
    }

    @Override
    public Optional<SecUserRole> findBySkUserAndSkRol(Integer skUser, Integer skRol) {
        return secUserRoleRepository.findBySkUserAndSkRol(skUser, skRol);
    }
}
