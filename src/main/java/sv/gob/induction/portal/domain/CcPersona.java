/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.gob.induction.portal.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.time.LocalDate;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import sv.gob.induction.portal.commons.Constants;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "cc_persona")
public class CcPersona implements Serializable {

    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "sk_persona")
    @EqualsAndHashCode.Include
    @JsonView
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_cc_persona")
    @SequenceGenerator(name = "seq_cc_persona", sequenceName = "seq_cc_persona", allocationSize = 1)

    private Integer skPersona;

    @Size(max = 10, message = "El campo cdCcc excede la longitud permitida")
    @Column(name = "cd_ccc", length = 10)

    private String cdCcc;

    @Basic(optional = false)
    @NotNull(message = "No puede estar vacio el campo stNombre")
    @Size(max = 150, message = "El campo stNombre excede la longitud permitida")
    @Column(name = "st_nombre", nullable = false, length = 150)

    private String stNombre;

    @Size(max = 150, message = "El campo stRazonSocial excede la longitud permitida")
    @Column(name = "st_razon_social", length = 150)

    private String stRazonSocial;

    @Basic(optional = false)
    @NotNull(message = "No puede estar vacio el campo cdTipoPersona")
    @Size(max = 1, message = "El campo cdTipoPersona excede la longitud permitida")
    @Column(name = "cd_tipo_persona", nullable = false, length = 1)

    private String cdTipoPersona;

    @Basic(optional = false)
    @NotNull(message = "No puede estar vacio el campo cdTipoDocumento")
    @Size(max = 3, message = "El campo cdTipoDocumento excede la longitud permitida")
    @Column(name = "cd_tipo_documento", nullable = false, length = 3)
    private String cdTipoDocumento;

    @Size(max = 10, message = "El campo cdDui excede la longitud permitida")
    @Column(name = "cd_dui", length = 10)

    private String cdDui;

    @Size(max = 20, message = "El campo cdPasaporte excede la longitud permitida")
    @Column(name = "cd_pasaporte", length = 20)

    private String cdPasaporte;

    @Basic(optional = false)
    @NotNull(message = "No puede estar vacio el campo cdNit")
    @Size(max = 17, message = "El campo cdNit excede la longitud permitida")
    @Column(name = "cd_nit", nullable = false, length = 17)

    private String cdNit;

    @Size(max = 17, message = "El campo cdNitJuridico excede la longitud permitida")
    @Column(name = "cd_nit_juridico", length = 17)

    private String cdNitJuridico;

    @Size(max = 1, message = "El campo cdTipoRepresentante excede la longitud permitida")
    @Column(name = "cd_tipo_representante", length = 1)

    private String cdTipoRepresentante;

    @Size(max = 150, message = "El campo stCreaUsuario excede la longitud permitida")
    @Column(name = "st_crea_usuario", length = 150)
    @CreatedBy

    private String stCreaUsuario;

    @Basic(optional = false)
    @NotNull(message = "No puede estar vacio el campo fcCreaFecha")
    @Column(name = "fc_crea_fecha", nullable = false)
    @DateTimeFormat(pattern = Constants.DATE_FORMAT)
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    @CreatedDate

    private LocalDate fcCreaFecha;

    @Column(name = "st_mod_usuario", length = 150)
    @Size(max = 150, message = "El campo stModUsuario excede la longitud permitida")
    @LastModifiedBy

    private String stModUsuario;

    @Column(name = "fc_mod_fecha")
    @DateTimeFormat(pattern = Constants.DATE_FORMAT)
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    @LastModifiedDate

    private LocalDate fcModFecha;

    @Getter(onMethod = @__(@JsonIgnore))
    @JoinColumn(name = "fk_user", referencedColumnName = "sk_user", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude

    private SecUser secUser;

    @Column(name = "st_hash")
    @NotBlank(message = "No puede estar vacio el campo stHash")
    @Size(max = 128, message = "El campo stHash excede la longitud permitida")
    private String stHash;

    @Transient
    private String aceptarCondicion;

    @Transient
    private String passwordConfirm;

    public String getCdUserDelegate() {
        return (secUser != null) ? secUser.getCdUser() : null;
    }

    public String getStPasswordDelegate() {
        return (secUser != null) ? secUser.getStPassword() : null;
    }
}
