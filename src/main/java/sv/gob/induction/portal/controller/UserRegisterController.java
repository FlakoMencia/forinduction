/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.gob.induction.portal.controller;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sv.gob.induction.portal.commons.Constants;
import sv.gob.induction.portal.commons.S2;
import sv.gob.induction.portal.commons.ServiceResponse;
import sv.gob.induction.portal.domain.CcPersona;
import sv.gob.induction.portal.enums.TipoDocumentoEnum;
import sv.gob.induction.portal.service.CcPersonaService;


@Slf4j
@Controller
@RequestMapping("/public/regUser")
public class UserRegisterController implements Serializable {

    private static final String CC_PERSONA = "ccPersona";

    private static final String REDIRECT_TIPO_PERSONA = "redirect:/public/regUser/tipoPersona";

    private static final String REDIRECT_TERMINOS = "redirect:/public/regUser/terminos";

    private static final String REDIRECT_FORM = "redirect:/public/regUser/form";

    private static final String REDIRECT_FORM_SUCCESS = "redirect:/public/regUser/formSuccess";

    @Value("${induction.portal.recaptcha2.token}")
    private String recaptcha2Token;


    private final CcPersonaService ccPersonaService;

    public UserRegisterController(CcPersonaService ccPersonaService) {
        this.ccPersonaService = ccPersonaService;
    }

    @GetMapping("/tipoPersona")
    public String tipoPersona(Model model) {
        CcPersona persona = new CcPersona();
        model.addAttribute("ccPersona", persona);
        return "pages/public/registroUsuario/tipoPersona";
    }

    @GetMapping("/terminos")
    public String terminosUsuario(@RequestParam(value = "cdTipoPersona", required = false) String cdTipoPersona, Model model) {
        if (!model.containsAttribute(CC_PERSONA)) {
            CcPersona persona = new CcPersona();
            persona.setCdTipoPersona(cdTipoPersona);
            model.addAttribute("ccPersona", persona);
        }
        return "pages/public/registroUsuario/terminos";
    }

    @PostMapping("/elegirTipoPersona")
    public String elegirTipoPersona(
            @Valid CcPersona persona,
            BindingResult bResult,
            RedirectAttributes attsRedirect) {
        attsRedirect.addFlashAttribute(CC_PERSONA, persona);
        attsRedirect.addFlashAttribute(BindingResult.class.getCanonicalName() + ".ccPersona", bResult);
        if (StringUtils.isBlank(persona.getCdTipoPersona())) {
            ServiceResponse serviceResponse = new ServiceResponse(false, "Debe seleccionar un tipo de persona, antes de continuar.");
            attsRedirect.addFlashAttribute(Constants.SERVICE_RESPONSE_NAME, serviceResponse);
            return REDIRECT_TIPO_PERSONA;
        }
        attsRedirect.addAttribute("cdTipoPersona", persona.getCdTipoPersona());
        return REDIRECT_TERMINOS;
    }

    @PostMapping("/aceptarTerminos")
    public String aceptarTerminos(
            @Valid CcPersona persona,
            BindingResult bResult,
            RedirectAttributes attsRedirect) {
        attsRedirect.addFlashAttribute(CC_PERSONA, persona);
        attsRedirect.addFlashAttribute(BindingResult.class.getCanonicalName() + ".ccPersona", bResult);
        if (StringUtils.isBlank(persona.getAceptarCondicion())) {
            ServiceResponse serviceResponse = new ServiceResponse(false, "Debe aceptar las condiciones, antes de continuar.");
            attsRedirect.addFlashAttribute(Constants.SERVICE_RESPONSE_NAME, serviceResponse);
            return REDIRECT_TERMINOS;
        }
        attsRedirect.addAttribute("cdTipoPersona", persona.getCdTipoPersona());
        return REDIRECT_FORM;
    }

