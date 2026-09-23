package com.fsabando.facturador.catalogo;

/**
 * Tipo de identificacion del cliente (tabla 6 de la ficha tecnica del SRI).
 * Corresponde al {@code <xsd:simpleType name="tipoIdentificacionComprador">}
 * con patron {@code [0][4-8]}.
 */
public enum TipoIdentificacion {

    RUC("04", "RUC", 13),
    CEDULA("05", "Cedula", 10),
    PASAPORTE("06", "Pasaporte", 20),
    VENTA_CONSUMIDOR_FINAL("07", "Venta a consumidor final", 13),
    IDENTIFICACION_EXTERIOR("08", "Identificacion del exterior", 20);

    private final String codigo;
    private final String descripcion;
    private final int longitudMaxima;

    TipoIdentificacion(String codigo, String descripcion, int longitudMaxima) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.longitudMaxima = longitudMaxima;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getLongitudMaxima() {
        return longitudMaxima;
    }

    public static TipoIdentificacion desdeCodigo(String codigo) {
        for (TipoIdentificacion tipo : values()) {
            if (tipo.codigo.equals(codigo)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de identificacion desconocido: " + codigo);
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
