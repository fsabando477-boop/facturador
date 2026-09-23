package com.fsabando.facturador.modelo;

import com.fsabando.facturador.catalogo.TipoIdentificacion;
import com.fsabando.facturador.comun.Validaciones;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Cliente con contrato de distribucion firmado con el emisor.
 *
 * <pre>
 *   descuento = porcentaje contractual  ( entre 5 % y 20 % )
 *             + 2 %  si el monto supera el cupo pactado
 * </pre>
 */
public class ClienteMayorista extends Cliente {

    public static final BigDecimal DESCUENTO_MINIMO = new BigDecimal("5.00");
    public static final BigDecimal DESCUENTO_MAXIMO = new BigDecimal("20.00");
    public static final BigDecimal ADICIONAL_POR_CUPO = new BigDecimal("2.00");

    private BigDecimal descuentoContractual;
    private BigDecimal cupoPactado;

    public ClienteMayorista(TipoIdentificacion tipoIdentificacion, String identificacion,
                            String razonSocial, BigDecimal descuentoContractual,
                            BigDecimal cupoPactado) {
        super(tipoIdentificacion, identificacion, razonSocial);
        setDescuentoContractual(descuentoContractual);
        setCupoPactado(cupoPactado);
    }

    @Override
    public String getTipoCliente() {
        return "Mayorista";
    }

    @Override
    public BigDecimal calcularDescuento(BigDecimal montoBruto) {
        return aplicarPorcentaje(montoBruto, porcentajeAplicable(montoBruto));
    }

    public BigDecimal porcentajeAplicable(BigDecimal montoBruto) {
        if (montoBruto != null && montoBruto.compareTo(cupoPactado) > 0) {
            return descuentoContractual.add(ADICIONAL_POR_CUPO);
        }
        return descuentoContractual;
    }

    public boolean superaCupo(BigDecimal montoBruto) {
        return montoBruto != null && montoBruto.compareTo(cupoPactado) > 0;
    }

    public BigDecimal getDescuentoContractual() {
        return descuentoContractual;
    }

    public final void setDescuentoContractual(BigDecimal descuentoContractual) {
        Validaciones.obligatorio(descuentoContractual, "descuentoContractual");
        if (descuentoContractual.compareTo(DESCUENTO_MINIMO) < 0
                || descuentoContractual.compareTo(DESCUENTO_MAXIMO) > 0) {
            throw new IllegalArgumentException(String.format(
                    "El descuento contractual debe estar entre %s%% y %s%% (recibido: %s%%)",
                    DESCUENTO_MINIMO, DESCUENTO_MAXIMO, descuentoContractual));
        }
        this.descuentoContractual = descuentoContractual.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getCupoPactado() {
        return cupoPactado;
    }

    public final void setCupoPactado(BigDecimal cupoPactado) {
        this.cupoPactado = Validaciones.decimal(cupoPactado, "cupoPactado");
    }

    @Override
    public String toString() {
        return super.toString() + " - " + getTipoCliente()
                + " (contrato " + descuentoContractual + "%, cupo " + cupoPactado + ")";
    }
}
