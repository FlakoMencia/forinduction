package sv.gob.induction.portal.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.sql.Types;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "reg_documento_anexo")
public class RegDocumentoAnexo implements Serializable {
    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "sk_doc_anexo")
    @EqualsAndHashCode.Include
    @JsonView
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_reg_doc_anexo")
    @SequenceGenerator(name = "seq_reg_doc_anexo", sequenceName = "seq_reg_doc_anexo", allocationSize = 1)
    private Integer skDocAnexo; 

    @Column(name = "st_nombre_doc" )
    @Size(max = 100, message = "El nombre del documento excede la longitud permitida")
    private String stNombreDoc; 

    @Lob
    @Column(name = "byte_file" )
    @Basic(fetch = FetchType.LAZY)
    @JdbcTypeCode(Types.BINARY) // necesario en mapeo de tipo de dato correctamente con postgresql
    private byte[] byteFile;

    @Column(name = "type_file" )
    @Size(max = 25, message = "El campo typeFile excede la longitud permitida")
    private String typeFile;


    @Getter(onMethod = @__( @JsonIgnore))
    @JoinColumn(name = "sk_tipo_doc", referencedColumnName = "sk_tipo_doc")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private CcTipoDocumento ccTipoDocumento; 

    @Getter(onMethod = @__( @JsonIgnore))
    @JoinColumn(name = "sk_empresa", referencedColumnName = "sk_empresa")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private RegEmpresa regEmpresa; 

    @Getter(onMethod = @__( @JsonIgnore))
    @JoinColumn(name = "sk_solicitud", referencedColumnName = "sk_solicitud")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private RegSolicitud regSolicitud; 



    // delegates de ids
    public Integer getCcTipoDocumentoSkTipoDocDelegate() {
        if(ccTipoDocumento != null) {
            return ccTipoDocumento.getSkTipoDoc();
        }
        else return null;
    } 
    
    public String getCcTipoDocumentoStDescripcionDelegate() {
        if(ccTipoDocumento != null) {
            return ccTipoDocumento.getSkTipoDoc().toString();
        }
        else return "";
    }

    public String getCcTipoDocumentoSelect2Delegate() {
        return String.valueOf(getCcTipoDocumentoSkTipoDocDelegate())
            + "|"
            + getCcTipoDocumentoStDescripcionDelegate();
    }

    public Integer getRegEmpresaSkEmpresaDelegate() {
        if(regEmpresa != null) {
            return regEmpresa.getSkEmpresa();
        }
        else return null;
    } 
    
    public String getRegEmpresaStDescripcionDelegate() {
        if(regEmpresa != null) {
            return regEmpresa.getSkEmpresa().toString();
        }
        else return "";
    }

    public String getRegEmpresaSelect2Delegate() {
        return String.valueOf(getRegEmpresaSkEmpresaDelegate())
            + "|"
            + getRegEmpresaStDescripcionDelegate();
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
