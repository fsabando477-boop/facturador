package com.fsabando.facturador.modelo.impuesto;

import com.fsabando.facturador.catalogo.TarifaIva;
import com.fsabando.facturador.comun.Validaciones;

import java.math.BigDecimal;

/**
 * Impuesto al Valor Agregado. Codigo 2 en el XSD.
 */
public class Iva extends Impuesto {

    public static final String CODIGO = "2";

    private final TarifaIva tarifaIva;

    public Iva(BigDecimal baseImponible, TarifaIva tarifaIva) {
        super(baseImponible, Validaciones.obligatorio(tarifaIva, "tarifaIva").getPorcentaje());
        this.tarifaIva = tarifaIva;
    }

    public Iva(String baseImponible, TarifaIva tarifaIva) {
        this(new BigDecimal(baseImponible), tarifaIva);
    }

    public static Iva general(BigDecimal baseImponible) {
        return new Iva(baseImponible, TarifaIva.QUINCE);
    }

    public TarifaIva getTarifaIva() {
        return tarifaIva;
    }

    @Override
    public String getCodigo() {
        return CODIGO;
    }

    @Override
    public String getNombre() {
        return "IVA";
    }

    @Override
    public String getCodigoPorcentaje() {
        return tarifaIva.getCodigoPorcentaje();
    }

    @Override
    public BigDecimal calcularValor() {
        return aplicarPorcentaje();
    }
}
