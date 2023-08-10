package sv.gob.induction.portal.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.time.*;

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
@Table(name = "sec_user_reset_password")
public class SecUserResetPassword implements Serializable {
    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "sk_user_reset_password")
    @EqualsAndHashCode.Include
    @JsonView
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_sec_user_reset_password")
    @SequenceGenerator(name = "SEQ_sec_user_reset_password", sequenceName = "SEQ_sec_user_reset_password", allocationSize = 1)
    private Integer skUserResetPassword; 

    @Column(name = "st_hash" )
    @NotBlank(message = "No puede estar vacio el campo stHash")
    @Size(max = 128, message = "El campo stHash excede la longitud permitida")
    private String stHash; 

    @Column(name = "fc_fin_validez" )
    @NotNull(message = "No puede estar vacio el campo fcFinValidez")
    @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private LocalDateTime fcFinValidez; 

    @Column(name = "bn_disponible" )
    @NotNull(message = "No puede estar vacio el campo bnDisponible")
    private Boolean bnDisponible; 





    @Getter(onMethod = @__( @JsonIgnore))
    @JoinColumn(name = "fk_user", referencedColumnName = "sk_user")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private SecUser secUser; 



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
}
