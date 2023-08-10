package sv.gob.induction.portal.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

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
@Table(name = "reg_solicitud")
public class RegSolicitud implements Serializable {
    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "sk_solicitud")
    @EqualsAndHashCode.Include
    @JsonView
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_reg_solicitud")
    @SequenceGenerator(name = "seq_reg_solicitud", sequenceName = "seq_reg_solicitud", allocationSize = 1)
    private Integer skSolicitud; 

    @Column(name = "num_solicitud" )
    private Integer numSolicitud; 

    @Column(name = "fc_crea_fecha" )
    @DateTimeFormat(pattern = Constants.DATE_FORMAT)
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    @CreatedDate
    private Date fcCreaFecha;

    @Column(name = "fc_fecha_analisis" )
    @DateTimeFormat(pattern = Constants.DATE_FORMAT)
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    private Date fcFechaAnalisis;

    @Column(name = "cd_tipo_solicitud" )
    @Size(max = 1, message = "El campo cdTipoSolicitud excede la longitud permitida")
    private String cdTipoSolicitud;

    @Column(name = "cd_acepta_terminos" )
    private Integer cdAceptaTerminos;

    @Column(name = "cd_tipo_persona" )
    @Size(max = 1, message = "El campo cdTipoPersona excede la longitud permitida")
    private String cdTipoPersona;


    @Getter(onMethod = @__(
            @JsonIgnore))
    @JoinColumn(name = "fk_user_sol", referencedColumnName = "sk_user", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude

    private SecUser secUserSolicita;

    @Getter(onMethod = @__(
            @JsonIgnore))
    @JoinColumn(name = "fk_user_rev", referencedColumnName = "sk_user", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude

    private SecUser secUserAnaliza;

    @OneToMany(mappedBy = "regSolicitud", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @Getter(onMethod = @__(@JsonIgnore))
    @ToString.Exclude
    private Set<RegDocumentoAnexo> regDocumentoAnexos;

    @OneToMany(mappedBy = "regSolicitud", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @Getter(onMethod = @__(@JsonIgnore))
    @ToString.Exclude
    private Set<RegEmpresa> regEmpresas;


    @Getter(onMethod = @__( @JsonIgnore))
    @JoinColumn(name = "sk_estado_sol", referencedColumnName = "sk_estado")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private CcEstadoSolicitud ccEstadoSolicitud; 



    // delegates de ids
    public Integer getCcEstadoSolicitudSkEstadoDelegate() {
        if(ccEstadoSolicitud != null) {
            return ccEstadoSolicitud.getSkEstado();
        }
        else return null;
    } 
    
    public String getCcEstadoSolicitudStDescripcionDelegate() {
        if(ccEstadoSolicitud != null) {
            return ccEstadoSolicitud.getSkEstado().toString();
        }
        else return "";
    }

    public String getCcEstadoSolicitudSelect2Delegate() {
        return String.valueOf(getCcEstadoSolicitudSkEstadoDelegate())
            + "|"
            + getCcEstadoSolicitudStDescripcionDelegate();
    }

}
