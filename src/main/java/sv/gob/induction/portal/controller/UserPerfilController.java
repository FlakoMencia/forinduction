package sv.gob.induction.portal.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import sv.gob.induction.portal.config.SecurityHelper;
import sv.gob.induction.portal.domain.CcPersona;
import sv.gob.induction.portal.service.CcPersonaService;

@Slf4j
@Controller
@RequestMapping("/user/perfil")
public class UserPerfilController {

    @Autowired
    private CcPersonaService ccPersonaService;

    @RequestMapping("/perfilPersona")
    public ModelAndView perfilPersona() {
        ModelAndView mv = new ModelAndView();
        CcPersona ccPersona = ccPersonaService.findBySecUser(SecurityHelper.getLoggedInUserDetails().getUsername());
        mv.addObject("ccPersona", ccPersona == null? (new CcPersona()): ccPersona );
        mv.setViewName("pages/contribuyenteInicio/perfil");
        return mv;
    }


}
