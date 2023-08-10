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
@Table(name = "cc_tipo_documento")
public class CcTipoDocumento implements Serializable {
    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "sk_tipo_doc")
    @EqualsAndHashCode.Include
    @JsonView
    private Integer skTipoDoc; 

    @Column(name = "cd_tipo_doc" )
    @NotBlank(message = "Campo no puede quedar vacio")
    @Size(max = 5, message = "El campo cdTipoDoc excede la longitud permitida")
    private String cdTipoDoc; 

    @Column(name = "st_tipo_doc" )
    @NotBlank(message = "Campo no puede quedar vacio")
    @Size(max = 50, message = "El campo stTipoDoc excede la longitud permitida")
    private String stTipoDoc; 

    @Column(name = "bo_activo" )
    @NotNull(message = "Campo no puede quedar vacio")
    private Integer boActivo; 



    @OneToMany(mappedBy = "ccTipoDocumento", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @Getter(onMethod = @__(@JsonIgnore))
    @ToString.Exclude
    private Set<RegDocumentoAnexo> regDocumentoAnexos;




    // delegates de ids
}
