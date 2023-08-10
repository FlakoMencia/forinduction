package sv.gob.induction.portal.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Set;

import java.time.*;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import sv.gob.induction.portal.commons.Constants;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;


import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "sec_role")
//@TablaVersionada(nombre = "secRole", etiqueta = "SecRole")
public class SecRole implements Serializable {
    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "sk_role")
    @EqualsAndHashCode.Include
    @JsonView
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_sec_role")
    @SequenceGenerator(name = "SEQ_sec_role", sequenceName = "SEQ_sec_role", allocationSize = 1)
    private Integer skRole; 

    @Column(name = "cd_role" )
    @NotBlank(message = "No puede estar vacio el campo cdRole")
    @Size(max = 50, message = "El campo cdRole excede la longitud permitida")
    private String cdRole; 

    @Column(name = "st_descripcion" )
    @NotBlank(message = "No puede estar vacio el campo stDescripcion")
    @Size(max = 256, message = "El campo stDescripcion excede la longitud permitida")
    private String stDescripcion; 

    @Column(name = "is_activo" )
    @NotNull(message = "No puede estar vacio el campo isActivo")
    private Boolean isActivo; 

    @Column(name = "st_crea_usuario" )
    @Size(max = 150, message = "El campo stCreaUsuario excede la longitud permitida")
    @CreatedBy
    private String stCreaUsuario; 

    @Column(name = "fc_crea_fecha" )
    @DateTimeFormat(pattern = Constants.DATE_FORMAT)
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    @CreatedDate
    private LocalDate fcCreaFecha; 

    @Column(name = "st_mod_usuario" )
    @Size(max = 150, message = "El campo stModUsuario excede la longitud permitida")
    @LastModifiedBy
    private String stModUsuario; 

    @Column(name = "fc_mod_fecha" )
    @DateTimeFormat(pattern = Constants.DATE_FORMAT)
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    @LastModifiedDate
    private LocalDate fcModFecha; 




    @OneToMany(mappedBy = "secRole", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @Getter(onMethod = @__(@JsonIgnore))
    @ToString.Exclude
    private Set<SecUserRole> secUserRolees;

    // delegates de ids
}
