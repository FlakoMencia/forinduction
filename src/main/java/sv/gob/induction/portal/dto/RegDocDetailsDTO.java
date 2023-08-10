package sv.gob.induction.portal.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.Subselect;

@Data
@Entity
@Subselect("SELECT d.sk_doc_anexo skDocAnexo, d.st_nombre_doc stNombreDoc, ctd.cd_tipo_doc cdTipoDoc, d.sk_empresa skEmpresa, d.type_file typeFile FROM reg_documento_anexo d join cc_tipo_documento ctd on d.sk_tipo_doc = ctd.sk_tipo_doc")
public class RegDocDetailsDTO {
    @Id
    private Integer skDocAnexo;
    private String stNombreDoc;
    private String cdTipoDoc;
    private Integer skEmpresa;
    private String typeFile;
}


