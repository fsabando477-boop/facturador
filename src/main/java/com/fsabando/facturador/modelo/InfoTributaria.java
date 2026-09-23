package com.fsabando.facturador.modelo;

import com.fsabando.facturador.catalogo.Ambiente;
import com.fsabando.facturador.catalogo.TipoEmision;
import com.fsabando.facturador.comun.Validaciones;

/**
 * Datos del emisor y de identificacion del documento.
 * Corresponde al {@code <xsd:complexType name="infoTributaria">} del XSD.
 *
 * <p>Ejemplo directo de encapsulamiento: todos los atributos son privados y
 * cada {@code set} replica la restriccion declarada en el esquema.</p>
 */
public class InfoTributaria {

    private Ambiente ambiente;
    private TipoEmision tipoEmision;
    private String razonSocial;
    private String nombreComercial;
    private String ruc;
    private String claveAcceso;
    private String codDoc;
    private String estab;
    private String ptoEmi;
    private String secuencial;
    private String dirMatriz;
    private String agenteRetencion;
    private boolean contribuyenteRimpe;

    private static final String LEYENDA_RIMPE = "CONTRIBUYENTE REGIMEN RIMPE";

    public InfoTributaria(Ambiente ambiente, TipoEmision tipoEmision, String razonSocial,
                          String ruc, String estab, String ptoEmi, String secuencial,
                          String dirMatriz) {
        setAmbiente(ambiente);
        setTipoEmision(tipoEmision);
        setRazonSocial(razonSocial);
        setRuc(ruc);
        setEstab(estab);
        setPtoEmi(ptoEmi);
        setSecuencial(secuencial);
        setDirMatriz(dirMatriz);
    }

    public Ambiente getAmbiente() {
        return ambiente;
    }

    public final void setAmbiente(Ambiente ambiente) {
        this.ambiente = Validaciones.obligatorio(ambiente, "ambiente");
    }

    public TipoEmision getTipoEmision() {
        return tipoEmision;
    }

    public final void setTipoEmision(TipoEmision tipoEmision) {
        this.tipoEmision = Validaciones.obligatorio(tipoEmision, "tipoEmision");
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public final void setRazonSocial(String razonSocial) {
        this.razonSocial = Validaciones.cadena(razonSocial, "razonSocial", 1, 300);
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public final void setNombreComercial(String nombreComercial) {
        this.nombreComercial = Validaciones.cadenaOpcional(nombreComercial, "nombreComercial", 1, 300);
    }

    public String getRuc() {
        return ruc;
    }

    public final void setRuc(String ruc) {
        this.ruc = Validaciones.patron(ruc, "ruc", "[0-9]{10}001");
    }

    public String getClaveAcceso() {
        return claveAcceso;
    }

    public final void setClaveAcceso(String claveAcceso) {
        this.claveAcceso = Validaciones.patron(claveAcceso, "claveAcceso", "[0-9]{49}");
    }

    public String getCodDoc() {
        return codDoc;
    }

    public final void setCodDoc(String codDoc) {
        this.codDoc = Validaciones.patron(codDoc, "codDoc", "[0-9]{2}");
    }

    public String getEstab() {
        return estab;
    }

    public final void setEstab(String estab) {
        this.estab = Validaciones.patron(estab, "estab", "[0-9]{3}");
    }

    public String getPtoEmi() {
        return ptoEmi;
    }

    public final void setPtoEmi(String ptoEmi) {
        this.ptoEmi = Validaciones.patron(ptoEmi, "ptoEmi", "[0-9]{3}");
    }

    public String getSecuencial() {
        return secuencial;
    }

    public final void setSecuencial(String secuencial) {
        this.secuencial = Validaciones.patron(secuencial, "secuencial", "[0-9]{9}");
    }

    public String getDirMatriz() {
        return dirMatriz;
    }

    public final void setDirMatriz(String dirMatriz) {
        this.dirMatriz = Validaciones.cadena(dirMatriz, "dirMatriz", 1, 300);
    }

    public String getAgenteRetencion() {
        return agenteRetencion;
    }

    public final void setAgenteRetencion(String agenteRetencion) {
        this.agenteRetencion = agenteRetencion == null
                ? null
                : Validaciones.patron(agenteRetencion, "agenteRetencion", "[0-9]{1,8}");
    }

    public boolean isContribuyenteRimpe() {
        return contribuyenteRimpe;
    }

    public void setContribuyenteRimpe(boolean contribuyenteRimpe) {
        this.contribuyenteRimpe = contribuyenteRimpe;
    }

    public String getLeyendaRimpe() {
        return contribuyenteRimpe ? LEYENDA_RIMPE : null;
    }

    public String getNumeroComprobante() {
        return estab + "-" + ptoEmi + "-" + secuencial;
    }

    @Override
    public String toString() {
        return razonSocial + " (RUC " + ruc + ") - " + getNumeroComprobante();
    }
}
