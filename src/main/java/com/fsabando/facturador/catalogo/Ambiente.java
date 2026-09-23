package com.fsabando.facturador.catalogo;

/**
 * Ambiente de emision del comprobante.
 * Corresponde al {@code <xsd:simpleType name="ambiente">} con patron {@code [1-2]}.
 */
public enum Ambiente {

    PRUEBAS("1", "Pruebas"),
    PRODUCCION("2", "Produccion");

    private final String codigo;
    private final String descripcion;

    Ambiente(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static Ambiente desdeCodigo(String codigo) {
        for (Ambiente ambiente : values()) {
            if (ambiente.codigo.equals(codigo)) {
                return ambiente;
            }
        }
        throw new IllegalArgumentException("Ambiente desconocido: " + codigo);
    }

    @Override
    public String toString() {
        return codigo + " - " + descripcion;
    }
}
