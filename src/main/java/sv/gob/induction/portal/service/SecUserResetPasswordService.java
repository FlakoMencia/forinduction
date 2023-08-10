package sv.gob.induction.portal.service;

import java.util.Optional;
import sv.gob.induction.portal.commons.ServiceResponse;
import sv.gob.induction.portal.domain.SecUserResetPassword;

public interface SecUserResetPasswordService {
    Optional<SecUserResetPassword> findById(Integer skUserResetPassword);
    ServiceResponse crearEntradaResetPassword(String stCorreoRecuperacion);

    ServiceResponse ejecutarResetPassword(String stCorreoElectronico, String stHash, String password);

    ServiceResponse isResetPasswordValido(String stCorreoElectronico, String stHash);
}
