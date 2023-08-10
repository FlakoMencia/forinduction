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

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;


import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "reg_empresa")
public class RegEmpresa implements Serializable {
    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "sk_empresa")
    @EqualsAndHashCode.Include
    @JsonView
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_reg_empresa")
    @SequenceGenerator(name = "seq_reg_empresa", sequenceName = "seq_reg_empresa", allocationSize = 1)
    private Integer skEmpresa; 

    @Column(name = "cd_ccc" )
    private String cdCcc; 

    @Column(name = "st_razon_social" )
    @Size(max = 255, message = "El campo stRazonSocial excede la longitud permitida")
    private String stRazonSocial; 

    @Column(name = "st_nombres" )
    @Size(max = 255, message = "El campo stNombres excede la longitud permitida")
    private String stNombres; 

    @Column(name = "st_apellidos" )
    @Size(max = 255, message = "El campo stApellidos excede la longitud permitida")
    private String stApellidos;

    @Column(name = "st_email" )
    @Size(max = 255, message = "El campo stEmail excede la longitud permitida")
    private String stEmail; 

    @Column(name = "extranjero" )
    private Integer extranjero;

    @Column(name = "dui" )
    @Size(max = 9, message = "El campo dui excede la longitud permitida")
    private String dui; 

    @Column(name = "nit" )
    @Size(max = 14, message = "El campo nit excede la longitud permitida")
    private String nit; 

    @Column(name = "nrc" )
    @Size(max = 15, message = "El campo nrc excede la longitud permitida")
    private String nrc; 

    @Column(name = "pasaporte" )
    @Size(max = 100, message = "El campo pasaporte excede la longitud permitida")
    private String pasaporte; 

    @Column(name = "cd_actua_como" )
    @Size(max = 1, message = "El campo cdActuaComo excede la longitud permitida")
    private String cdActuaComo; 

    @Column(name = "st_denominacion" )
    @Size(max = 255, message = "El campo stDenominacion excede la longitud permitida")
    private String stDenominacion; 

    @Column(name = "st_direccion" )
    @Size(max = 255, message = "El campo stDireccion excede la longitud permitida")
    private String stDireccion; 

    @Column(name = "st_direccion_cobros" )
    @Size(max = 255, message = "El campo stDireccionCobros excede la longitud permitida")
    private String stDireccionCobros; 

    @Column(name = "st_direccion_inmueble" )
    @Size(max = 255, message = "El campo stDireccionInmueble excede la longitud permitida")
    private String stDireccionInmueble; 

    @Column(name = "cd_propietario_inm" )
    private Integer cdPropietarioInm; 

    @Column(name = "st_propietario_inm" )
    @Size(max = 255, message = "El campo stPropietarioInm excede la longitud permitida")
    private String stPropietarioInm; 

    @Column(name = "st_matricula" )
    @Size(max = 255, message = "El campo stMatricula excede la longitud permitida")
    private String stMatricula; 

    @Column(name = "activo_inicial" )
    private Double activoInicial;

    @Column(name = "fc_inicio_operaciones" )
    @DateTimeFormat(pattern = Constants.DATE_FORMAT)
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    private LocalDate fcInicioOperaciones; 

    @Column(name = "comentario" )
    private String comentario; 

    @Column(name = "cd_tipo_activo" )
    @Size(max = 1, message = "El campo cdTipoActivo excede la longitud permitida")
    private String cdTipoActivo; 

    @Column(name = "st_nombre_contador" )
    @Size(max = 255, message = "El campo stNombreContador excede la longitud permitida")
    private String stNombreContador; 

    @Column(name = "st_apellido_contador" )
    @Size(max = 255, message = "El campo stApellidoContador excede la longitud permitida")
    private String stApellidoContador;

    @Getter(onMethod = @__( @JsonIgnore))
    @JoinColumn(name = "sk_genero_contador", referencedColumnName = "gen_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private GenGenero genGeneroContador;

    @Column(name = "st_email_contador" )
    @Size(max = 255, message = "El campo stEmailContador excede la longitud permitida")
    private String stEmailContador; 

    @Column(name = "cd_aprobado" )
    private Integer cdAprobado;

    @Column(name = "cd_conta_formal" )
    private Integer cdContaFormal;


    @OneToMany(mappedBy = "regEmpresa", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @Getter(onMethod = @__(@JsonIgnore))
    @ToString.Exclude
    private Set<RegDocumentoAnexo> regDocumentoAnexoes;


    @Getter(onMethod = @__( @JsonIgnore))
    @JoinColumn(name = "sk_solicitud", referencedColumnName = "sk_solicitud")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private RegSolicitud regSolicitud;

    @Getter(onMethod = @__( @JsonIgnore))
    @JoinColumn(name = "sk_genero", referencedColumnName = "gen_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private GenGenero genGenero;

    @Getter(onMethod = @__( @JsonIgnore))
    @JoinColumn(name = "sk_giro", referencedColumnName = "gir_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private GirGiro girGiro;

    @OneToMany(mappedBy = "regEmpresa", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @Getter(onMethod = @__(@JsonIgnore))
    @ToString.Exclude
    private Set<RegTelefonos> regTelefonos;

    // delegates de ids
    public Integer getGenGeneroGenIdDelegate() {
        if(genGenero != null) {
            return genGenero.getGenId();
        }
        else return null;
    }

    public String getGenGeneroStDescripcionDelegate() {
        if(genGenero != null) {
            return genGenero.getGenId().toString();
        }
        else return "";
    }

    public String getGenGeneroSelect2Delegate() {
        return String.valueOf(getGenGeneroGenIdDelegate())
                + "|"
                + getGenGeneroStDescripcionDelegate();
    }

    public Integer getRegSolicitudSkSolicitudDelegate() {
        if(regSolicitud != null) {
            return regSolicitud.getSkSolicitud();
        }
        else return null;
    } 
    
    public String getRegSolicitudStDescripcionDelegate() {
        if(regSolicitud != null) {
            return regSolicitud.getSkSolicitud().toString();
        }
        else return "";
    }

    public String getRegSolicitudSelect2Delegate() {
        return String.valueOf(getRegSolicitudSkSolicitudDelegate())
            + "|"
            + getRegSolicitudStDescripcionDelegate();
    }

}
