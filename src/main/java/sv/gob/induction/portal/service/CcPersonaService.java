package sv.gob.induction.portal.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.gob.induction.portal.commons.Constants;
import sv.gob.induction.portal.commons.ServiceResponse;
import sv.gob.induction.portal.commons.Utils;
import sv.gob.induction.portal.commons.emailsender.model.Mail;
import sv.gob.induction.portal.commons.emailsender.service.EmailSenderService;
import sv.gob.induction.portal.domain.CcPersona;
import sv.gob.induction.portal.domain.SecRole;
import sv.gob.induction.portal.domain.SecUser;
import sv.gob.induction.portal.domain.SecUserRole;
import sv.gob.induction.portal.enums.RoleEmun;
import sv.gob.induction.portal.repository.*;


@Slf4j
@Service
@Transactional
public class CcPersonaService {

    private static final int LONGITUD_HASH = 128;


    private final CcPersonaRepository ccPersonaRepository;


    private final SecUserRepository secUserRepository;


    private final SecUserRoleRepository secUserRoleRepository;


    private final SecRoleRepository secRoleRepository;


    private final PasswordEncoder passwordEncoder;

    private final EmailSenderService emailSenderService;

    public CcPersonaService(CcPersonaRepository ccPersonaRepository, SecUserRepository secUserRepository, SecUserRoleRepository secUserRoleRepository, SecRoleRepository secRoleRepository, PasswordEncoder passwordEncoder, EmailSenderService emailSenderService) {
        this.ccPersonaRepository = ccPersonaRepository;
        this.secUserRepository = secUserRepository;
        this.secUserRoleRepository = secUserRoleRepository;
        this.secRoleRepository = secRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailSenderService = emailSenderService;
    }


    public Optional<CcPersona> findById(Integer skPersona) {
        return ccPersonaRepository.findById(skPersona);
    }

    public ServiceResponse saveValidated(CcPersona persona, String context) {
        ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
        Pattern regexMayuscula = Pattern.compile("[A-Z]");
        Pattern regexNumero = Pattern.compile("[0-9]");
        Pattern regexCaracterEspecial = Pattern.compile("^([0-9]|\\w)+$");
        Pattern regexEmail = Pattern.compile("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$");
        if (!regexEmail.matcher(persona.getCdUserDelegate()).find()) {
            serviceResponse = new ServiceResponse(false, "El correo electrónico debe contener el formato correcto");
            return serviceResponse;
        }
        if (!regexMayuscula.matcher(persona.getStPasswordDelegate()).find() || !regexNumero.matcher(persona.getStPasswordDelegate()).find() || regexCaracterEspecial.matcher(persona.getStPasswordDelegate()).find()) {
            serviceResponse = new ServiceResponse(false, "La contraseña debe contener al menos una letra mayúscula, un número y un carácter especial (! \" # $ % &  ? @)");
            return serviceResponse;
        }
        if (!(persona.getStPasswordDelegate().length() >= 8 && persona.getStPasswordDelegate().length() <= 12)) {
            serviceResponse = new ServiceResponse(false, "La contraseña debe tener un mínimo de 8 y 12 caracteres");
            return serviceResponse;
        }
        if (!(persona.getStPasswordDelegate().equals(persona.getPasswordConfirm()))) {
            serviceResponse = new ServiceResponse(false, "Las contraseñas ingresadas no coinciden. Intente nuevamente");
            return serviceResponse;
        }
        SecUser secUser = new SecUser();
        secUser.setCdUser(persona.getCdUserDelegate());
        secUser.setStPassword(passwordEncoder.encode(persona.getStPasswordDelegate()));
        secUser.setFcCreaFecha(LocalDate.now());
        secUser.setHabilitado(0);
        secUser = secUserRepository.save(secUser);

        persona.setFcCreaFecha(LocalDate.now());
        persona.setFcModFecha(null);
        persona.setSecUser(secUser);
        persona.setStHash(Utils.generarStringAleatorio(LONGITUD_HASH));
        persona = ccPersonaRepository.save(persona);

        SecUserRole userRole = new SecUserRole();
        SecRole rol = secRoleRepository.findByCodRole(RoleEmun.CONTRIBUYENTE.getRol());
        if (rol != null && rol.getSkRole() != null) {
            userRole.setSecUser(secUser);
            userRole.setSecRole(rol);
            secUserRoleRepository.save(userRole);
        }

        ServiceResponse serviceResponseEmail = processSendEmail(persona, context);
        if (!serviceResponseEmail.isSuccess()) {
            return serviceResponseEmail;
        }
        serviceResponse.setMessage(Constants.MSG_GUARDADO_EXITOSO);
        serviceResponse.setSuccess(true);
        serviceResponse.setData(persona);
        return serviceResponse;

    }

