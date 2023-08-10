package sv.gob.induction.portal.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Set;

import java.time.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import sv.gob.induction.portal.commons.Constants;

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
@Table(name = "SEC_USER")
//@TablaVersionada(nombre = "secUser", etiqueta = "SecUser")
public class SecUser implements Serializable {

    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "SK_USER")
    @EqualsAndHashCode.Include
    @JsonView
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_SEC_USER")
    @SequenceGenerator(name = "SEQ_SEC_USER", sequenceName = "SEQ_SEC_USER", allocationSize = 1)
    private Integer skUser;

    @Column(name = "CD_USER")
    @NotBlank(message = "No puede estar vacio el campo cdUser")
    @Size(max = 150, message = "El campo cdUser excede la longitud permitida")
    private String cdUser;

    @Column(name = "ST_PASSWORD")
    @NotBlank(message = "No puede estar vacio el campo stPassword")
    @Size(max = 100, message = "El campo stPassword excede la longitud permitida")
    private String stPassword;

    @Column(name = "FC_CREA_FECHA")
    @DateTimeFormat(pattern = Constants.DATE_FORMAT)
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    @CreatedDate
    private LocalDate fcCreaFecha;

    @Column(name = "habilitado")
    @NotNull(message = "No puede estar vacio el campo habilitado")
    private Integer habilitado;

    @OneToMany(mappedBy = "secUser", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @Getter(onMethod = @__(
            @JsonIgnore))
    @ToString.Exclude
    private Set<SecUserRole> secUserRolees;
    
    @Getter(onMethod = @__(@JsonIgnore))
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "secUser")
    @ToString.Exclude

    private CcPersona ccPersona;

    // delegates de ids
    
    public boolean isHabilitadoLogin() {
        return habilitado != null && habilitado == 1;
    }
}
