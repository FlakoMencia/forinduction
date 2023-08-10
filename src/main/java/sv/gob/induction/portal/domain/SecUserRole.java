package sv.gob.induction.portal.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.time.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import sv.gob.induction.portal.commons.Constants;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;


import lombok.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "SEC_USER_ROLE")
//@TablaVersionada(nombre = "secUserRole", etiqueta = "SecUserRole")
public class SecUserRole implements Serializable {
    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "SK_USER_ROLE")
    @EqualsAndHashCode.Include
    @JsonView
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_SEC_USER_ROLE")
    @SequenceGenerator(name = "SEQ_SEC_USER_ROLE", sequenceName = "SEQ_SEC_USER_ROLE", allocationSize = 1)
    private Integer skUserRole; 

    @Column(name = "ST_CREA_USUARIO" )
    @Size(max = 150, message = "El campo stCreaUsuario excede la longitud permitida")
    @CreatedBy
    private String stCreaUsuario; 

    @Column(name = "FC_CREA_FECHA" )
    @DateTimeFormat(pattern = Constants.DATE_FORMAT)
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    @CreatedDate
    private LocalDate fcCreaFecha; 





    @Getter(onMethod = @__( @JsonIgnore))
    @JoinColumn(name = "FK_USER", referencedColumnName = "SK_USER")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private SecUser secUser; 


    @Getter(onMethod = @__( @JsonIgnore))
    @JoinColumn(name = "FK_ROLE", referencedColumnName = "SK_ROLE")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private SecRole secRole; 



    // delegates de ids
    public Integer getSecUserSkUserDelegate() {
        if(secUser != null) {
            return secUser.getSkUser();
        }
        else return null;
    } 
    
    public String getSecUserStDescripcionDelegate() {
        if(secUser != null) {
            return secUser.getSkUser().toString();
        }
        else return "";
    }

    public String getSecUserSelect2Delegate() {
        return String.valueOf(getSecUserSkUserDelegate())
            + "|"
            + getSecUserStDescripcionDelegate();
    }
    public Integer getSecRoleSkRoleDelegate() {
        if(secRole != null) {
            return secRole.getSkRole();
        }
        else return null;
    } 
    
    public String getSecRoleStDescripcionDelegate() {
        if(secRole != null) {
            return secRole.getSkRole().toString();
        }
        else return "";
    }

    public String getSecRoleSelect2Delegate() {
        return String.valueOf(getSecRoleSkRoleDelegate())
            + "|"
            + getSecRoleStDescripcionDelegate();
    }

    public void setSecRoleIdDelegate(Integer skRol) {
        this.secRole = new SecRole();
        this.secRole.setSkRole(skRol);
    }
}
