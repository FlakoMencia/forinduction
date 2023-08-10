package sv.gob.induction.portal.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sv.gob.induction.portal.commons.ServiceResponse;
import sv.gob.induction.portal.service.SecUserResetPasswordService;

@Controller
@Slf4j
@RequestMapping("/public/usuario")
public class SecUserResetPasswordController {
    
    @Autowired
    private SecUserResetPasswordService secUserResetPasswordService;
    
    @PostMapping("/password/reset/crear")
    public @ResponseBody ServiceResponse crearResetPassword(@RequestParam String stCorreoRecuperacion) {
        try {
            return secUserResetPasswordService.crearEntradaResetPassword(stCorreoRecuperacion);
        } catch (Exception e) {
            String mensajeError = "Ocurrio un error al intentar enviar el correo de cambio de contraseña";
            log.error(mensajeError, e);
            return new ServiceResponse(false, mensajeError);
        }
    }
    
    @GetMapping("/password/reset/ver")
    public String verResetPassword(@RequestParam String stCorreoElectronico
            , @RequestParam String stHash
            , @RequestParam(required = false) String error
            , @RequestParam(required = false, defaultValue = "false") boolean inline
            , Model modelo
            , RedirectAttributes redirect
    ) {
        ServiceResponse respuesta = secUserResetPasswordService.isResetPasswordValido(stCorreoElectronico, stHash);
        if(respuesta.isSuccess()) {
            modelo.addAttribute("stCorreoElectronico", stCorreoElectronico);
            modelo.addAttribute("stHash", stHash);
            modelo.addAttribute("error", error);
            modelo.addAttribute("inline", inline);

            return "pages/public/resetPassword";
        }
        else {
            redirect.addAttribute("mensaje", respuesta.getMessage());
            
            return "redirect:/public/usuario/password/reset/error";
        }
    }
    
    @PostMapping("/password/reset/ejecutar")
    public String ejecutarResetPassword(@RequestParam String stCorreoElectronico, @RequestParam String stHash
            , @RequestParam String password, RedirectAttributes redirect) {
        ServiceResponse respuesta = secUserResetPasswordService.ejecutarResetPassword(stCorreoElectronico, stHash, password);
        if(!respuesta.isSuccess()) {
            redirect.addAttribute("stCorreoElectronico", stCorreoElectronico);
            redirect.addAttribute("stHash", stHash);
            redirect.addAttribute("error", respuesta.getMessage());
            redirect.addAttribute("inline", respuesta.getData() != null ?  respuesta.getData() : false);
            
            return "redirect:/public/usuario/password/reset/ver";
        }
        else {
            redirect.addAttribute("mensaje", "Cambio de contraseña realizado de forma exitosa.");
            
            return "redirect:/public/usuario/password/reset/exito";
        }
    }
    
    @GetMapping("/password/reset/exito")
    public String mostrarExitoCambioPassword(Model modelo) {
        modelo.addAttribute("mensaje", "La contraseña fue cambiada exitosamente. Será redirigido al inicio de sesión.");
        return "pages/public/exitoResetPassword";
    }
    
    @GetMapping("/password/reset/error")
    public String mostrarErrorCambioPassword(@RequestParam String mensaje, Model modelo) {
        modelo.addAttribute("mensaje", mensaje);
        return "pages/public/exitoResetPassword";
    }
}
