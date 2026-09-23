package com.fsabando.facturador.modelo;

import com.fsabando.facturador.comun.Validaciones;
import com.fsabando.facturador.modelo.impuesto.Impuesto;

import java.math.BigDecimal;

/**
 * Impuesto consolidado a nivel de comprobante ({@code totalImpuesto} dentro
 * de {@code <totalConImpuestos>} en el XSD).
 */
public class TotalImpuesto {

    private final String codigo;
    private final String codigoPorcentaje;
    private final BigDecimal tarifa;
    private BigDecimal baseImponible;
    private BigDecimal valor;

    public TotalImpuesto(Impuesto impuesto) {
        Validaciones.obligatorio(impuesto, "impuesto");
        this.codigo = impuesto.getCodigo();
        this.codigoPorcentaje = impuesto.getCodigoPorcentaje();
        this.tarifa = impuesto.getTarifa();
        this.baseImponible = impuesto.getBaseImponible();
        this.valor = impuesto.calcularValor();
    }

    public void acumular(Impuesto impuesto) {
        Validaciones.obligatorio(impuesto, "impuesto");
        if (!codigo.equals(impuesto.getCodigo())
                || !codigoPorcentaje.equals(impuesto.getCodigoPorcentaje())) {
            throw new IllegalArgumentException(
                    "No se pueden acumular impuestos de conceptos distintos");
        }
        this.baseImponible = baseImponible.add(impuesto.getBaseImponible());
        this.valor = valor.add(impuesto.calcularValor());
    }

    public boolean corresponde(Impuesto impuesto) {
        return impuesto != null
                && codigo.equals(impuesto.getCodigo())
                && codigoPorcentaje.equals(impuesto.getCodigoPorcentaje());
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCodigoPorcentaje() {
        return codigoPorcentaje;
    }

    public BigDecimal getTarifa() {
        return tarifa;
    }

    public BigDecimal getBaseImponible() {
        return baseImponible;
    }

    public BigDecimal getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return String.format("codigo=%s porcentaje=%s base=%s valor=%s",
                codigo, codigoPorcentaje, baseImponible, valor);
    }
}
