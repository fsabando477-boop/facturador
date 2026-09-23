package com.fsabando.facturador.modelo;

import com.fsabando.facturador.catalogo.TipoIdentificacion;
import com.fsabando.facturador.comun.Validaciones;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Datos del adquirente del comprobante.
 *
 * <p><b>Abstraccion.</b> Un cliente generico no existe en el negocio:
 * existe el cliente minorista y existe el mayorista, y cada uno negocia sus
 * descuentos de forma distinta. Por eso la clase es {@code abstract} y declara
 * {@link #calcularDescuento(BigDecimal)} sin cuerpo.</p>
 *
 * <p><b>Clase abstracta y no interfaz</b> porque comparte estado (identificacion,
 * razon social, direccion, correo) y validaciones. Interfaz cuando solo se
 * comparte comportamiento, clase abstracta cuando ademas se comparte estado.</p>
 */
public abstract class Cliente {

    public static final String CONSUMIDOR_FINAL = "9999999999999";

    private TipoIdentificacion tipoIdentificacion;
    private String identificacion;
    private String razonSocial;
    private String direccion;
    private String correo;

    protected Cliente(TipoIdentificacion tipoIdentificacion, String identificacion, String razonSocial) {
        setTipoIdentificacion(tipoIdentificacion);
        setIdentificacion(identificacion);
        setRazonSocial(razonSocial);
    }

    public abstract String getTipoCliente();

    /**
     * Metodo polimorfico: quien lo invoca no sabe si esta hablando con un
     * minorista o con un mayorista.
     */
    public abstract BigDecimal calcularDescuento(BigDecimal montoBruto);

    protected final BigDecimal aplicarPorcentaje(BigDecimal montoBruto, BigDecimal porcentaje) {
        Validaciones.obligatorio(montoBruto, "montoBruto");
        Validaciones.obligatorio(porcentaje, "porcentaje");
        BigDecimal descuento = montoBruto
                .multiply(porcentaje)
                .divide(new BigDecimal("100"), Validaciones.ESCALA_MONETARIA, RoundingMode.HALF_UP);
        return descuento.min(Validaciones.decimal(montoBruto, "montoBruto"));
    }

    public TipoIdentificacion getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public final void setTipoIdentificacion(TipoIdentificacion tipoIdentificacion) {
        this.tipoIdentificacion = Validaciones.obligatorio(tipoIdentificacion, "tipoIdentificacion");
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public final void setIdentificacion(String identificacion) {
        String valor = Validaciones.cadena(identificacion, "identificacionComprador", 1, 20);
        if (valor.length() > tipoIdentificacion.getLongitudMaxima()) {
            throw new IllegalArgumentException(String.format(
                    "La identificacion de tipo %s admite hasta %d caracteres",
                    tipoIdentificacion.getDescripcion(), tipoIdentificacion.getLongitudMaxima()));
        }
        if (tipoIdentificacion == TipoIdentificacion.CEDULA && !Validaciones.esCedulaValida(valor)) {
            throw new IllegalArgumentException("Cedula invalida (digito verificador): " + valor);
        }
        if (tipoIdentificacion == TipoIdentificacion.RUC && !valor.matches("[0-9]{10}(001|[0-9]{3})")) {
            throw new IllegalArgumentException("RUC del cliente invalido: " + valor);
        }
        this.identificacion = valor;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public final void setRazonSocial(String razonSocial) {
        this.razonSocial = Validaciones.cadena(razonSocial, "razonSocialComprador", 1, 300);
    }

    public String getDireccion() {
        return direccion;
    }

    public final void setDireccion(String direccion) {
        this.direccion = Validaciones.cadenaOpcional(direccion, "direccionComprador", 1, 300);
    }

    public String getCorreo() {
        return correo;
    }

    public final void setCorreo(String correo) {
        String valor = Validaciones.cadenaOpcional(correo, "correo", 1, 100);
        if (valor != null && !valor.matches("[^@\\s]+@[^@\\s]+\\.[A-Za-z]{2,}")) {
            throw new IllegalArgumentException("Correo electronico invalido: " + valor);
        }
        this.correo = valor;
    }

    public boolean esConsumidorFinal() {
        return CONSUMIDOR_FINAL.equals(identificacion);
    }

    @Override
    public String toString() {
        return razonSocial + " (" + tipoIdentificacion.getDescripcion() + " " + identificacion + ")";
    }
}
