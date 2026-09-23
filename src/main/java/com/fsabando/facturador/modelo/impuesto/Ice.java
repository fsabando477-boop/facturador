package com.fsabando.facturador.modelo.impuesto;

import com.fsabando.facturador.comun.Validaciones;

import java.math.BigDecimal;

/**
 * Impuesto a los Consumos Especiales. Codigo 3 en el XSD.
 */
public class Ice extends Impuesto {

    public static final String CODIGO = "3";

    private final String codigoPorcentaje;
    private final String grupo;

    public Ice(BigDecimal baseImponible, String codigoPorcentaje, BigDecimal tarifa, String grupo) {
        super(baseImponible, tarifa);
        this.codigoPorcentaje = Validaciones.patron(codigoPorcentaje, "codigoPorcentaje", "[0-9]{1,4}");
        this.grupo = Validaciones.cadena(grupo, "grupo del ICE", 1, 300);
    }

    public String getGrupo() {
        return grupo;
    }

    @Override
    public String getCodigo() {
        return CODIGO;
    }

    @Override
    public String getNombre() {
        return "ICE " + grupo;
    }

    @Override
    public String getCodigoPorcentaje() {
        return codigoPorcentaje;
    }

    @Override
    public BigDecimal calcularValor() {
        return aplicarPorcentaje();
    }
}
