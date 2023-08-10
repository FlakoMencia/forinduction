package sv.gob.induction.portal.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        if(SecurityHelper.getLoggedInUserDetails() != null) {
            return Optional.of(SecurityHelper.getLoggedInUserDetails().getUsername());
        }
        else return Optional.empty();
    }
}
