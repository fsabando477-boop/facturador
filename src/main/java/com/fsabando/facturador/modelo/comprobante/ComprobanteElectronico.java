package com.fsabando.facturador.modelo.comprobante;

import com.fsabando.facturador.catalogo.TipoComprobante;
import com.fsabando.facturador.comun.Validaciones;
import com.fsabando.facturador.modelo.CampoAdicional;
import com.fsabando.facturador.modelo.Cliente;
import com.fsabando.facturador.modelo.InfoTributaria;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Clase base de todos los comprobantes electronicos del SRI.
 *
 * <p><b>Por que existe.</b> Factura, nota de credito, nota de debito y
 * comprobante de retencion comparten literalmente el mismo bloque
 * {@code infoTributaria}, la misma fecha de emision, el mismo cliente y el
 * mismo algoritmo de clave de acceso. Ponerlo aqui es el caso de uso de la
 * herencia.</p>
 */
public abstract class ComprobanteElectronico {

    protected static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private InfoTributaria infoTributaria;
    private LocalDate fechaEmision;
    private Cliente cliente;
    private String dirEstablecimiento;
    private String contribuyenteEspecial;
    private boolean obligadoContabilidad;
    private String codigoNumerico = "12345678";
    private final List<CampoAdicional> infoAdicional = new ArrayList<>();

    protected ComprobanteElectronico(InfoTributaria infoTributaria, LocalDate fechaEmision,
                                     Cliente cliente) {
        setInfoTributaria(infoTributaria);
        setFechaEmision(fechaEmision);
        setCliente(cliente);
        this.infoTributaria.setCodDoc(getTipoComprobante().getCodigo());
    }

    public abstract TipoComprobante getTipoComprobante();

    public abstract BigDecimal getTotalSinImpuestos();

    public abstract BigDecimal getImporteTotal();

    /**
     * Genera la clave de acceso de 49 digitos y la asigna al bloque tributario.
     *
     * <p>Estructura del SRI:</p>
     * <pre>
     *   fecha de emision (8)  ddmmaaaa
     *   tipo de comprobante (2)
     *   RUC del emisor (13)
     *   ambiente (1)
     *   serie: establecimiento + punto de emision (6)
     *   secuencial (9)
     *   codigo numerico (8)
     *   tipo de emision (1)
     *   digito verificador modulo 11 (1)
     * </pre>
     */
    public String generarClaveAcceso() {
        String base = fechaEmision.format(DateTimeFormatter.ofPattern("ddMMyyyy"))
                + getTipoComprobante().getCodigo()
                + infoTributaria.getRuc()
                + infoTributaria.getAmbiente().getCodigo()
                + infoTributaria.getEstab()
                + infoTributaria.getPtoEmi()
                + infoTributaria.getSecuencial()
                + codigoNumerico
                + infoTributaria.getTipoEmision().getCodigo();

        if (base.length() != 48) {
            throw new IllegalStateException(
                    "La clave de acceso previa al verificador debe tener 48 digitos, tiene " + base.length());
        }
        String clave = base + Validaciones.modulo11(base);
        infoTributaria.setClaveAcceso(clave);
        return clave;
    }

    public void validar() {
        if (fechaEmision.isAfter(LocalDate.now())) {
            throw new IllegalStateException("La fecha de emision no puede ser futura");
        }
        if (getImporteTotal().signum() < 0) {
            throw new IllegalStateException("El importe total no puede ser negativo");
        }
    }

    public InfoTributaria getInfoTributaria() {
        return infoTributaria;
    }

    public final void setInfoTributaria(InfoTributaria infoTributaria) {
        this.infoTributaria = Validaciones.obligatorio(infoTributaria, "infoTributaria");
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public final void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = Validaciones.obligatorio(fechaEmision, "fechaEmision");
    }

    public String getFechaEmisionFormateada() {
        return fechaEmision.format(FORMATO_FECHA);
    }

    public Cliente getCliente() {
        return cliente;
    }

    public final void setCliente(Cliente cliente) {
        this.cliente = Validaciones.obligatorio(cliente, "cliente");
    }

    public String getDirEstablecimiento() {
        return dirEstablecimiento;
    }

    public void setDirEstablecimiento(String dirEstablecimiento) {
        this.dirEstablecimiento = Validaciones.cadenaOpcional(dirEstablecimiento, "dirEstablecimiento", 1, 300);
    }

    public String getContribuyenteEspecial() {
        return contribuyenteEspecial;
    }

    public void setContribuyenteEspecial(String contribuyenteEspecial) {
        this.contribuyenteEspecial = Validaciones.cadenaOpcional(
                contribuyenteEspecial, "contribuyenteEspecial", 3, 13);
    }

    public boolean isObligadoContabilidad() {
        return obligadoContabilidad;
    }

    public void setObligadoContabilidad(boolean obligadoContabilidad) {
        this.obligadoContabilidad = obligadoContabilidad;
    }

    public String getObligadoContabilidadTexto() {
        return obligadoContabilidad ? "SI" : "NO";
    }

    public String getCodigoNumerico() {
        return codigoNumerico;
    }

    public void setCodigoNumerico(String codigoNumerico) {
        this.codigoNumerico = Validaciones.patron(codigoNumerico, "codigoNumerico", "[0-9]{8}");
    }

    public List<CampoAdicional> getInfoAdicional() {
        return Collections.unmodifiableList(infoAdicional);
    }

    public void agregarCampoAdicional(String nombre, String valor) {
        if (infoAdicional.size() >= CampoAdicional.MAXIMO_POR_COMPROBANTE) {
            throw new IllegalStateException("El XSD admite como maximo "
                    + CampoAdicional.MAXIMO_POR_COMPROBANTE + " campos adicionales");
        }
        infoAdicional.add(new CampoAdicional(nombre, valor));
    }

    @Override
    public String toString() {
        return getTipoComprobante().getDescripcion() + " "
                + infoTributaria.getNumeroComprobante()
                + " - " + cliente.getRazonSocial()
                + " - USD " + getImporteTotal();
    }
}
