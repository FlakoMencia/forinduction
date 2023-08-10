package sv.gob.induction.portal.enums;

import lombok.Getter;

@Getter
public enum TitularTelefonoEnum {

    PROPIETARIO("PROPIETARIO", "Telefono de Propietario de Empresa"),
    CONTADOR("CONTADOR", "Telefono de Contador de la Empresa"),

    ESTABLECIMIENTO("ESTABLECIMIENTO", "Telefono del Establecimiento"),
    OTRO("OTRO", "Otro");

    private String codigo;

    private String descripcion;

    private TitularTelefonoEnum(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public TitularTelefonoEnum getTitularTelefonoEnum(String codigo) {
        for (TitularTelefonoEnum item : values()) {
            if (item.getCodigo().equals(codigo)) {
                return item;
            }
        }
        return null;
    }



}
