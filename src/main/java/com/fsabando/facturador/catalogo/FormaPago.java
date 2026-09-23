package com.fsabando.facturador.catalogo;

/**
 * Formas de pago admitidas (tabla 24 de la ficha tecnica del SRI).
 * Corresponde al {@code <xsd:simpleType name="formaPago">}.
 */
public enum FormaPago {

    SIN_UTILIZACION_SISTEMA_FINANCIERO("01", "Sin utilizacion del sistema financiero"),
    COMPENSACION_DEUDAS("15", "Compensacion de deudas"),
    TARJETA_DEBITO("16", "Tarjeta de debito"),
    DINERO_ELECTRONICO("17", "Dinero electronico"),
    TARJETA_PREPAGO("18", "Tarjeta prepago"),
    TARJETA_CREDITO("19", "Tarjeta de credito"),
    OTROS_CON_SISTEMA_FINANCIERO("20", "Otros con utilizacion del sistema financiero"),
    ENDOSO_TITULOS("21", "Endoso de titulos");

    private final String codigo;
    private final String descripcion;

    FormaPago(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static FormaPago desdeCodigo(String codigo) {
        for (FormaPago forma : values()) {
            if (forma.codigo.equals(codigo)) {
                return forma;
            }
        }
        throw new IllegalArgumentException("Forma de pago desconocida: " + codigo);
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
