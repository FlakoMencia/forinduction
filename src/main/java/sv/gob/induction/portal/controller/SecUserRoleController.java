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

import sv.gob.induction.portal.domain.SecUserRole;
import sv.gob.induction.portal.service.SecUserRoleService;
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
@RequestMapping("/user/secUserRole")
public class SecUserRoleController {
	
    private static final String HAS_AUTHORITY_USER = "hasAuthority('')";
    private static final String REDIRECT_SEC_USER_ROLE = "redirect:/user/secUserRole/";
    private static final String SEC_USER_ROLE = "secUserRole";
	
    @Autowired
    private SecUserRoleService secUserRoleService;
	
    //@PreAuthorize(HAS_AUTHORITY_USER)
    @GetMapping(value = {"/", ""})
    public String indexSecUserRole() {
            return "pages/secUserRole/list";
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)	
    @GetMapping("/list")
    public @ResponseBody
    DataTablesOutput<SecUserRole> listSecUserRole(@Valid DataTablesInput input) {
        return secUserRoleService.findAll(input);
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)
    @GetMapping("/form")
    public String formSecUserRole(@RequestParam(required = false) Integer skUserRole, Model model) {
        if (!model.containsAttribute(SEC_USER_ROLE)) {
            SecUserRole secUserRole = new SecUserRole();
            if (skUserRole != null) {
                Optional<SecUserRole> optSecUserRole = secUserRoleService.findById(skUserRole);
                if (!optSecUserRole.isPresent()) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "SecUserRole Not Found");
                }
                secUserRole = optSecUserRole.get();
            }
            model.addAttribute(SEC_USER_ROLE, secUserRole);
        }
        return "pages/secUserRole/form";
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)
    @PostMapping("/save")
    public String saveEntity(@Valid SecUserRole secUserRole
            , BindingResult bdResult
            , RedirectAttributes atts
        ) {
        String redirectTo = Constants.REDIRECT_FORM;
        String[] parametrosAExcluir = new String[]{
            ""
        };
        if (ValidadorHttp.isPeticionCorrectaExcluyendoCampos(bdResult, parametrosAExcluir)) {
            ServiceResponse serviceResponse = secUserRoleService.saveValidated(secUserRole);
            atts.addFlashAttribute(Constants.SERVICE_RESPONSE_NAME, serviceResponse);
            redirectTo = serviceResponse.isSuccess() ? REDIRECT_SEC_USER_ROLE : redirectTo;
        }
        atts.addFlashAttribute(SEC_USER_ROLE, secUserRole);
        atts.addFlashAttribute(BindingResult.class.getCanonicalName() + ".secUserRole", bdResult);
        return redirectTo;
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)
    @PostMapping(value = {"/delete"})
    public @ResponseBody ServiceResponse deleteEntity(@RequestParam(value="skUserRole", required = false) Integer skUserRole){
        ServiceResponse serviceResponse = secUserRoleService.delete(skUserRole);
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
            Slice<SecUserRole> list;
            if (q == null || q.equals("")) {
                list = secUserRoleService.getList(page, rows);
            }else{
                list = secUserRoleService.getListByQ(q, PageRequest.of(page - 1, rows));
            }
            List<S2> results = new ArrayList<>();
            list.getContent().stream().map(u -> new S2(u.getSkUserRole().toString(), u.getSkUserRole().toString(),null)).forEachOrdered(results::add);
            response = new S2Response<>(results, list.hasNext());
        } else {
            response = new S2Response<>();
        }
        return response;
    }
}
