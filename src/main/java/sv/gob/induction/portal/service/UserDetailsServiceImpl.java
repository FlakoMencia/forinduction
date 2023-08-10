package sv.gob.induction.portal.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sv.gob.induction.portal.repository.SecUserRepository;
import sv.gob.induction.portal.config.SecurityUserDetails;
import sv.gob.induction.portal.domain.SecUser;

import java.util.Optional;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SecUserRepository secUserRepository;

    public UserDetailsServiceImpl(SecUserRepository secUserRepository) {
        this.secUserRepository = secUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<SecUser> userOpt = secUserRepository.findByCodigoUsuario(username);

        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException(username + " Not Found");
        }
        
        SecUser user = userOpt.get();
        
        // si el usuario no esta habilitado no ingresa al sistema
        if(!user.isHabilitadoLogin()) {
            throw new UsernameNotFoundException(username + " Not Found");
        }

        SecurityUserDetails securityUser = new SecurityUserDetails();
        securityUser.setCuser(user.getCdUser());
        securityUser.setPassword(user.getStPassword());


        return securityUser;

    }
}
