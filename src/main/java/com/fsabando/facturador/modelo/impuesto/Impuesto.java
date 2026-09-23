package com.fsabando.facturador.modelo.impuesto;

import com.fsabando.facturador.comun.Validaciones;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Clase base de todo impuesto aplicable a una linea de detalle.
 * Corresponde al {@code <xsd:complexType name="impuesto">} del XSD.
 *
 * <p><b>Herencia.</b> El XSD restringe el elemento {@code codigo} al
 * patron [235]: 2 = IVA, 3 = ICE, 5 = IRBPNR. Esos tres valores son justamente
 * las tres subclases concretas. La clase declara {@code abstract} lo que cambia
 * entre ellas ({@link #getCodigo()}, {@link #getNombre()},
 * {@link #calcularValor()}) y concreta lo que comparten.</p>
 */
public abstract class Impuesto {

    private BigDecimal baseImponible;
    private BigDecimal tarifa;

    protected Impuesto(BigDecimal baseImponible, BigDecimal tarifa) {
        setBaseImponible(baseImponible);
        setTarifa(tarifa);
    }

    public abstract String getCodigo();

    public abstract String getNombre();

    public abstract String getCodigoPorcentaje();

    /**
     * Metodo polimorfico: IVA e ICE lo resuelven como porcentaje sobre la base,
     * el IRBPNR como tarifa fija por unidad.
     */
    public abstract BigDecimal calcularValor();

    public BigDecimal getBaseImponible() {
        return baseImponible;
    }

    public final void setBaseImponible(BigDecimal baseImponible) {
        this.baseImponible = Validaciones.decimal(baseImponible, "baseImponible");
    }

    public BigDecimal getTarifa() {
        return tarifa;
    }

    public final void setTarifa(BigDecimal tarifa) {
        Validaciones.obligatorio(tarifa, "tarifa");
        if (tarifa.signum() < 0) {
            throw new IllegalArgumentException("La tarifa no puede ser negativa");
        }
        this.tarifa = tarifa.setScale(2, RoundingMode.HALF_UP);
    }

    protected final BigDecimal aplicarPorcentaje() {
        return getBaseImponible()
                .multiply(getTarifa())
                .divide(new BigDecimal("100"), Validaciones.ESCALA_MONETARIA, RoundingMode.HALF_UP);
    }

    public boolean esAdValorem() {
        return true;
    }

    public boolean mismoConcepto(Impuesto otro) {
        return otro != null
                && getCodigo().equals(otro.getCodigo())
                && getCodigoPorcentaje().equals(otro.getCodigoPorcentaje());
    }

    @Override
    public String toString() {
        return String.format("%s %s%% sobre %s = %s",
                getNombre(), getTarifa(), getBaseImponible(), calcularValor());
    }
}
