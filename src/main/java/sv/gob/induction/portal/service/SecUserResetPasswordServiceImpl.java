package sv.gob.induction.portal.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import sv.gob.induction.portal.repository.SecUserResetPasswordRepository;
import sv.gob.induction.portal.domain.SecUserResetPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sv.gob.induction.portal.commons.ServiceResponse;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import sv.gob.induction.portal.commons.Utils;
import sv.gob.induction.portal.commons.emailsender.model.Mail;
import sv.gob.induction.portal.commons.emailsender.service.EmailSenderService;
import sv.gob.induction.portal.domain.SecUser;

@Slf4j
@Service
@Transactional
public class SecUserResetPasswordServiceImpl implements SecUserResetPasswordService {
    
    private static final int LONGITUD_HASH = 128;
    private static final int HORAS_LIMITE_RESET = 2;
    
    @Value("${induction.portal.urlBase}")
    private String urlBase;
    
    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private SecUserResetPasswordRepository secUserResetPasswordRepository;
    
    @Autowired
    private SecUserService secUserService;
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<SecUserResetPassword> findById(Integer skUserResetPassword) {
            return secUserResetPasswordRepository.findById(skUserResetPassword);
    }
    
    @Override
    public ServiceResponse crearEntradaResetPassword(String stCorreoRecuperacion) {
        ServiceResponse respuestaUsuarioNoEncontrado = new ServiceResponse(false, "Usuario no encontrado. Verifique el correo ingresado.");
        Optional<SecUser> optionalSecUser = secUserService.findByCodigoUsuario(stCorreoRecuperacion);
        if(optionalSecUser.isPresent()) {
            SecUser usuario = optionalSecUser.get();
            
            // se valida que el usuario este habilitado
            if(!usuario.isHabilitadoLogin()) {
                return respuestaUsuarioNoEncontrado;
            }
            
            // se valida que el usuario tenga llena su tabla de CcPersona, sino dara error el envio de correo
            if(usuario.getCcPersona() == null) {
                return new ServiceResponse(false, "Este usuario no puede recibir un cambio de contraseña "
                        + "debido a que no fue creado de forma regular....");
            }
            
            SecUserResetPassword reset = crearResetPassword(optionalSecUser.get());
            enviarCorreoReset(reset, usuario);
            
            return new ServiceResponse(true, "Si el correo está registrado, el correo de cambio de contraseña fue enviado de forma exitosa. Verifique su correo electrónico.");
        }
        else return respuestaUsuarioNoEncontrado;
    }
    
    private SecUserResetPassword crearResetPassword(SecUser usuario) {
        /** Primero borro todos los reset de password a nombre del usuario actual. 
         *  Esto es para evitar multiples reset de password
         */
        List<SecUserResetPassword> resetsPrevios = secUserResetPasswordRepository.findBySkUser(usuario.getSkUser());
        for(SecUserResetPassword resetPrevio : resetsPrevios) {
            secUserResetPasswordRepository.delete(resetPrevio);
        }
        
        // se crea y guarda el reset del usuario
        SecUserResetPassword reset = new SecUserResetPassword();
        reset.setSecUser(usuario);
        reset.setBnDisponible(true);
        reset.setFcFinValidez(LocalDateTime.now().plusHours(HORAS_LIMITE_RESET));
        reset.setStHash(Utils.generarStringAleatorio(LONGITUD_HASH));
        
        return secUserResetPasswordRepository.save(reset);
    }

    private void enviarCorreoReset(SecUserResetPassword reset, SecUser usuario) {
        String correoUsuario = usuario.getCdUser();
        try {
            String urlDestino = "<a href='" 
                    + urlBase + "/public/usuario/password/reset/ver?stCorreoElectronico="  + URLEncoder.encode(correoUsuario, StandardCharsets.UTF_8.toString()) + "&stHash=" + reset.getStHash() 
                    + "'>Cambiar contraseña</a>";
        
            String encabezado = "Alcaldia - Correo de cambio de contraseña";
            String logo = urlBase + "/images/logo.png";
            String cuerpo = "<center><div style='width:100%;'><img src='" + logo + "' width='120' height='120'/></div> "
                    + "<div style='width:100%;'><h3>test</h3></center></div> <br/>"
                    + "Estimado(a) Sr(a). " 
                    + usuario.getCcPersona().getStNombre()
                    + (usuario.getCcPersona().getStRazonSocial() != null ? "/" + usuario.getCcPersona().getStRazonSocial() : "")
                    + ",<br/><br/> Ha solicitado el restablecimiento de su contraseña desde el portal de "
                    + "la alcaldía, haga clic en el siguiente enlace y siga las instrucciones para realizar el cambio de su contraseña. <br/><br/>"
                    + urlDestino
                    + "<br/><br/>Si no ha solicitado restablecer su contraseña por favor ignore este correo electrónico."
                    + "<br/><br/>Para otras consultas ciudadanas, puede acercarse a las oficinaslos horarios de atención establecidos"
                    + ", de lunes a viernes de 7:00 am a 4:00 pm.";
            Mail mail = new Mail(Arrays.asList(correoUsuario), encabezado, cuerpo);
            emailSenderService.enviarCorreo(mail);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ocurrio un error al intentar enviar el correo... ", e);
        }
    }

    @Override
    public ServiceResponse ejecutarResetPassword(String stCorreoElectronico, String stHash, String password) {
        ServiceResponse validacion = this.isResetPasswordValido(stCorreoElectronico, stHash);
        if(validacion.isSuccess()) {
            // valido el password enviado
            if(!Utils.isPasswordValido(password)) {
                return new ServiceResponse(false, "El password no cumple con el estandar de seguridad establecido. Intente nuevamente.");
            }
            
            // seteo el password del usuario
            SecUserResetPassword reset = (SecUserResetPassword) validacion.getData();
            SecUser secUser = reset.getSecUser();
            
            // valido que el password nuevo no sea igual al seteado previamente
            if(secUserService.isPasswordEquivalente(password, secUser.getStPassword())) {
                return new ServiceResponse(false, "La nueva contraseña no debe coincidir con la anterior. Intente nuevamente.", true);
            }
            
            secUser.setStPassword(password);

            secUserService.saveValidated(secUser);

            // elimino el reset de password
            secUserResetPasswordRepository.delete(reset);

            return new ServiceResponse(true, "Password cambiado de forma exitosa");
        }
        
        return validacion;
    }

    @Override
    public ServiceResponse isResetPasswordValido(String stCorreoElectronico, String stHash) {
        Optional<SecUserResetPassword> optionalReset = secUserResetPasswordRepository.findByStCorreoAndHash(stCorreoElectronico, stHash);
        if(!optionalReset.isPresent()){
            return new ServiceResponse(false, "Registo no encontrado. Verifique si la información ingresada es correcta...");
        }
        
        SecUserResetPassword reset = optionalReset.get();
        
        // se valida que el reset se intente en el periodo establecido
        if(ChronoUnit.HOURS.between(LocalDateTime.now(), reset.getFcFinValidez()) > HORAS_LIMITE_RESET) {
            return new ServiceResponse(false, "Cambio de contraseña ha expirado. Intente nuevamente...");
        }
        
        return new ServiceResponse(true, "Todo valido", reset);
    }
}
