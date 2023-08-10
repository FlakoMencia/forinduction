package sv.gob.induction.portal.enums;

import lombok.Getter;

@Getter
public enum TipoSolicitudEmun {
    EMPRESA("E", "Solicitud para Empresa"),
    SUCURSAL("E", "Solicitud para Sucursal");

    private String codigo;

    private String descripcion;


    private TipoSolicitudEmun(String rol, String descripcion) {
        this.codigo = rol;
        this.descripcion = descripcion;
    }

    public TipoSolicitudEmun getTipoSolEnum(String rol) {
        for (TipoSolicitudEmun item : values()) {
            if (item.getCodigo().equals(rol)) {
                return item;
            }
        }
        return null;
    }
}
