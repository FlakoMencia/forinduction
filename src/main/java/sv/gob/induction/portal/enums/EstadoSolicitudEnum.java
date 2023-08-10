package sv.gob.induction.portal.enums;

import lombok.Getter;

@Getter
public enum EstadoSolicitudEnum {
    CREADO("CRE", "Creado"),
    ENVIADO("ENV", "Enviado"),
    DENEGADO("DEN", "Denegado"),
    APROBADO("APR", "Aprobado"),
    VALIDADO("VAL", "Validado");

    private String codigo;

    private String descripcion;

    private EstadoSolicitudEnum(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public EstadoSolicitudEnum getEstadoSolicitudEnum(String rol) {
        for (EstadoSolicitudEnum item : values()) {
            if (item.getCodigo().equals(rol)) {
                return item;
            }
        }
        return null;
    }
}
