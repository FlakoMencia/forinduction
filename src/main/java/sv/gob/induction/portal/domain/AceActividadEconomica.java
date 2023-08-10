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
@Table(name = "ace_actividad_economica")
public class AceActividadEconomica implements Serializable {
    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "ace_id")
    @EqualsAndHashCode.Include
    @JsonView
    private Integer aceId;

    @Column(name = "ace_nombre" )
    @Size(max = 15, message = "El campo aceNombre excede la longitud permitida")
    private String aceNombre; 

    @Column(name = "ace_id_csr_variable" )
    @NotNull(message = "No puede estar vacio el campo aceIdCsrVariable")
    private Integer aceIdCsrVariable; 

    @Column(name = "ace_id_csr_fijo" )
    @NotNull(message = "No puede estar vacio el campo aceIdCsrFijo")
    private Integer aceIdCsrFijo; 


    @OneToMany(mappedBy = "aceActividadEconomica", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @Getter(onMethod = @__(@JsonIgnore))
    @ToString.Exclude
    private Set<GirGiro> girGiroes;




    // delegates de ids
}
