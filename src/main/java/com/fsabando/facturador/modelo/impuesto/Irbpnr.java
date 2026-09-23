package com.fsabando.facturador.modelo.impuesto;

import com.fsabando.facturador.comun.Validaciones;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Impuesto Redimible a las Botellas Plasticas no Retornables. Codigo 5 en el XSD.
 *
 * <p>Caso que justifica que {@link Impuesto#calcularValor()} sea abstracto: NO
 * es un porcentaje sobre el precio, sino una tarifa fija por cada botella.</p>
 */
public class Irbpnr extends Impuesto {

    public static final String CODIGO = "5";

    public static final BigDecimal TARIFA_POR_BOTELLA = new BigDecimal("0.02");

    private final int unidades;

    public Irbpnr(int unidades) {
        super(BigDecimal.valueOf(Math.max(unidades, 0)), TARIFA_POR_BOTELLA);
        if (unidades < 0) {
            throw new IllegalArgumentException("El numero de botellas no puede ser negativo");
        }
        this.unidades = unidades;
    }

    public int getUnidades() {
        return unidades;
    }

    @Override
    public String getCodigo() {
        return CODIGO;
    }

    @Override
    public String getNombre() {
        return "IRBPNR";
    }

    @Override
    public String getCodigoPorcentaje() {
        return "5001";
    }

    @Override
    public boolean esAdValorem() {
        return false;
    }

    @Override
    public BigDecimal calcularValor() {
        return TARIFA_POR_BOTELLA
                .multiply(BigDecimal.valueOf(unidades))
                .setScale(Validaciones.ESCALA_MONETARIA, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return String.format("%s: %d botellas x %s = %s",
                getNombre(), unidades, TARIFA_POR_BOTELLA, calcularValor());
    }
}
