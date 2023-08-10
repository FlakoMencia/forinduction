package sv.gob.induction.portal.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;
import sv.gob.induction.portal.commons.ServiceResponse;
import sv.gob.induction.portal.domain.RegEmpresa;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesInput;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesOutput;
import sv.gob.induction.portal.domain.RegSolicitud;
import sv.gob.induction.portal.dto.WebServicePersonaDTO;

public interface RegEmpresaService {
    Optional<RegEmpresa> findById(Integer skEmpresa);
    Optional<RegEmpresa> findBySkEmpresa(Integer skEmpresa);
    ServiceResponse saveValidated(RegEmpresa regEmpresa);
    ServiceResponse delete(Integer skEmpresa);
    List<RegEmpresa> findAll();
	
	// metodos para obtener data como lista
    Slice<RegEmpresa> getList(Integer page, Integer rows);
    Slice<RegEmpresa> getListByQ(String q, Pageable page);
	
    // metodos para DatatTable
    DataTablesOutput<RegEmpresa> findAll(DataTablesInput input);

    RegEmpresa findOrCreateRegEmpresaBySolicitud(RegSolicitud sol);

    RegEmpresa findByIdAndSolicitante(Integer skRegEmpresa, String username);

    RegEmpresa findByIdAndSolicitanteAndEstadoCre(Integer skRegEmpresa, String username);

    ServiceResponse saveValidatedCompanyAndAttachment(RegEmpresa provisional, MultipartFile duiFile, MultipartFile nitFile, MultipartFile nrcFile, MultipartFile pasaFile);

    ServiceResponse saveValidatedCompanyAndAttachment(RegEmpresa provisional, MultipartFile arrFile, MultipartFile balFile, MultipartFile invFile);

    ServiceResponse sendAndValidatedSolWithCredencials(RegEmpresa provisional, List<MultipartFile> files);

    RegEmpresa findOrCreateSolicitudByUserCCCSolicitante( WebServicePersonaDTO personaByCC, String username);

    RegEmpresa findOrCreateRegEmpresaByUserSolicitante(String username);

    ServiceResponse saveValidatedCompanyPJAndAttachment(RegEmpresa company, MultipartFile escFile, MultipartFile nitFile, MultipartFile nrcFile);

    ServiceResponse saveValidatedCompanyPJAndAttachment(RegEmpresa company, MultipartFile duiFile, MultipartFile nitFile, MultipartFile creFile, MultipartFile pasaFile);

}
