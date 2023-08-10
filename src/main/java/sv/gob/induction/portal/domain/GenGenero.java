package sv.gob.induction.portal.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;
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
@Table(name = "gen_genero")
public class GenGenero implements Serializable {
    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "gen_id")
    @EqualsAndHashCode.Include
    @JsonView
    private Integer genId;

    @Column(name = "gen_nombre" )
    @Size(max = 15, message = "El campo genNombre excede la longitud permitida")
    private String genNombre; 






    // delegates de ids
}
