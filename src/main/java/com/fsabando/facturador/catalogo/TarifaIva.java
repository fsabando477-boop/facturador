package com.fsabando.facturador.catalogo;

import java.math.BigDecimal;

/**
 * Tarifas de IVA vigentes (tabla 17 de la ficha tecnica del SRI).
 *
 * <p>El XSD declara {@code codigoPorcentaje} y {@code tarifa} como campos
 * independientes, de modo que nada impide escribir un porcentaje 4 con tarifa
 * 12. Este enumerado ata ambos valores para que esa combinacion invalida
 * simplemente no pueda representarse.</p>
 */
public enum TarifaIva {

    CERO("0", "0"),
    DOCE("2", "12"),
    CATORCE("3", "14"),
    QUINCE("4", "15"),
    CINCO("5", "5"),
    NO_OBJETO("6", "0"),
    EXENTO("7", "0"),
    TRECE("10", "13");

    private final String codigoPorcentaje;
    private final BigDecimal porcentaje;

    TarifaIva(String codigoPorcentaje, String porcentaje) {
        this.codigoPorcentaje = codigoPorcentaje;
        this.porcentaje = new BigDecimal(porcentaje);
    }

    public String getCodigoPorcentaje() {
        return codigoPorcentaje;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public static TarifaIva desdeCodigo(String codigoPorcentaje) {
        for (TarifaIva tarifa : values()) {
            if (tarifa.codigoPorcentaje.equals(codigoPorcentaje)) {
                return tarifa;
            }
        }
        throw new IllegalArgumentException("Codigo de porcentaje de IVA desconocido: " + codigoPorcentaje);
    }

    @Override
    public String toString() {
        return porcentaje + "%";
    }
}
