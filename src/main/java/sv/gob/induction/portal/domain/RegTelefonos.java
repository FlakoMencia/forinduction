package sv.gob.induction.portal.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import sv.gob.induction.portal.commons.Constants;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;


import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "reg_telefonos")
public class RegTelefonos implements Serializable {
    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "sk_tel")
    @EqualsAndHashCode.Include
    @JsonView
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_reg_telefonos")
    @SequenceGenerator(name = "seq_reg_telefonos", sequenceName = "seq_reg_telefonos", allocationSize = 1)
    private Integer skTel; 

    @Column(name = "cd_codigo_pais" )
    private String cdCodigoPais;

    @Column(name = "cd_tipo_telefono" )
    private String cdTipoTelefono; 

    @Column(name = "st_numero_telefono" )
    @NotBlank(message = "Numero de telefono Obligatorio ")
    private String stNumeroTelefono; 

    @Column(name = "cd_user_registra" )
    @NotBlank(message = "Ingresar Usuario")
    private String cdUserRegistra; 

    @Column(name = "fc_fecha_reg" )
    @DateTimeFormat(pattern = Constants.DATE_FORMAT)
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    private Date fcFechaReg;

    @Getter(onMethod = @__( @JsonIgnore))
    @JoinColumn(name = "sk_reg_empresa", referencedColumnName = "sk_empresa")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private RegEmpresa regEmpresa; 

    @Getter(onMethod = @__( @JsonIgnore))
    @JoinColumn(name = "sk_telefono_titular", referencedColumnName = "sk_tel_titular")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private CcTelefonoTitular ccTelefonoTitular; 


    // delegates de ids
    public Integer getRegEmpresaSkEmpresaDelegate() {
        if(regEmpresa != null) {
            return regEmpresa.getSkEmpresa();
        }
        else return null;
    } 
    
    public String getRegEmpresaStDescripcionDelegate() {
        if(regEmpresa != null) {
            return regEmpresa.getSkEmpresa().toString();
        }
        else return "";
    }

    public String getRegEmpresaSelect2Delegate() {
        return String.valueOf(getRegEmpresaSkEmpresaDelegate())
            + "|"
            + getRegEmpresaStDescripcionDelegate();
    }

    public Integer getCcTelefonoTitularSkTelTitularDelegate() {
        if(ccTelefonoTitular != null) {
            return ccTelefonoTitular.getSkTelTitular();
        }
        else return null;
    } 
    
    public String getCcTelefonoTitularStDescripcionDelegate() {
        if(ccTelefonoTitular != null) {
            return ccTelefonoTitular.getSkTelTitular().toString();
        }
        else return "";
    }

    public String getCcTelefonoTitularSelect2Delegate() {
        return String.valueOf(getCcTelefonoTitularSkTelTitularDelegate())
            + "|"
            + getCcTelefonoTitularStDescripcionDelegate();
    }

}
