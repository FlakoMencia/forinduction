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

import sv.gob.induction.portal.domain.SecRole;
import sv.gob.induction.portal.service.SecRoleService;
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
@RequestMapping("/user/secRole")
public class SecRoleController {
	
    private static final String HAS_AUTHORITY_USER = "hasAuthority('')";
    private static final String REDIRECT_sec_role = "redirect:/user/secRole/";
    private static final String sec_role = "secRole";
	
    @Autowired
    private SecRoleService secRoleService;
	
    //@PreAuthorize(HAS_AUTHORITY_USER)
    @GetMapping(value = {"/", ""})
    public String indexSecRole() {
            return "pages/secRole/list";
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)	
    @GetMapping("/list")
    public @ResponseBody
    DataTablesOutput<SecRole> listSecRole(@Valid DataTablesInput input) {
        return secRoleService.findAll(input);
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)
    @GetMapping("/form")
    public String formSecRole(@RequestParam(required = false) Integer skRole, Model model) {
        if (!model.containsAttribute(sec_role)) {
            SecRole secRole = new SecRole();
            if (skRole != null) {
                Optional<SecRole> optSecRole = secRoleService.findById(skRole);
                if (!optSecRole.isPresent()) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "SecRole Not Found");
                }
                secRole = optSecRole.get();
            }
            model.addAttribute(sec_role, secRole);
        }
        return "pages/secRole/form";
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)
    @PostMapping("/save")
    public String saveEntity(@Valid SecRole secRole
            , BindingResult bdResult
            , RedirectAttributes atts
        ) {
        String redirectTo = Constants.REDIRECT_FORM;
        String[] parametrosAExcluir = new String[]{
            ""
        };
        if (ValidadorHttp.isPeticionCorrectaExcluyendoCampos(bdResult, parametrosAExcluir)) {
            ServiceResponse serviceResponse = secRoleService.saveValidated(secRole);
            atts.addFlashAttribute(Constants.SERVICE_RESPONSE_NAME, serviceResponse);
            redirectTo = serviceResponse.isSuccess() ? REDIRECT_sec_role : redirectTo;
        }
        atts.addFlashAttribute(sec_role, secRole);
        atts.addFlashAttribute(BindingResult.class.getCanonicalName() + ".secRole", bdResult);
        return redirectTo;
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)
    @PostMapping(value = {"/delete"})
    public @ResponseBody ServiceResponse deleteEntity(@RequestParam(value="skRole", required = false) Integer skRole){
        ServiceResponse serviceResponse = secRoleService.delete(skRole);
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
            Slice<SecRole> list;
            if (q == null || q.equals("")) {
                list = secRoleService.getList(page, rows);
            }else{
                list = secRoleService.getListByQ(q, PageRequest.of(page - 1, rows));
            }
            List<S2> results = new ArrayList<>();
            list.getContent().stream().map(u -> new S2(u.getSkRole().toString(), u.getStDescripcion(),null)).forEachOrdered(results::add);
            response = new S2Response<>(results, list.hasNext());
        } else {
            response = new S2Response<>();
        }
        return response;
    }
}
