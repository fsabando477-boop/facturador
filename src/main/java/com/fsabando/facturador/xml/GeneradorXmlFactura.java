package com.fsabando.facturador.xml;

import com.fsabando.facturador.modelo.CampoAdicional;
import com.fsabando.facturador.modelo.Detalle;
import com.fsabando.facturador.modelo.InfoTributaria;
import com.fsabando.facturador.modelo.Pago;
import com.fsabando.facturador.modelo.TotalImpuesto;
import com.fsabando.facturador.modelo.comprobante.Factura;
import com.fsabando.facturador.modelo.impuesto.Impuesto;

/**
 * Genera el XML de {@code <factura id="comprobante" version="1.1.0">}
 * segun el esquema factura_V1.1.0.xsd del SRI.
 *
 * <p>Trabaja con {@link StringBuilder} y escapa los cinco caracteres XML para
 * evitar dependencias externas.</p>
 */
public final class GeneradorXmlFactura {

    private static final String VERSION = "1.1.0";

    private GeneradorXmlFactura() {
    }

    public static String generar(Factura factura) {
        StringBuilder xml = new StringBuilder(4096);
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<factura id=\"comprobante\" version=\"").append(VERSION).append("\">\n");
        escribirInfoTributaria(xml, factura);
        escribirInfoFactura(xml, factura);
        escribirDetalles(xml, factura);
        escribirInfoAdicional(xml, factura);
        xml.append("</factura>\n");
        return xml.toString();
    }

    private static void escribirInfoTributaria(StringBuilder xml, Factura factura) {
        InfoTributaria it = factura.getInfoTributaria();
        xml.append("  <infoTributaria>\n");
        elemento(xml, 4, "ambiente", it.getAmbiente().getCodigo());
        elemento(xml, 4, "tipoEmision", it.getTipoEmision().getCodigo());
        elemento(xml, 4, "razonSocial", it.getRazonSocial());
        elementoOpcional(xml, 4, "nombreComercial", it.getNombreComercial());
        elemento(xml, 4, "ruc", it.getRuc());
        elemento(xml, 4, "claveAcceso", it.getClaveAcceso());
        elemento(xml, 4, "codDoc", it.getCodDoc());
        elemento(xml, 4, "estab", it.getEstab());
        elemento(xml, 4, "ptoEmi", it.getPtoEmi());
        elemento(xml, 4, "secuencial", it.getSecuencial());
        elemento(xml, 4, "dirMatriz", it.getDirMatriz());
        elementoOpcional(xml, 4, "agenteRetencion", it.getAgenteRetencion());
        elementoOpcional(xml, 4, "contribuyenteRimpe", it.getLeyendaRimpe());
        xml.append("  </infoTributaria>\n");
    }

    private static void escribirInfoFactura(StringBuilder xml, Factura factura) {
        xml.append("  <infoFactura>\n");
        elemento(xml, 4, "fechaEmision", factura.getFechaEmisionFormateada());
        elementoOpcional(xml, 4, "dirEstablecimiento", factura.getDirEstablecimiento());
        elementoOpcional(xml, 4, "contribuyenteEspecial", factura.getContribuyenteEspecial());
        elemento(xml, 4, "obligadoContabilidad", factura.getObligadoContabilidadTexto());
        elemento(xml, 4, "tipoIdentificacionComprador",
                factura.getCliente().getTipoIdentificacion().getCodigo());
        elemento(xml, 4, "razonSocialComprador", factura.getCliente().getRazonSocial());
        elemento(xml, 4, "identificacionComprador", factura.getCliente().getIdentificacion());
        elementoOpcional(xml, 4, "direccionComprador", factura.getCliente().getDireccion());
        elemento(xml, 4, "totalSinImpuestos", factura.getTotalSinImpuestos().toPlainString());
        elemento(xml, 4, "totalDescuento", factura.getTotalDescuento().toPlainString());
        escribirTotalConImpuestos(xml, factura);
        elemento(xml, 4, "propina", factura.getPropina().toPlainString());
        elemento(xml, 4, "importeTotal", factura.getImporteTotal().toPlainString());
        elemento(xml, 4, "moneda", factura.getMoneda());
        escribirPagos(xml, factura);
        xml.append("  </infoFactura>\n");
    }

    private static void escribirTotalConImpuestos(StringBuilder xml, Factura factura) {
        xml.append("    <totalConImpuestos>\n");
        for (TotalImpuesto ti : factura.getTotalConImpuestos()) {
            xml.append("      <totalImpuesto>\n");
            elemento(xml, 8, "codigo", ti.getCodigo());
            elemento(xml, 8, "codigoPorcentaje", ti.getCodigoPorcentaje());
            elemento(xml, 8, "baseImponible", ti.getBaseImponible().toPlainString());
            elemento(xml, 8, "valor", ti.getValor().toPlainString());
            xml.append("      </totalImpuesto>\n");
        }
        xml.append("    </totalConImpuestos>\n");
    }

    private static void escribirPagos(StringBuilder xml, Factura factura) {
        if (factura.getPagos().isEmpty()) {
            return;
        }
        xml.append("    <pagos>\n");
        for (Pago pago : factura.getPagos()) {
            xml.append("      <pago>\n");
            elemento(xml, 8, "formaPago", pago.getFormaPago().getCodigo());
            elemento(xml, 8, "total", pago.getTotal().toPlainString());
            if (pago.getPlazo() != null) {
                elemento(xml, 8, "plazo", pago.getPlazo().toString());
            }
            elementoOpcional(xml, 8, "unidadTiempo", pago.getUnidadTiempo());
            xml.append("      </pago>\n");
        }
        xml.append("    </pagos>\n");
    }

    private static void escribirDetalles(StringBuilder xml, Factura factura) {
        xml.append("  <detalles>\n");
        for (Detalle detalle : factura.getDetalles()) {
            xml.append("    <detalle>\n");
            elemento(xml, 6, "codigoPrincipal", detalle.getCodigoPrincipal());
            elementoOpcional(xml, 6, "codigoAuxiliar", detalle.getCodigoAuxiliar());
            elemento(xml, 6, "descripcion", detalle.getDescripcion());
            elemento(xml, 6, "cantidad", detalle.getCantidad().toPlainString());
            elemento(xml, 6, "precioUnitario", detalle.getPrecioUnitario().toPlainString());
            elemento(xml, 6, "descuento", detalle.getDescuento().toPlainString());
            elemento(xml, 6, "precioTotalSinImpuesto",
                    detalle.getPrecioTotalSinImpuesto().toPlainString());
            escribirDetallesAdicionales(xml, detalle);
            escribirImpuestosDetalle(xml, detalle);
            xml.append("    </detalle>\n");
        }
        xml.append("  </detalles>\n");
    }

    private static void escribirDetallesAdicionales(StringBuilder xml, Detalle detalle) {
        if (detalle.getDetallesAdicionales().isEmpty()) {
            return;
        }
        xml.append("      <detallesAdicionales>\n");
        for (CampoAdicional ca : detalle.getDetallesAdicionales()) {
            xml.append("        <detAdicional nombre=\"").append(escapar(ca.getNombre()))
                    .append("\" valor=\"").append(escapar(ca.getValor())).append("\"/>\n");
        }
        xml.append("      </detallesAdicionales>\n");
    }

    private static void escribirImpuestosDetalle(StringBuilder xml, Detalle detalle) {
        xml.append("      <impuestos>\n");
        for (Impuesto imp : detalle.getImpuestos()) {
            xml.append("        <impuesto>\n");
            elemento(xml, 10, "codigo", imp.getCodigo());
            elemento(xml, 10, "codigoPorcentaje", imp.getCodigoPorcentaje());
            elemento(xml, 10, "tarifa", imp.getTarifa().toPlainString());
            elemento(xml, 10, "baseImponible", imp.getBaseImponible().toPlainString());
            elemento(xml, 10, "valor", imp.calcularValor().toPlainString());
            xml.append("        </impuesto>\n");
        }
        xml.append("      </impuestos>\n");
    }

    private static void escribirInfoAdicional(StringBuilder xml, Factura factura) {
        if (factura.getInfoAdicional().isEmpty()) {
            return;
        }
        xml.append("  <infoAdicional>\n");
        for (CampoAdicional campo : factura.getInfoAdicional()) {
            xml.append("    <campoAdicional nombre=\"").append(escapar(campo.getNombre()))
                    .append("\">").append(escapar(campo.getValor()))
                    .append("</campoAdicional>\n");
        }
        xml.append("  </infoAdicional>\n");
    }

    private static void elemento(StringBuilder xml, int sangria, String nombre, String valor) {
        for (int i = 0; i < sangria; i++) {
            xml.append(' ');
        }
        xml.append('<').append(nombre).append('>')
                .append(escapar(valor))
                .append("</").append(nombre).append(">\n");
    }

    private static void elementoOpcional(StringBuilder xml, int sangria, String nombre, String valor) {
        if (valor == null || valor.isEmpty()) {
            return;
        }
        elemento(xml, sangria, nombre, valor);
    }

    /** Escapa los cinco caracteres reservados por la especificacion XML. */
    private static String escapar(String texto) {
        if (texto == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(texto.length());
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            switch (c) {
                case '&' -> sb.append("&amp;");
                case '<' -> sb.append("&lt;");
                case '>' -> sb.append("&gt;");
                case '"' -> sb.append("&quot;");
                case '\'' -> sb.append("&apos;");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }
}
