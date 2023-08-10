/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package sv.gob.induction.portal.enums;

import lombok.Getter;

@Getter
public enum RoleEmun {
    ADMIN("ADMIN", "Administrador"),
    CONTRIBUYENTE("CONTRIBUYENTE", "Contribuyente");

    private String rol;

    private String descripcion;

    private RoleEmun(String rol, String descripcion) {
        this.rol = rol;
        this.descripcion = descripcion;
    }

    public RoleEmun getRoleEmun(String rol) {
        for (RoleEmun item : values()) {
            if (item.getRol().equals(rol)) {
                return item;
            }
        }
        return null;
    }
}
