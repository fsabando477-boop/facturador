package com.fsabando.facturador.registro;

import com.fsabando.facturador.comun.Validaciones;
import com.fsabando.facturador.modelo.comprobante.ComprobanteElectronico;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementacion del registro que guarda los comprobantes en memoria.
 *
 * <p>Usa un {@link LinkedHashMap} generico {@code <String, ComprobanteElectronico>}
 * (Semana 5) para que {@link #listar()} devuelva los comprobantes en el mismo
 * orden en que se emitieron.</p>
 */
public class RegistroComprobantesEnMemoria implements RegistroComprobantes {

    private final Map<String, ComprobanteElectronico> porClaveAcceso = new LinkedHashMap<>();

    @Override
    public String emitir(ComprobanteElectronico comprobante) {
        Validaciones.obligatorio(comprobante, "comprobante");

        comprobante.validar();

        String clave = comprobante.getInfoTributaria().getClaveAcceso();
        if (clave == null) {
            clave = comprobante.generarClaveAcceso();
        }
        if (porClaveAcceso.containsKey(clave)) {
            throw new IllegalArgumentException(
                    "Ya existe un comprobante emitido con la clave de acceso " + clave);
        }
        porClaveAcceso.put(clave, comprobante);
        return clave;
    }

    @Override
    public ComprobanteElectronico buscarPorClaveAcceso(String claveAcceso) {
        if (claveAcceso == null) {
            throw new IllegalArgumentException("La clave de acceso no puede ser null");
        }
        return porClaveAcceso.get(claveAcceso);
    }

    @Override
    public List<ComprobanteElectronico> listar() {
        return Collections.unmodifiableList(new ArrayList<>(porClaveAcceso.values()));
    }
}
