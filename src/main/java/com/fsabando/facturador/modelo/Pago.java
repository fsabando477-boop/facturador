package com.fsabando.facturador.modelo;

import com.fsabando.facturador.catalogo.FormaPago;
import com.fsabando.facturador.comun.Validaciones;

import java.math.BigDecimal;

/**
 * Forma de pago aplicada al comprobante.
 * Corresponde al elemento {@code pago} dentro del
 * {@code <xsd:complexType name="pagos">}.
 */
public class Pago {

    private FormaPago formaPago;
    private BigDecimal total;
    private Integer plazo;
    private String unidadTiempo;

    public Pago(FormaPago formaPago, BigDecimal total) {
        setFormaPago(formaPago);
        setTotal(total);
    }

    public FormaPago getFormaPago() {
        return formaPago;
    }

    public final void setFormaPago(FormaPago formaPago) {
        this.formaPago = Validaciones.obligatorio(formaPago, "formaPago");
    }

    public BigDecimal getTotal() {
        return total;
    }

    public final void setTotal(BigDecimal total) {
        this.total = Validaciones.decimal(total, "total del pago");
    }

    public Integer getPlazo() {
        return plazo;
    }

    public void setPlazo(Integer plazo) {
        if (plazo != null && plazo < 0) {
            throw new IllegalArgumentException("El plazo no puede ser negativo");
        }
        this.plazo = plazo;
    }

    public String getUnidadTiempo() {
        return unidadTiempo;
    }

    public void setUnidadTiempo(String unidadTiempo) {
        this.unidadTiempo = Validaciones.cadenaOpcional(unidadTiempo, "unidadTiempo", 1, 10);
    }

    public boolean esCredito() {
        return plazo != null && plazo > 0;
    }

    @Override
    public String toString() {
        return formaPago.getDescripcion() + ": " + total;
    }
}
