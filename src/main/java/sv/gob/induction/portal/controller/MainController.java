package sv.gob.induction.portal.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import sv.gob.induction.portal.config.SecurityHelper;

import java.util.List;

@Slf4j
@Controller
public class MainController {
    
        @Value("${induction.portal.recaptcha2.token}")
        private String recaptcha2Token;

    private static final String CODE_ADMIN_ROLE = "Administrador";
    private static final String CODE_CONTRIBUYENTE_ROLE = "Contribuyente";
    private static final String SEC_USER_ROLE = "secUserRole";

        @RequestMapping({"", "/", "/index"})
	public String index() {
            return "redirect:/public/index";
	}
        
        @RequestMapping("/public/index")
	public ModelAndView indexPublico() {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("pages/public/index");
            return mav;
	}
        
        @RequestMapping("/user/login")
        public String loginUser(Model modelo) {
            modelo.addAttribute("recaptcha2Token", recaptcha2Token);
            return "commons/login";
        }

    //@PreAuthorize(HAS_AUTHORITY_USER)
	@RequestMapping("/user/index")
	public ModelAndView indexUser() {
            ModelAndView mav = new ModelAndView();
            String user = SecurityHelper.getLoggedInUserDetails().getUsername();
            List<String> roles = SecurityHelper.getLoggedInUserDetails().getRoles();
            boolean isAdmin = (!roles.isEmpty() && roles.contains(CODE_ADMIN_ROLE));
            boolean isContribuyente = (!roles.isEmpty() && roles.contains(CODE_CONTRIBUYENTE_ROLE));
            boolean hasCompanyRegistered = false ; //Parece que si le van a dejar registrar mas empresas
            mav.addObject("userLogged", user);
            mav.addObject("isAdminRole" , isAdmin);
            mav.addObject("hasCompanyRegistered" , hasCompanyRegistered);
            mav.addObject("isContribuyenteRole" , isContribuyente);
            mav.setViewName(isAdmin? "commons/index" : "pages/contribuyenteInicio/index");
            return mav;
	}


}
