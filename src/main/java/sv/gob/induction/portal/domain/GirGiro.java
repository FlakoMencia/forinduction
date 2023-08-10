package sv.gob.induction.portal.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
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
@Table(name = "gir_giro")
public class GirGiro implements Serializable {
    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "gir_id")
    @EqualsAndHashCode.Include
    @JsonView
    private Integer girId;

    @Column(name = "gir_nombre" )
    @NotBlank(message = "No puede estar vacio el campo girNombre")
    @Size(max = 150, message = "El campo girNombre excede la longitud permitida")
    private String girNombre; 


    @Getter(onMethod = @__( @JsonIgnore))
    @JoinColumn(name = "gir_id_ace", referencedColumnName = "ace_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private AceActividadEconomica aceActividadEconomica; 



    // delegates de ids
    public Integer getAceActividadEconomicaAceIdDelegate() {
        if(aceActividadEconomica != null) {
            return aceActividadEconomica.getAceId();
        }
        else return null;
    } 
    
    public String getAceActividadEconomicaStDescripcionDelegate() {
        if(aceActividadEconomica != null) {
            return aceActividadEconomica.getAceNombre().toString();
        }
        else return "";
    }

    public String getAceActividadEconomicaSelect2Delegate() {
        return String.valueOf(getAceActividadEconomicaAceIdDelegate())
            + "|"
            + getAceActividadEconomicaStDescripcionDelegate();
    }

}
