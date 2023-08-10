package sv.gob.induction.portal.service;

import sv.gob.induction.portal.repository.SecUserRepository;
import sv.gob.induction.portal.domain.SecUser;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import sv.gob.induction.portal.domain.SecUserRole;
import sv.gob.induction.portal.dto.RoleDTO;

@Slf4j
@Service
@Transactional
public class SecUserServiceImpl implements SecUserService {


    private final SecUserRepository secUserRepository;
    private final SecUserRoleService secUserRoleService;

    private final PasswordEncoder passwordEncoder;

    public SecUserServiceImpl(SecUserRepository secUserRepository, SecUserRoleService secUserRoleService, PasswordEncoder passwordEncoder) {
        this.secUserRepository = secUserRepository;
        this.secUserRoleService = secUserRoleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<SecUser> findById(Integer skUser) {
            return secUserRepository.findById(skUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SecUser> findBySkUser(Integer skUser) {
            return secUserRepository.findBySkUser(skUser);
    }

    @Override
    public ServiceResponse saveValidated(SecUser secUser) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            secUser.setStPassword(passwordEncoder.encode(secUser.getStPassword()));
            SecUser savedSecUser = secUserRepository.save(secUser);
            serviceResponse.setMessage(Constants.MSG_GUARDADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(savedSecUser);

            return serviceResponse;
    }
    
    @Override
    public ServiceResponse saveValidatedConRol(SecUser secUser, Integer skRol) {
        ServiceResponse respuestaGuardadoRegular = saveValidated(secUser);
        if(respuestaGuardadoRegular.isSuccess()) {
            SecUser user = (SecUser) respuestaGuardadoRegular.getData();
            
            // si no esta ese usuario rol, se crea
            Optional<SecUserRole> optionalUserRole = secUserRoleService.findBySkUserAndSkRol(user.getSkUser(), skRol);
            if(!optionalUserRole.isPresent()) {
                SecUserRole userRole = new SecUserRole();
                userRole.setSecUser(secUser);
                userRole.setSecRoleIdDelegate(skRol);
                
                secUserRoleService.saveValidated(userRole);
            }
        }
        return respuestaGuardadoRegular;
    }

    @Override
    public ServiceResponse delete(Integer skUser) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            SecUser secUser = findById(skUser)
                    .orElseThrow(() -> new EntidadNoEncontradaException(skUser.toString()));
            secUserRepository.delete(secUser);

            serviceResponse.setMessage(Constants.MSG_ELIMINADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(null);
            return serviceResponse;
    }

    @Override
    public List<SecUser> findAll() {
        return secUserRepository.findAll();
    }

	// metodos para obtener data como lista
    @Override
    @Transactional(readOnly = true)
    public Slice<SecUser> getList(Integer page, Integer rows) {
            return secUserRepository.findAll(PageRequest.of(page - 1, rows));
    }
	
    @Override
    @Transactional(readOnly = true)
    public Slice<SecUser> getListByQ(String q, Pageable page) {
            return secUserRepository.findBySkUserIgnoreCaseContaining(q, page);
    }
	
    // metodos para DataTable
    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<SecUser> findAll(DataTablesInput input) {
            return secUserRepository.findAll(input);
    }

    @Override
    public Optional<SecUser> findByCodigoUsuario(String userName) {
        return secUserRepository.findByCodigoUsuario(userName);
    }

    @Override
    public List<RoleDTO> getRolesDeUsuario(String userName) {
        return secUserRepository.getRolesDeUsuario(userName);
    }

    @Override
    public boolean isPasswordEquivalente(String password, String hashViejoPassword) {
        return passwordEncoder.matches(password, hashViejoPassword);
    }

}
