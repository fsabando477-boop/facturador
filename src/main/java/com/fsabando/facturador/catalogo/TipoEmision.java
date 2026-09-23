package com.fsabando.facturador.catalogo;

/**
 * Tipo de emision del comprobante.
 * Corresponde al {@code <xsd:simpleType name="tipoEmision">} con patron {@code [12]}.
 */
public enum TipoEmision {

    NORMAL("1", "Emision normal"),
    CONTINGENCIA("2", "Emision por contingencia");

    private final String codigo;
    private final String descripcion;

    TipoEmision(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static TipoEmision desdeCodigo(String codigo) {
        for (TipoEmision tipo : values()) {
            if (tipo.codigo.equals(codigo)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de emision desconocido: " + codigo);
    }

    @Override
    public String toString() {
        return codigo + " - " + descripcion;
    }
}
