/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package sv.gob.induction.portal.enums;

import lombok.Getter;

@Getter
public enum TipoDocumentoEnum {
    DUI("DUI", "Documento Unico de Identidad"),

    NIT("NIT", "Numero de Identificacion Tributaria"),

    NRC("NRC", "Numero de Registro Contribuyente"),
    PAS("PAS", "Pasaporte"),
    DUIT("DUIT", "DUI y NIT Homologado"),
    ESC("ESC", "Escritura de la Constitucion"),
    CRED("CRED", "Credencial Legal o Poder Judicial"),
    ARE("ARE", "Contrato de Arrendamiento"),
    BAL("BAL", "Balance Inicial"),
    INV("INV", "Inventario Inicial"),
    PER("PER", "Permisos"),
    NCR("NCR", "Numero de Carnet de Residencia"),
    OTRO("OTRO", "Otros");

    private String tipo;

    private String descripcion;

    private TipoDocumentoEnum(String tipo, String descripcion) {
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    public TipoDocumentoEnum getTipoDocumentoEnum(String tipo) {
        for (TipoDocumentoEnum item : values()) {
            if (item.getTipo().equals(tipo)) {
                return item;
            }
        }
        return null;
    }
}
