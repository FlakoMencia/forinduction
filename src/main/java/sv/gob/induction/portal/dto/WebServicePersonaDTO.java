package sv.gob.induction.portal.dto;

import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class WebServicePersonaDTO implements Serializable {

    @Id
    private Integer skWs;

    private String ccc;

    private String nombres;

    private String apellidos;

    private String dui;

    private String pasaporte;

    private String nit;

    private String razonSocial;

    private String nombre;

    private String nitRepresentante;

    private String nrc;

    private String direccionCobro;

    private String tipoPersona;

    private String empresas;

}
