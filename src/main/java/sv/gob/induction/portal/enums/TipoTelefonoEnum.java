package sv.gob.induction.portal.enums;

import lombok.Getter;

@Getter
public enum TipoTelefonoEnum {

    CASA("CASA", "Telefono de residencia del propietario"),
    TRABAJO("TRABAJO", "Telefono de trabajo del propietario"),
    MOBIL("MOBIL", "Telefono celular del propietario"),
    OTRO("OTRO", "Otro Telefono");

    private String codigo;

    private String descripcion;

    private TipoTelefonoEnum(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public TipoTelefonoEnum getTipoTelefonoEnumEnum(String codigo) {
        for (TipoTelefonoEnum item : values()) {
            if (item.getCodigo().equals(codigo)) {
                return item;
            }
        }
        return null;
    }

}
