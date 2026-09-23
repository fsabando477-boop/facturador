package com.fsabando.facturador;

import com.fsabando.facturador.ui.Pantalla;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Punto de entrada de la aplicacion.
 */
public class Facturador {

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            Pantalla pantalla = new Pantalla();
            pantalla.setLocationRelativeTo(null);
            pantalla.setVisible(true);
        });
    }
}
