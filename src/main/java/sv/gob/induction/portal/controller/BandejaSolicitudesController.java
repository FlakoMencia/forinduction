package sv.gob.induction.portal.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bandeja")
public class BandejaSolicitudesController {

    @GetMapping({"/", ""})
    public String index(Authentication auth, Model model) {
        if (auth != null && auth.isAuthenticated()) {
            return "pages/bandejas/index";
        }
        return "pages/login";
    }
}
