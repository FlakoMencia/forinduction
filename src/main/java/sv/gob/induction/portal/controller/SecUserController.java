package sv.gob.induction.portal.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import sv.gob.induction.portal.domain.SecUser;
import sv.gob.induction.portal.service.SecUserService;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesInput;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesOutput;
import sv.gob.induction.portal.commons.Constants;
import sv.gob.induction.portal.commons.S2;
import sv.gob.induction.portal.commons.S2Response;
import sv.gob.induction.portal.commons.ServiceResponse;
import sv.gob.induction.portal.commons.ValidadorHttp;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
@RequestMapping("/user/secUser")
public class SecUserController {
	
    private static final String HAS_AUTHORITY_USER = "hasAuthority('')";
    private static final String REDIRECT_SEC_USER = "redirect:/user/secUser/";
    private static final String SEC_USER = "secUser";
	
    @Autowired
    private SecUserService secUserService;
	
    //@PreAuthorize(HAS_AUTHORITY_USER)
    @GetMapping(value = {"/", ""})
    public String indexSecUser() {
            return "pages/secUser/list";
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)	
    @GetMapping("/list")
    public @ResponseBody
    DataTablesOutput<SecUser> listSecUser(@Valid DataTablesInput input) {
        return secUserService.findAll(input);
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)
    @GetMapping("/form")
    public String formSecUser(@RequestParam(required = false) Integer skUser, Model model) {
        if (!model.containsAttribute(SEC_USER)) {
            SecUser secUser = new SecUser();
            if (skUser != null) {
                Optional<SecUser> optSecUser = secUserService.findById(skUser);
                if (!optSecUser.isPresent()) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "SecUser Not Found");
                }
                secUser = optSecUser.get();
            }
            model.addAttribute(SEC_USER, secUser);
        }
        return "pages/secUser/form";
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)
    @PostMapping("/save")
    public String saveEntity(@Valid SecUser secUser
            , @RequestParam Integer skRol
            , BindingResult bdResult
            , RedirectAttributes atts
        ) {
        String redirectTo = Constants.REDIRECT_FORM;
        String[] parametrosAExcluir = new String[]{
            ""
        };
        if (ValidadorHttp.isPeticionCorrectaExcluyendoCampos(bdResult, parametrosAExcluir)) {
            ServiceResponse serviceResponse = secUserService.saveValidatedConRol(secUser, skRol);
            atts.addFlashAttribute(Constants.SERVICE_RESPONSE_NAME, serviceResponse);
            redirectTo = serviceResponse.isSuccess() ? REDIRECT_SEC_USER : redirectTo;
        }
        atts.addFlashAttribute(SEC_USER, secUser);
        atts.addFlashAttribute(BindingResult.class.getCanonicalName() + ".secUser", bdResult);
        return redirectTo;
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)
    @PostMapping(value = {"/delete"})
    public @ResponseBody ServiceResponse deleteEntity(@RequestParam(value="skUser", required = false) Integer skUser){
        ServiceResponse serviceResponse = secUserService.delete(skUser);
        return serviceResponse;
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)
    @GetMapping(value = {"/cboFilterS2"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody
    S2Response<S2> s2(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "rows", required = false) Integer rows) {
        S2Response<S2> response;
        if (rows != 0) {
            Slice<SecUser> list;
            if (q == null || q.equals("")) {
                list = secUserService.getList(page, rows);
            }else{
                list = secUserService.getListByQ(q, PageRequest.of(page - 1, rows));
            }
            List<S2> results = new ArrayList<>();
            list.getContent().stream().map(u -> new S2(u.getSkUser().toString(), u.getSkUser().toString(),null)).forEachOrdered(results::add);
            response = new S2Response<>(results, list.hasNext());
        } else {
            response = new S2Response<>();
        }
        return response;
    }
}
