package sv.gob.induction.portal.service;

import sv.gob.induction.portal.repository.CcTelefonoTitularRepository;
import sv.gob.induction.portal.repository.RegEmpresaRepository;
import sv.gob.induction.portal.repository.RegTelefonosRepository;
import sv.gob.induction.portal.domain.RegTelefonos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sv.gob.induction.portal.enums.TitularTelefonoEnum;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import sv.gob.induction.portal.commons.Constants;
import sv.gob.induction.portal.commons.ServiceResponse;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesInput;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesOutput;
import sv.gob.induction.portal.commons.exception.EntidadNoEncontradaException;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class RegTelefonosServiceImpl implements RegTelefonosService {

    @Autowired
    private RegTelefonosRepository regTelefonosRepository;

    @Autowired
    private RegEmpresaRepository regEmpresaRepository;

    @Autowired
    private CcTelefonoTitularRepository ccTelefonoTitularRepository;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<RegTelefonos> findById(Integer skTel) {
            return regTelefonosRepository.findById(skTel);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RegTelefonos> findBySkTel(Integer skTel) {
            return regTelefonosRepository.findBySkTel(skTel);
    }

    @Override
    public ServiceResponse saveValidated(RegTelefonos regTelefonos) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            RegTelefonos savedRegTelefonos = regTelefonosRepository.save(regTelefonos);
            serviceResponse.setMessage(Constants.MSG_GUARDADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(savedRegTelefonos);

            return serviceResponse;
    }

    @Override
    public ServiceResponse delete(Integer skTel) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            RegTelefonos regTelefonos = findById(skTel)
                    .orElseThrow(() -> new EntidadNoEncontradaException(skTel.toString()));
            regTelefonosRepository.delete(regTelefonos);

            serviceResponse.setMessage(Constants.MSG_ELIMINADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(null);
            return serviceResponse;
    }

    @Override
    public List<RegTelefonos> findAll() {
        return regTelefonosRepository.findAll();
    }

	// metodos para obtener data como lista
    @Override
    @Transactional(readOnly = true)
    public Slice<RegTelefonos> getList(Integer page, Integer rows) {
            return regTelefonosRepository.findAll(PageRequest.of(page - 1, rows));
    }
	
    @Override
    @Transactional(readOnly = true)
    public Slice<RegTelefonos> getListByQ(String q, Pageable page) {
            return regTelefonosRepository.findBySkTelIgnoreCaseContaining(q, page);
    }
	
    // metodos para DataTable
    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<RegTelefonos> findAll(DataTablesInput input) {
            return regTelefonosRepository.findAll(input);
    }

    @Override
    public ServiceResponse saveTelephoneForOwner(Integer skEmpresa, String tipoTel, String newtel, String user) {
        ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
        RegTelefonos tel = new RegTelefonos();
        tel.setCdTipoTelefono(tipoTel);
        tel.setCdUserRegistra(user);
        tel.setStNumeroTelefono(newtel);
        tel.setFcFechaReg(new Date());
        tel.setCcTelefonoTitular(ccTelefonoTitularRepository.findByCdTelefonoTitular(TitularTelefonoEnum.PROPIETARIO.getCodigo()));
        tel.setRegEmpresa(regEmpresaRepository.findBySkEmpresa(skEmpresa).get());
        regTelefonosRepository.save(tel);
        serviceResponse.setMessage(Constants.MSG_GUARDADO_EXITOSO);
        serviceResponse.setSuccess(true);
        serviceResponse.setData(null);
        return serviceResponse;
    }
    @Override
    public ServiceResponse saveTelephoneOther(Integer skEmpresa, String tipoTel, String newtel, String user, String titular) {
        ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
        RegTelefonos tel = new RegTelefonos();
        tel.setCdTipoTelefono(tipoTel);
        tel.setCdUserRegistra(user);
        tel.setStNumeroTelefono(newtel);
        tel.setFcFechaReg(new Date());
        tel.setCcTelefonoTitular(ccTelefonoTitularRepository.findByCdTelefonoTitular(titular));
        tel.setRegEmpresa(regEmpresaRepository.findBySkEmpresa(skEmpresa).get());
        regTelefonosRepository.save(tel);
        serviceResponse.setMessage(Constants.MSG_GUARDADO_EXITOSO);
        serviceResponse.setSuccess(true);
        serviceResponse.setData(null);
        return serviceResponse;
    }

    @Override
    public ServiceResponse removeTelephone(Integer skEmpresa, String phone, String tipoTitular) {
        ServiceResponse serviceResponse = new ServiceResponse(false, "No se encontraron registros");
        List<RegTelefonos> telephones =  regTelefonosRepository.findBySkEmpresaAndTipoTitular(skEmpresa, tipoTitular);
        if(telephones!= null && !telephones.isEmpty()){
            for(RegTelefonos tel: telephones){
                if(tel.getStNumeroTelefono().replace("-","").equals(phone)){
                    regTelefonosRepository.delete(tel);
                    serviceResponse.setMessage(Constants.MSG_ELIMINADO_EXITOSO);
                    serviceResponse.setSuccess(true);
                    serviceResponse.setData(null);
                    break;
                }
            }
        }
        return serviceResponse;
    }
}
