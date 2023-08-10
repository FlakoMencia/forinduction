package sv.gob.induction.portal.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "cc_estado_solicitud")
public class CcEstadoSolicitud implements Serializable {
    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "sk_estado")
    @EqualsAndHashCode.Include
    @JsonView
    private Integer skEstado; 

    @Column(name = "cd_estado" )
    @NotBlank(message = "Campo no puede quedar vacio")
    @Size(max = 3, message = "El campo cdEstado excede la longitud permitida")
    private String cdEstado; 

    @Column(name = "st_nombre" )
    @NotBlank(message = "Campo no puede quedar vacio")
    @Size(max = 15, message = "El campo stNombre excede la longitud permitida")
    private String stNombre; 



    @OneToMany(mappedBy = "ccEstadoSolicitud", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @Getter(onMethod = @__(@JsonIgnore))
    @ToString.Exclude
    private Set<RegSolicitud> regSolicitudes;


}
