package com.fsabando.facturador.modelo;

import com.fsabando.facturador.catalogo.TipoIdentificacion;

import java.math.BigDecimal;

/**
 * Cliente que compra al detalle, sin contrato previo con el emisor.
 *
 * <pre>
 *   menos de  USD 100,00  .......  0 %
 *   de 100,00 a 500,00    .......  3 %
 *   mas de    USD 500,00  .......  5 %
 * </pre>
 */
public class ClienteMinorista extends Cliente {

    public static final BigDecimal UMBRAL_BASICO = new BigDecimal("100.00");
    public static final BigDecimal UMBRAL_PREFERENTE = new BigDecimal("500.00");

    private static final BigDecimal PORCENTAJE_NULO = BigDecimal.ZERO;
    private static final BigDecimal PORCENTAJE_BASICO = new BigDecimal("3");
    private static final BigDecimal PORCENTAJE_PREFERENTE = new BigDecimal("5");

    public ClienteMinorista(TipoIdentificacion tipoIdentificacion, String identificacion,
                            String razonSocial) {
        super(tipoIdentificacion, identificacion, razonSocial);
    }

    /** Metodo de fabrica para el consumidor final del punto de venta. */
    public static ClienteMinorista consumidorFinal() {
        return new ClienteMinorista(TipoIdentificacion.VENTA_CONSUMIDOR_FINAL,
                CONSUMIDOR_FINAL, "CONSUMIDOR FINAL");
    }

    @Override
    public String getTipoCliente() {
        return "Minorista";
    }

    @Override
    public BigDecimal calcularDescuento(BigDecimal montoBruto) {
        return aplicarPorcentaje(montoBruto, porcentajeSegunTramo(montoBruto));
    }

    public BigDecimal porcentajeSegunTramo(BigDecimal montoBruto) {
        if (montoBruto == null || montoBruto.compareTo(UMBRAL_BASICO) < 0) {
            return PORCENTAJE_NULO;
        }
        if (montoBruto.compareTo(UMBRAL_PREFERENTE) <= 0) {
            return PORCENTAJE_BASICO;
        }
        return PORCENTAJE_PREFERENTE;
    }

    @Override
    public String toString() {
        return super.toString() + " - " + getTipoCliente();
    }
}
