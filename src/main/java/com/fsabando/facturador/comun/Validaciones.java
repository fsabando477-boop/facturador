package com.fsabando.facturador.comun;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utilidades de validacion que trasladan a codigo Java las restricciones
 * declaradas en el esquema factura_V1.0.0.xsd.
 *
 * <p>Cada metodo corresponde a un tipo de {@code <xsd:restriction>}:
 * {@code minLength} / {@code maxLength} se validan en {@link #cadena},
 * {@code pattern} en {@link #patron} y
 * {@code totalDigits} / {@code fractionDigits} en {@link #decimal}.</p>
 */
public final class Validaciones {

    public static final int ESCALA_MONETARIA = 2;

    private Validaciones() {
    }

    public static <T> T obligatorio(T valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException(campo + " es obligatorio segun el XSD");
        }
        return valor;
    }

    public static String cadena(String valor, String campo, int minimo, int maximo) {
        obligatorio(valor, campo);
        String limpio = valor.trim();
        if (limpio.length() < minimo || limpio.length() > maximo) {
            throw new IllegalArgumentException(String.format(
                    "%s debe tener entre %d y %d caracteres (recibido: %d)",
                    campo, minimo, maximo, limpio.length()));
        }
        if (limpio.indexOf('\n') >= 0) {
            throw new IllegalArgumentException(campo + " no admite saltos de linea");
        }
        return limpio;
    }

    public static String cadenaOpcional(String valor, String campo, int minimo, int maximo) {
        return valor == null || valor.trim().isEmpty() ? null : cadena(valor, campo, minimo, maximo);
    }

    public static String patron(String valor, String campo, String expresion) {
        obligatorio(valor, campo);
        String limpio = valor.trim();
        if (!limpio.matches(expresion)) {
            throw new IllegalArgumentException(String.format(
                    "%s no cumple el patron del XSD %s (recibido: '%s')", campo, expresion, limpio));
        }
        return limpio;
    }

    public static BigDecimal decimal(BigDecimal valor, String campo) {
        obligatorio(valor, campo);
        if (valor.signum() < 0) {
            throw new IllegalArgumentException(campo + " no puede ser negativo");
        }
        BigDecimal redondeado = valor.setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);
        if (redondeado.precision() > 14) {
            throw new IllegalArgumentException(campo + " excede los 14 digitos del XSD");
        }
        return redondeado;
    }

    public static BigDecimal decimal(String valor, String campo) {
        return decimal(new BigDecimal(obligatorio(valor, campo)), campo);
    }

    public static boolean esCedulaValida(String cedula) {
        if (cedula == null || !cedula.matches("[0-9]{10}")) {
            return false;
        }
        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if (provincia < 1 || provincia > 24) {
            return false;
        }
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int digito = Character.getNumericValue(cedula.charAt(i));
            if (i % 2 == 0) {
                digito *= 2;
                if (digito > 9) {
                    digito -= 9;
                }
            }
            suma += digito;
        }
        int verificador = (10 - (suma % 10)) % 10;
        return verificador == Character.getNumericValue(cedula.charAt(9));
    }

    public static int modulo11(String digitos) {
        obligatorio(digitos, "digitos");
        int suma = 0;
        int coeficiente = 2;
        for (int i = digitos.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(digitos.charAt(i)) * coeficiente;
            coeficiente = coeficiente == 7 ? 2 : coeficiente + 1;
        }
        int verificador = 11 - (suma % 11);
        if (verificador == 11) {
            return 0;
        }
        return verificador == 10 ? 1 : verificador;
    }
}
