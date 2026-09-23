package com.fsabando.facturador.catalogo;

/**
 * Tipos de comprobante electronico (tabla 3 de la ficha tecnica del SRI).
 * Corresponde al {@code <xsd:simpleType name="codDoc">} con patron {@code [0-9]{2}}.
 */
public enum TipoComprobante {

    FACTURA("01", "Factura"),
    LIQUIDACION_COMPRA("03", "Liquidacion de compra de bienes y prestacion de servicios"),
    NOTA_CREDITO("04", "Nota de credito"),
    NOTA_DEBITO("05", "Nota de debito"),
    GUIA_REMISION("06", "Guia de remision"),
    COMPROBANTE_RETENCION("07", "Comprobante de retencion");

    private final String codigo;
    private final String descripcion;

    TipoComprobante(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static TipoComprobante desdeCodigo(String codigo) {
        for (TipoComprobante tipo : values()) {
            if (tipo.codigo.equals(codigo)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de comprobante desconocido: " + codigo);
    }

    @Override
    public String toString() {
        return codigo + " - " + descripcion;
    }
}
