package com.fsabando.facturador.modelo.comprobante;

import com.fsabando.facturador.catalogo.TipoComprobante;
import com.fsabando.facturador.comun.Validaciones;
import com.fsabando.facturador.modelo.Cliente;
import com.fsabando.facturador.modelo.Detalle;
import com.fsabando.facturador.modelo.InfoTributaria;
import com.fsabando.facturador.modelo.Pago;
import com.fsabando.facturador.modelo.TotalImpuesto;
import com.fsabando.facturador.modelo.impuesto.Impuesto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Factura, comprobante de codigo {@code 01}. Elemento raiz {@code <factura>}
 * del XSD.
 *
 * <p><b>Herencia.</b> Extiende {@link ComprobanteElectronico} y aporta lo que
 * distingue a una factura: lineas de detalle, formas de pago y propina.</p>
 *
 * <p><b>Composicion.</b> La factura contiene sus {@link Detalle} y sus
 * {@link Pago}; ambas listas son {@code final} y privadas.</p>
 */
public class Factura extends ComprobanteElectronico {

    private final List<Detalle> detalles = new ArrayList<>();
    private final List<Pago> pagos = new ArrayList<>();
    private BigDecimal propina = new BigDecimal("0.00");
    private String moneda = "DOLAR";

    public Factura(InfoTributaria infoTributaria, LocalDate fechaEmision, Cliente cliente) {
        super(infoTributaria, fechaEmision, cliente);
    }

    @Override
    public TipoComprobante getTipoComprobante() {
        return TipoComprobante.FACTURA;
    }

    public List<Detalle> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    public Factura agregarDetalle(Detalle detalle) {
        detalles.add(Validaciones.obligatorio(detalle, "detalle"));
        return this;
    }

    public List<Pago> getPagos() {
        return Collections.unmodifiableList(pagos);
    }

    public Factura agregarPago(Pago pago) {
        pagos.add(Validaciones.obligatorio(pago, "pago"));
        return this;
    }

    @Override
    public BigDecimal getTotalSinImpuestos() {
        BigDecimal total = BigDecimal.ZERO;
        for (Detalle detalle : detalles) {
            total = total.add(detalle.getPrecioTotalSinImpuesto());
        }
        return Validaciones.decimal(total, "totalSinImpuestos");
    }

    public BigDecimal getTotalDescuento() {
        BigDecimal total = BigDecimal.ZERO;
        for (Detalle detalle : detalles) {
            total = total.add(detalle.getDescuento());
        }
        return Validaciones.decimal(total, "totalDescuento");
    }

    public BigDecimal getSubtotalBruto() {
        BigDecimal total = BigDecimal.ZERO;
        for (Detalle detalle : detalles) {
            total = total.add(detalle.getSubtotalBruto());
        }
        return Validaciones.decimal(total, "subtotalBruto");
    }

    /**
     * Aqui esta el polimorfismo del descuento: la factura no pregunta de que
     * tipo es el cliente ni usa {@code instanceof}. Le pide el calculo y
     * responde el minorista o el mayorista segun el objeto real.
     */
    public BigDecimal getDescuentoDeCliente() {
        return getCliente().calcularDescuento(getSubtotalBruto());
    }

    /**
     * Bloque {@code totalConImpuestos}: agrupa los impuestos de todas las
     * lineas por codigo y codigo de porcentaje.
     */
    public List<TotalImpuesto> getTotalConImpuestos() {
        List<TotalImpuesto> totales = new ArrayList<>();
        for (Detalle detalle : detalles) {
            for (Impuesto impuesto : detalle.getImpuestos()) {
                TotalImpuesto acumulado = buscarAcumulado(totales, impuesto);
                if (acumulado == null) {
                    totales.add(new TotalImpuesto(impuesto));
                } else {
                    acumulado.acumular(impuesto);
                }
            }
        }
        return Collections.unmodifiableList(totales);
    }

    public BigDecimal getTotalImpuestos() {
        BigDecimal total = BigDecimal.ZERO;
        for (TotalImpuesto totalImpuesto : getTotalConImpuestos()) {
            total = total.add(totalImpuesto.getValor());
        }
        return Validaciones.decimal(total, "totalImpuestos");
    }

    @Override
    public BigDecimal getImporteTotal() {
        return Validaciones.decimal(
                getTotalSinImpuestos().add(getTotalImpuestos()).add(propina), "importeTotal");
    }

    public BigDecimal getTotalPagado() {
        BigDecimal total = BigDecimal.ZERO;
        for (Pago pago : pagos) {
            total = total.add(pago.getTotal());
        }
        return Validaciones.decimal(total, "totalPagado");
    }

    @Override
    public void validar() {
        super.validar();
        if (detalles.isEmpty()) {
            throw new IllegalStateException(
                    "El XSD exige al menos un detalle (minOccurs=1) en la factura");
        }
        if (!pagos.isEmpty() && getTotalPagado().compareTo(getImporteTotal()) != 0) {
            throw new IllegalStateException(String.format(
                    "Las formas de pago suman %s pero el importe total es %s",
                    getTotalPagado(), getImporteTotal()));
        }
    }

    public BigDecimal getPropina() {
        return propina;
    }

    public void setPropina(BigDecimal propina) {
        this.propina = Validaciones.decimal(propina, "propina");
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = Validaciones.cadena(moneda, "moneda", 1, 15);
    }

    private TotalImpuesto buscarAcumulado(List<TotalImpuesto> totales, Impuesto impuesto) {
        for (TotalImpuesto candidato : totales) {
            if (candidato.corresponde(impuesto)) {
                return candidato;
            }
        }
        return null;
    }
}
