package com.fsabando.facturador.modelo;

import com.fsabando.facturador.catalogo.TarifaIva;
import com.fsabando.facturador.comun.Validaciones;
import com.fsabando.facturador.modelo.impuesto.Impuesto;
import com.fsabando.facturador.modelo.impuesto.Iva;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Linea de detalle del comprobante (elemento {@code detalle} del XSD).
 *
 * <p><b>Composicion.</b> Un detalle contiene una lista de {@link Impuesto};
 * si la linea desaparece, sus impuestos desaparecen con ella.</p>
 *
 * <p><b>Encapsulamiento.</b> {@code precioTotalSinImpuesto} no se almacena:
 * se <b>calcula</b>. Y {@link #getImpuestos()} devuelve una lista de solo
 * lectura para que nadie pueda agregar impuestos saltandose las validaciones.</p>
 */
public class Detalle {

    private String codigoPrincipal;
    private String codigoAuxiliar;
    private String descripcion;
    private String unidadMedida;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuento;
    private final List<Impuesto> impuestos = new ArrayList<>();
    private final List<CampoAdicional> detallesAdicionales = new ArrayList<>();

    public Detalle(String codigoPrincipal, String descripcion,
                   BigDecimal cantidad, BigDecimal precioUnitario) {
        setCodigoPrincipal(codigoPrincipal);
        setDescripcion(descripcion);
        setCantidad(cantidad);
        setPrecioUnitario(precioUnitario);
        setDescuento(BigDecimal.ZERO);
    }

    public String getCodigoPrincipal() {
        return codigoPrincipal;
    }

    public final void setCodigoPrincipal(String codigoPrincipal) {
        this.codigoPrincipal = Validaciones.cadena(codigoPrincipal, "codigoPrincipal", 1, 25);
    }

    public String getCodigoAuxiliar() {
        return codigoAuxiliar;
    }

    public final void setCodigoAuxiliar(String codigoAuxiliar) {
        this.codigoAuxiliar = Validaciones.cadenaOpcional(codigoAuxiliar, "codigoAuxiliar", 1, 25);
    }

    public String getDescripcion() {
        return descripcion;
    }

    public final void setDescripcion(String descripcion) {
        this.descripcion = Validaciones.cadena(descripcion, "descripcion", 1, 300);
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public final void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = Validaciones.cadenaOpcional(unidadMedida, "unidadMedida", 1, 50);
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public final void setCantidad(BigDecimal cantidad) {
        this.cantidad = Validaciones.decimal(cantidad, "cantidad");
        sincronizarBasesImponibles();
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public final void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = Validaciones.decimal(precioUnitario, "precioUnitario");
        sincronizarBasesImponibles();
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public final void setDescuento(BigDecimal descuento) {
        BigDecimal valor = Validaciones.decimal(descuento, "descuento");
        if (valor.compareTo(getSubtotalBruto()) > 0) {
            throw new IllegalArgumentException(
                    "El descuento (" + valor + ") supera el subtotal de la linea (" + getSubtotalBruto() + ")");
        }
        this.descuento = valor;
        sincronizarBasesImponibles();
    }

    public BigDecimal getSubtotalBruto() {
        return Validaciones.decimal(cantidad.multiply(precioUnitario), "subtotal");
    }

    /** Atributo derivado: {@code precioTotalSinImpuesto} del XSD. */
    public BigDecimal getPrecioTotalSinImpuesto() {
        return Validaciones.decimal(getSubtotalBruto().subtract(descuento), "precioTotalSinImpuesto");
    }

    public List<Impuesto> getImpuestos() {
        return Collections.unmodifiableList(impuestos);
    }

    public Detalle agregarImpuesto(Impuesto impuesto) {
        Validaciones.obligatorio(impuesto, "impuesto");
        if (impuesto.esAdValorem()) {
            impuesto.setBaseImponible(getPrecioTotalSinImpuesto());
        }
        impuestos.add(impuesto);
        return this;
    }

    public Detalle agregarIva(TarifaIva tarifa) {
        return agregarImpuesto(new Iva(getPrecioTotalSinImpuesto(), tarifa));
    }

    public List<CampoAdicional> getDetallesAdicionales() {
        return Collections.unmodifiableList(detallesAdicionales);
    }

    public Detalle agregarDetalleAdicional(String nombre, String valor) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return this;
        }
        detallesAdicionales.add(new CampoAdicional(nombre, valor));
        return this;
    }

    /**
     * Suma polimorfica: el bucle no sabe si esta sumando IVA, ICE o IRBPNR,
     * cada objeto sabe calcularse solo.
     */
    public BigDecimal getTotalImpuestos() {
        BigDecimal total = BigDecimal.ZERO;
        for (Impuesto impuesto : impuestos) {
            total = total.add(impuesto.calcularValor());
        }
        return Validaciones.decimal(total, "totalImpuestos");
    }

    public BigDecimal getTotalLinea() {
        return getPrecioTotalSinImpuesto().add(getTotalImpuestos());
    }

    private void sincronizarBasesImponibles() {
        if (cantidad == null || precioUnitario == null || descuento == null) {
            return;
        }
        BigDecimal base = getPrecioTotalSinImpuesto();
        for (Impuesto impuesto : impuestos) {
            if (impuesto.esAdValorem()) {
                impuesto.setBaseImponible(base);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%-10s %-30s %6s x %8s = %10s",
                codigoPrincipal, descripcion, cantidad, precioUnitario, getPrecioTotalSinImpuesto());
    }
}