    @GetMapping("/form")
    public String formUsuario(@RequestParam(value = "cdTipoPersona", required = false) String cdTipoPersona, Model model) {
        if (!model.containsAttribute(CC_PERSONA)) {
            CcPersona ccPersona = new CcPersona();
            ccPersona.setCdTipoPersona(cdTipoPersona);
            model.addAttribute("ccPersona", ccPersona);
        }
        model.addAttribute("recaptcha2Token", recaptcha2Token);
        return "pages/public/registroUsuario/form";
    }

    @PostMapping("/save")
    public String saveUsuario(@Valid CcPersona persona,
            BindingResult bResult,
            RedirectAttributes attsRedirect,
                              HttpServletRequest request) {
        attsRedirect.addFlashAttribute(CC_PERSONA, persona);
        attsRedirect.addFlashAttribute(BindingResult.class.getCanonicalName() + ".ccPersona", bResult);
        ServiceResponse serviceResponse = ccPersonaService.saveValidated(persona, request.getRequestURL().toString().replace(request.getServletPath(), ""));
        attsRedirect.addFlashAttribute(Constants.SERVICE_RESPONSE_NAME, serviceResponse);
        attsRedirect.addAttribute("skPersona", persona.getSkPersona());
        if (serviceResponse.isSuccess()) {
            return REDIRECT_FORM_SUCCESS;
        }
        return REDIRECT_FORM;
    }

    @GetMapping("/formSuccess")
    public String formSuccess(@RequestParam(value = "skPersona", required = false) Integer skPersona, Model model) {
        if (!model.containsAttribute(CC_PERSONA)) {
            Optional<CcPersona> persona = ccPersonaService.findById(skPersona);
            model.addAttribute("ccPersona", (persona.isPresent()) ? persona.get() : new CcPersona());
        }
        return "pages/public/registroUsuario/form_success";
    }

    @PostMapping("/sendMail")
    public String sendMail(@Valid CcPersona pPersona,
            BindingResult bResult,
            RedirectAttributes attsRedirect,HttpServletRequest request)  {
        Optional<CcPersona> persona = ccPersonaService.findById(pPersona.getSkPersona());
        pPersona = persona.orElseGet(CcPersona::new);
        ServiceResponse serviceResponse = ccPersonaService.sendEmail(pPersona, request.getContextPath());
        attsRedirect.addFlashAttribute(Constants.SERVICE_RESPONSE_NAME, serviceResponse);
        attsRedirect.addFlashAttribute(CC_PERSONA, pPersona);
        attsRedirect.addFlashAttribute(BindingResult.class.getCanonicalName() + ".ccPersona", bResult);
        attsRedirect.addAttribute("skPersona", pPersona.getSkPersona());

        return REDIRECT_FORM_SUCCESS;
    }

    @GetMapping("/validEmail")
    public String verResetPassword(@RequestParam String email,
            @RequestParam String nombre,
            @RequestParam String stHash,
            Model modelo,
            RedirectAttributes redirect
    ) {
        ServiceResponse respuesta = ccPersonaService.userStatusChange(email, stHash);
        if (respuesta.isSuccess()) {
            modelo.addAttribute("msjConfirmacionCorreo", "Estimado(a) Sr(a). " + nombre + ", muchas gracias por validar el correo " + email + ". \n "
                    + "De esta forma ha completado el proceso de registro.");
            return "pages/public/registroUsuario/result_validation_email";
        } else {
            modelo.addAttribute("msjErrorCorreo", "Estimado(a) Sr(a). " + nombre + ", no se pudo validar el correo " + email + ". \n "
                    + "Acerquese a la alcaldia para modificar la información.");
            return "pages/public/registroUsuario/result_validation_email";
        }
    }

    @GetMapping(value = {"/listTipoDocumentos"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody
    List<S2> tiposDocumentos() {
        return Arrays.asList(TipoDocumentoEnum.values()).stream().map(item -> {
            S2 s2 = new S2();
            s2.setId(item.getTipo());
            s2.setText(item.getDescripcion());
            return s2;
        }).toList();
    }


}