    public ServiceResponse sendEmail(CcPersona persona, String context) {
        return processSendEmail(persona, context);
    }

    private ServiceResponse processSendEmail(CcPersona persona, String context) {
        ServiceResponse serviceResponse = new ServiceResponse(false, "Ocurrio un error al intentar enviar el correo... ");
        try {
            String href = context + "/public/regUser/validEmail?email=" + URLEncoder.encode(persona.getCdUserDelegate(), StandardCharsets.UTF_8) + "&nombre=" + URLEncoder.encode(persona.getStNombre(), StandardCharsets.UTF_8) + "&stHash=" + persona.getStHash();
            String nameFile = context + "/images/logo.png";
            String encabezado = "Alcaldia - Correo de registro de usuario";
            String cuerpo = "<center><div style='width:100%;'><img src='" + nameFile + "' width='120' height='120'/></div> "
                    + "<div style='width:100%;'><h3>TEST</h3></center></div> <br/>"
                    + "Estimado(a) Sr(a). " + persona.getStNombre() + ", con NIT " + persona.getCdNit() + ". <br/>"
                    + "Bienvenido/a al sistema  <br/>"
                    + "Su nombre de usuario es " + persona.getCdUserDelegate() + " <br/>"
                    + "Para completar su registro haga clic en el enlace que aparece a continuación: <br/>"
                    + "<a href='" + href + "'>Valide su correo</a> <br/>"
                    + "Saludos cordiales.";
            Mail mail = new Mail(Arrays.asList(persona.getCdUserDelegate()), encabezado, cuerpo);
            emailSenderService.enviarCorreo(mail);
            serviceResponse.setMessage("Correo Electr\u00f3nico enviado exitosamente");
            serviceResponse.setSuccess(true);
            serviceResponse.setData(persona);
            return serviceResponse;
        } catch (Exception e) {
            serviceResponse = new ServiceResponse(false, "Ocurrio un error al intentar enviar el correo... ");
            return serviceResponse;
        }
    }

    public ServiceResponse userStatusChange(String stCorreo, String stHash) {
        Optional<SecUser> optionalSecUser = ccPersonaRepository.findByEmailAndHash(stCorreo, stHash);
        if (!optionalSecUser.isPresent()) {
            return new ServiceResponse(false, "Correo no encontrado. Verifique si el correo ingresado es correcto...");
        }
        SecUser secUser = optionalSecUser.get();
        secUser.setHabilitado(1);
        secUser = secUserRepository.save(secUser);
        return new ServiceResponse(true, "Todo valido", secUser);
    }


    public boolean existsUserAndCcc(String user, String ccc) {
        try {
            Integer cUserCcc = ccPersonaRepository.countRegistersByUserAndCcc(user, ccc);
            return (cUserCcc > 0);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean existsCcc(String ccc){
        Optional<CcPersona> optionalCcPersona = ccPersonaRepository.findByCdCcc(ccc);
        return optionalCcPersona.isPresent();
    }

    public boolean existsEmail(String email){
        Optional<SecUser> optionalSecUser = secUserRepository.findByCodigoUsuario(email);
        return optionalSecUser.isPresent();
    }

    public CcPersona findPersonaByUsername(String user) {
        return ccPersonaRepository.findPersonaByUsername(user);
    }

    public CcPersona findBySecUser(String username) {
        return ccPersonaRepository.findByUser(username);
    }
}
