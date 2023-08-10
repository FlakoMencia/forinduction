package sv.gob.induction.portal.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import sv.gob.induction.portal.commons.ServiceResponse;
import sv.gob.induction.portal.domain.RegTelefonos;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesInput;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesOutput;

public interface RegTelefonosService {
    Optional<RegTelefonos> findById(Integer skTel);
    Optional<RegTelefonos> findBySkTel(Integer skTel);
    ServiceResponse saveValidated(RegTelefonos regTelefonos);
    ServiceResponse delete(Integer skTel);
    List<RegTelefonos> findAll();
	
	// metodos para obtener data como lista
    Slice<RegTelefonos> getList(Integer page, Integer rows);
    Slice<RegTelefonos> getListByQ(String q, Pageable page);
	
    // metodos para DatatTable
    DataTablesOutput<RegTelefonos> findAll(DataTablesInput input);

    ServiceResponse saveTelephoneForOwner(Integer skEmpresa, String tipoTel, String newtel, String cuser);

    ServiceResponse saveTelephoneOther(Integer skEmpresa, String tipoTel, String newtel, String user, String titular);

    ServiceResponse removeTelephone(Integer skEmpresa, String phone, String tipoTitular);
}
