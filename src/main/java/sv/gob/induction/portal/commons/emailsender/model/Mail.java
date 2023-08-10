package sv.gob.induction.portal.commons.emailsender.model;

import java.io.File;
import java.util.List;
import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mail {

    private List<String> correosDestino;
    private String encabezado;
    private String cuerpo;
    private String fileName;
    private File file;

    public Mail(List<String> correosDestino, String encabezado, String cuerpo) {
        this.correosDestino = correosDestino;
        this.encabezado = encabezado;
        this.cuerpo = cuerpo;
    }

}
