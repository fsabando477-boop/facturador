package com.fsabando.facturador.registro;

import com.fsabando.facturador.modelo.comprobante.ComprobanteElectronico;

import java.util.List;

/**
 * Contrato para emitir comprobantes y consultarlos por su clave de acceso.
 *
 * <p><b>Interfaz y no clase abstracta.</b> Aqui no hay estado que compartir:
 * solo se fija <i>que</i> operaciones debe ofrecer un registro. Cada
 * implementacion decide <i>como</i> las resuelve y donde guarda los datos.</p>
 *
 * <p>Comparese con {@code Cliente}, que si guarda estado comun. Las dos formas
 * de abstraccion conviven en el proyecto porque resuelven problemas distintos.</p>
 */
public interface RegistroComprobantes {

    /**
     * Valida el comprobante, le genera la clave de acceso si aun no la tiene y
     * lo registra.
     *
     * @throws IllegalArgumentException si la clave de acceso ya estaba emitida
     */
    String emitir(ComprobanteElectronico comprobante);

    ComprobanteElectronico buscarPorClaveAcceso(String claveAcceso);

    List<ComprobanteElectronico> listar();

    /** Metodo {@code default}: la interfaz puede traer implementacion. */
    default boolean estaEmitido(String claveAcceso) {
        return buscarPorClaveAcceso(claveAcceso) != null;
    }

    default int cantidad() {
        return listar().size();
    }
}
