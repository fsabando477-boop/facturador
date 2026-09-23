package com.fsabando.facturador.modelo;

import com.fsabando.facturador.comun.Validaciones;

/**
 * Par nombre/valor del bloque {@code infoAdicional} del XSD.
 * El esquema permite un maximo de 15 campos por comprobante.
 *
 * <p>Inmutable: sus dos atributos son {@code final} y no expone setters.</p>
 */
public class CampoAdicional {

    public static final int MAXIMO_POR_COMPROBANTE = 15;

    private final String nombre;
    private final String valor;

    public CampoAdicional(String nombre, String valor) {
        this.nombre = Validaciones.cadena(nombre, "nombre del campo adicional", 1, 300);
        this.valor = Validaciones.cadena(valor, "valor del campo adicional", 1, 300);
    }

    public String getNombre() {
        return nombre;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return nombre + ": " + valor;
    }
}
