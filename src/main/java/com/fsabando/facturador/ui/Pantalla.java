package com.fsabando.facturador.ui;

import com.fsabando.facturador.catalogo.Ambiente;
import com.fsabando.facturador.catalogo.FormaPago;
import com.fsabando.facturador.catalogo.TarifaIva;
import com.fsabando.facturador.catalogo.TipoEmision;
import com.fsabando.facturador.catalogo.TipoIdentificacion;
import com.fsabando.facturador.modelo.CampoAdicional;
import com.fsabando.facturador.modelo.Cliente;
import com.fsabando.facturador.modelo.ClienteMayorista;
import com.fsabando.facturador.modelo.ClienteMinorista;
import com.fsabando.facturador.modelo.Detalle;
import com.fsabando.facturador.modelo.InfoTributaria;
import com.fsabando.facturador.modelo.Pago;
import com.fsabando.facturador.modelo.comprobante.Factura;
import com.fsabando.facturador.modelo.impuesto.Impuesto;
import com.fsabando.facturador.modelo.impuesto.Iva;
import com.fsabando.facturador.registro.RegistroComprobantes;
import com.fsabando.facturador.registro.RegistroComprobantesEnMemoria;
import com.fsabando.facturador.xml.GeneradorXmlFactura;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Ventana principal de la aplicacion.
 *
 * <p>Recolecta los datos de la factura, mantiene el catalogo de productos
 * con las 5 operaciones CRUD (Semana 5), aplica los eventos de los botones
 * (Semana 6) y al presionar <b>Emitir factura</b> construye el modelo, lo
 * valida y genera el XML del SRI.</p>
 *
 * <p><b>Colecciones y genericos (Semana 5):</b></p>
 * <ul>
 *   <li>{@code ArrayList<Detalle>} para el catalogo de productos.</li>
 *   <li>{@code HashSet<String>} para evitar codigos duplicados en O(1).</li>
 *   <li>{@code HashMap<String, ComprobanteElectronico>} en el registro.</li>
 * </ul>
 */
public class Pantalla extends JFrame {

    private static final DateTimeFormatter FORMATO_FECHA_UI = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RegistroComprobantes registro = new RegistroComprobantesEnMemoria();

    // Catalogo de productos: ArrayList mantiene el orden de captura;
    // HashSet impide agregar dos veces el mismo codigo principal.
    private final List<Detalle> productos = new ArrayList<>();
    private final Set<String> codigosUsados = new HashSet<>();

    // Informacion tributaria
    private JComboBox<Ambiente> cmbAmbiente;
    private JComboBox<TipoEmision> cmbTipoEmision;
    private JTextField txtRazonSocial;
    private JTextField txtNombreComercial;
    private JTextField txtRuc;
    private JTextField txtEstab;
    private JTextField txtPtoEmi;
    private JTextField txtSecuencial;
    private JTextField txtDirMatriz;

    // Informacion de factura
    private JTextField txtFechaEmision;
    private JTextField txtDirEstablecimiento;
    private JTextField txtContribEspecial;
    private JComboBox<String> cmbObligadoContabilidad;

    // Comprador
    private JComboBox<TipoIdentificacion> cmbTipoIdentificacion;
    private JTextField txtRazonSocialComprador;
    private JTextField txtIdentificacionComprador;
    private JTextField txtDireccionComprador;
    private JComboBox<String> cmbTipoCliente;

    // Pago
    private JComboBox<FormaPago> cmbFormaPago;

    // Tabla de detalles
    private JTable tblDetalles;
    private DefaultTableModel modeloDetalles;

    public Pantalla() {
        super("Facturador SRI");
        construirUI();
        cargarValoresDePrueba();
    }

    // ------------------------------------------------------------------
    // Construccion del layout
    // ------------------------------------------------------------------

    private void construirUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Emisor", panelEmisor());
        tabs.addTab("Factura", panelFactura());
        tabs.addTab("Comprador", panelComprador());
        tabs.addTab("Detalles", panelDetalles());
        tabs.addTab("Pago", panelPago());
        add(tabs, BorderLayout.CENTER);

        add(panelAcciones(), BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(720, 520));
    }

    private JPanel panelEmisor() {
        JPanel p = grilla("Informacion tributaria del emisor");
        cmbAmbiente = new JComboBox<>(Ambiente.values());
        cmbTipoEmision = new JComboBox<>(TipoEmision.values());
        txtRazonSocial = new JTextField(24);
        txtNombreComercial = new JTextField(24);
        txtRuc = new JTextField(13);
        txtEstab = new JTextField(3);
        txtPtoEmi = new JTextField(3);
        txtSecuencial = new JTextField(9);
        txtDirMatriz = new JTextField(24);

        agregarFila(p, "Ambiente:", cmbAmbiente);
        agregarFila(p, "Tipo de emision:", cmbTipoEmision);
        agregarFila(p, "Razon social:", txtRazonSocial);
        agregarFila(p, "Nombre comercial:", txtNombreComercial);
        agregarFila(p, "RUC (13 digitos, termina en 001):", txtRuc);
        agregarFila(p, "Establecimiento (3 digitos):", txtEstab);
        agregarFila(p, "Punto de emision (3 digitos):", txtPtoEmi);
        agregarFila(p, "Secuencial (9 digitos):", txtSecuencial);
        agregarFila(p, "Direccion matriz:", txtDirMatriz);
        return p;
    }

    private JPanel panelFactura() {
        JPanel p = grilla("Informacion de la factura");
        txtFechaEmision = new JTextField(LocalDate.now().format(FORMATO_FECHA_UI), 10);
        txtDirEstablecimiento = new JTextField(24);
        txtContribEspecial = new JTextField(8);
        cmbObligadoContabilidad = new JComboBox<>(new String[]{"NO", "SI"});

        agregarFila(p, "Fecha de emision (dd/MM/yyyy):", txtFechaEmision);
        agregarFila(p, "Direccion establecimiento:", txtDirEstablecimiento);
        agregarFila(p, "Contribuyente especial:", txtContribEspecial);
        agregarFila(p, "Obligado a contabilidad:", cmbObligadoContabilidad);
        return p;
    }

    private JPanel panelComprador() {
        JPanel p = grilla("Datos del comprador");
        cmbTipoIdentificacion = new JComboBox<>(TipoIdentificacion.values());
        txtRazonSocialComprador = new JTextField(24);
        txtIdentificacionComprador = new JTextField(13);
        txtDireccionComprador = new JTextField(24);
        cmbTipoCliente = new JComboBox<>(new String[]{"Minorista", "Mayorista"});

        agregarFila(p, "Tipo de identificacion:", cmbTipoIdentificacion);
        agregarFila(p, "Razon social comprador:", txtRazonSocialComprador);
        agregarFila(p, "Identificacion comprador:", txtIdentificacionComprador);
        agregarFila(p, "Direccion comprador:", txtDireccionComprador);
        agregarFila(p, "Tipo de cliente (polimorfismo):", cmbTipoCliente);
        return p;
    }

    private JPanel panelDetalles() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBorder(BorderFactory.createTitledBorder(
                "Catalogo de productos (CRUD sobre ArrayList + HashSet)"));

        modeloDetalles = new DefaultTableModel(
                new Object[]{"Codigo", "Descripcion", "Cantidad", "P. Unitario",
                        "Descuento", "IVA", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblDetalles = new JTable(modeloDetalles);
        tblDetalles.setFillsViewportHeight(true);
        tblDetalles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        p.add(new JScrollPane(tblDetalles), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAgregar = new JButton("Agregar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnBuscar = new JButton("Buscar");
        JButton btnLimpiar = new JButton("Limpiar tabla");
        btnAgregar.addActionListener(e -> abrirDialogoDetalle(null));
        btnEditar.addActionListener(e -> editarDetalleSeleccionado());
        btnEliminar.addActionListener(e -> eliminarDetalleSeleccionado());
        btnBuscar.addActionListener(e -> buscarProducto());
        btnLimpiar.addActionListener(e -> {
            productos.clear();
            codigosUsados.clear();
            modeloDetalles.setRowCount(0);
        });
        botones.add(btnAgregar);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnBuscar);
        botones.add(btnLimpiar);
        p.add(botones, BorderLayout.SOUTH);
        return p;
    }

    private JPanel panelPago() {
        JPanel p = grilla("Forma de pago");
        cmbFormaPago = new JComboBox<>(FormaPago.values());
        agregarFila(p, "Forma de pago:", cmbFormaPago);
        return p;
    }

    private JPanel panelAcciones() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnEmitir = new JButton("Emitir factura y generar XML");
        JButton btnListar = new JButton("Listar comprobantes emitidos");
        btnEmitir.addActionListener(e -> emitirFactura());
        btnListar.addActionListener(e -> listarComprobantes());
        p.add(btnListar);
        p.add(btnEmitir);
        return p;
    }

    private JPanel grilla(String titulo) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder(titulo));
        return p;
    }

    private void agregarFila(JPanel panel, String etiqueta, JComponent campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        c.gridy = panel.getComponentCount() / 2;

        c.gridx = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(etiqueta), c);

        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campo, c);
    }

    // ------------------------------------------------------------------
    // Dialogo para agregar un detalle (producto)
    // ------------------------------------------------------------------

    private void abrirDialogoDetalle(Detalle existente) {
        boolean esEdicion = existente != null;

        JTextField txtCodigo = new JTextField(10);
        JTextField txtDescripcion = new JTextField(20);
        JTextField txtCantidad = new JTextField("1", 6);
        JTextField txtPrecio = new JTextField("0.00", 8);
        JTextField txtDescuento = new JTextField("0.00", 8);
        JComboBox<TarifaIva> cmbIva = new JComboBox<>(new TarifaIva[]{
                TarifaIva.CERO, TarifaIva.CINCO, TarifaIva.DOCE, TarifaIva.TRECE,
                TarifaIva.CATORCE, TarifaIva.QUINCE
        });
        cmbIva.setSelectedItem(TarifaIva.QUINCE);
        JTextField txtDetAdicNombre = new JTextField(10);
        JTextField txtDetAdicValor = new JTextField(10);

        if (esEdicion) {
            txtCodigo.setText(existente.getCodigoPrincipal());
            txtDescripcion.setText(existente.getDescripcion());
            txtCantidad.setText(existente.getCantidad().toPlainString());
            txtPrecio.setText(existente.getPrecioUnitario().toPlainString());
            txtDescuento.setText(existente.getDescuento().toPlainString());
            cmbIva.setSelectedItem(tarifaDe(existente));
            if (!existente.getDetallesAdicionales().isEmpty()) {
                CampoAdicional ca = existente.getDetallesAdicionales().get(0);
                txtDetAdicNombre.setText(ca.getNombre());
                txtDetAdicValor.setText(ca.getValor());
            }
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Codigo principal:"));
        form.add(txtCodigo);
        form.add(new JLabel("Descripcion:"));
        form.add(txtDescripcion);
        form.add(new JLabel("Cantidad:"));
        form.add(txtCantidad);
        form.add(new JLabel("Precio unitario:"));
        form.add(txtPrecio);
        form.add(new JLabel("Descuento:"));
        form.add(txtDescuento);
        form.add(new JLabel("Tarifa IVA:"));
        form.add(cmbIva);
        form.add(new JLabel("Detalle adicional (nombre):"));
        form.add(txtDetAdicNombre);
        form.add(new JLabel("Detalle adicional (valor):"));
        form.add(txtDetAdicValor);

        String titulo = esEdicion ? "Editar producto" : "Agregar producto al catalogo";
        int opcion = JOptionPane.showConfirmDialog(this, form, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String codigo = txtCodigo.getText().trim();
            String codigoOriginal = esEdicion ? existente.getCodigoPrincipal() : null;
            boolean codigoCambio = esEdicion && !codigo.equals(codigoOriginal);
            if ((!esEdicion || codigoCambio) && codigosUsados.contains(codigo)) {
                throw new IllegalArgumentException(
                        "Ya existe un producto con el codigo '" + codigo + "' en el catalogo");
            }

            Detalle nuevo = new Detalle(
                    codigo,
                    txtDescripcion.getText(),
                    new BigDecimal(txtCantidad.getText()),
                    new BigDecimal(txtPrecio.getText()));
            nuevo.setDescuento(new BigDecimal(txtDescuento.getText()));
            TarifaIva tarifa = (TarifaIva) cmbIva.getSelectedItem();
            nuevo.agregarIva(tarifa);
            if (!txtDetAdicNombre.getText().trim().isEmpty()
                    && !txtDetAdicValor.getText().trim().isEmpty()) {
                nuevo.agregarDetalleAdicional(txtDetAdicNombre.getText(), txtDetAdicValor.getText());
            }

            if (esEdicion) {
                int idx = productos.indexOf(existente);
                productos.set(idx, nuevo);
                codigosUsados.remove(codigoOriginal);
                codigosUsados.add(codigo);
                escribirFila(idx, nuevo, tarifa);
            } else {
                productos.add(nuevo);
                codigosUsados.add(codigo);
                modeloDetalles.addRow(filaDe(nuevo, tarifa));
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar el producto: " + ex.getMessage(),
                    "Error de validacion", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarDetalleSeleccionado() {
        int fila = tblDetalles.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para editar.",
                    "Nada seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        abrirDialogoDetalle(productos.get(fila));
    }

    private void eliminarDetalleSeleccionado() {
        int fila = tblDetalles.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar.",
                    "Nada seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Detalle removido = productos.remove(fila);
        codigosUsados.remove(removido.getCodigoPrincipal());
        modeloDetalles.removeRow(fila);
    }

    private void buscarProducto() {
        String texto = JOptionPane.showInputDialog(this,
                "Codigo o descripcion a buscar:", "Buscar producto",
                JOptionPane.QUESTION_MESSAGE);
        if (texto == null || texto.trim().isEmpty()) {
            return;
        }
        String aguja = texto.trim().toLowerCase();
        for (int i = 0; i < productos.size(); i++) {
            Detalle d = productos.get(i);
            if (d.getCodigoPrincipal().toLowerCase().contains(aguja)
                    || d.getDescripcion().toLowerCase().contains(aguja)) {
                tblDetalles.setRowSelectionInterval(i, i);
                tblDetalles.scrollRectToVisible(tblDetalles.getCellRect(i, 0, true));
                return;
            }
        }
        JOptionPane.showMessageDialog(this,
                "No se encontro ningun producto que coincida con: " + texto,
                "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
    }

    private Object[] filaDe(Detalle d, TarifaIva tarifa) {
        return new Object[]{
                d.getCodigoPrincipal(),
                d.getDescripcion(),
                d.getCantidad().toPlainString(),
                d.getPrecioUnitario().toPlainString(),
                d.getDescuento().toPlainString(),
                tarifa.getPorcentaje() + "%",
                d.getTotalLinea().toPlainString()
        };
    }

    private void escribirFila(int fila, Detalle d, TarifaIva tarifa) {
        Object[] valores = filaDe(d, tarifa);
        for (int c = 0; c < valores.length; c++) {
            modeloDetalles.setValueAt(valores[c], fila, c);
        }
    }

    private TarifaIva tarifaDe(Detalle d) {
        for (Impuesto imp : d.getImpuestos()) {
            if (imp instanceof Iva iva) {
                return iva.getTarifaIva();
            }
        }
        return TarifaIva.QUINCE;
    }

    // ------------------------------------------------------------------
    // Emitir factura y generar XML
    // ------------------------------------------------------------------

    private void emitirFactura() {
        try {
            if (productos.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Agregue al menos un producto antes de emitir la factura.",
                        "Sin productos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            InfoTributaria info = new InfoTributaria(
                    (Ambiente) cmbAmbiente.getSelectedItem(),
                    (TipoEmision) cmbTipoEmision.getSelectedItem(),
                    txtRazonSocial.getText(),
                    txtRuc.getText(),
                    txtEstab.getText(),
                    txtPtoEmi.getText(),
                    txtSecuencial.getText(),
                    txtDirMatriz.getText());
            info.setNombreComercial(txtNombreComercial.getText());

            Cliente cliente = construirCliente();

            LocalDate fecha = LocalDate.parse(txtFechaEmision.getText(), FORMATO_FECHA_UI);
            Factura factura = new Factura(info, fecha, cliente);
            factura.setDirEstablecimiento(txtDirEstablecimiento.getText());
            factura.setContribuyenteEspecial(txtContribEspecial.getText());
            factura.setObligadoContabilidad("SI".equals(cmbObligadoContabilidad.getSelectedItem()));

            for (Detalle d : productos) {
                factura.agregarDetalle(d);
            }
            factura.agregarPago(new Pago((FormaPago) cmbFormaPago.getSelectedItem(),
                    factura.getImporteTotal()));

            String clave = registro.emitir(factura);
            String xml = GeneradorXmlFactura.generar(factura);

            guardarXml(xml, clave);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo emitir la factura: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Cliente construirCliente() {
        TipoIdentificacion tipo = (TipoIdentificacion) cmbTipoIdentificacion.getSelectedItem();
        String identificacion = txtIdentificacionComprador.getText();
        String razonSocial = txtRazonSocialComprador.getText();
        String tipoCliente = (String) cmbTipoCliente.getSelectedItem();

        Cliente cliente;
        if ("Mayorista".equals(tipoCliente)) {
            cliente = new ClienteMayorista(tipo, identificacion, razonSocial,
                    new BigDecimal("10.00"), new BigDecimal("1000.00"));
        } else {
            cliente = new ClienteMinorista(tipo, identificacion, razonSocial);
        }
        String direccion = txtDireccionComprador.getText();
        if (direccion != null && !direccion.trim().isEmpty()) {
            cliente.setDireccion(direccion);
        }
        return cliente;
    }

    private void guardarXml(String xml, String claveAcceso) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(claveAcceso + ".xml"));
        chooser.setDialogTitle("Guardar XML de factura");
        int opcion = chooser.showSaveDialog(this);
        if (opcion != JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this,
                    "Factura emitida en memoria.\nClave de acceso: " + claveAcceso,
                    "Emitida", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        File destino = chooser.getSelectedFile();
        try (FileWriter writer = new FileWriter(destino, java.nio.charset.StandardCharsets.UTF_8)) {
            writer.write(xml);
            writer.flush();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo escribir el archivo: " + ex.getMessage(),
                    "Error de E/S", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,
                "Factura emitida correctamente.\nClave de acceso:\n" + claveAcceso
                        + "\n\nXML guardado en:\n" + destino.getAbsolutePath(),
                "Emitida", JOptionPane.INFORMATION_MESSAGE);
    }

    private void listarComprobantes() {
        if (registro.cantidad() == 0) {
            JOptionPane.showMessageDialog(this, "Aun no se ha emitido ningun comprobante.",
                    "Registro vacio", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Comprobantes emitidos: ").append(registro.cantidad()).append("\n\n");
        registro.listar().forEach(c -> sb.append("- ").append(c).append("\n"));
        JTextArea area = new JTextArea(sb.toString(), 12, 60);
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Registro de comprobantes", JOptionPane.INFORMATION_MESSAGE);
    }

    // ------------------------------------------------------------------
    // Datos de prueba para acelerar la demostracion
    // ------------------------------------------------------------------

    private void cargarValoresDePrueba() {
        txtRazonSocial.setText("MI EMPRESA S.A.");
        txtNombreComercial.setText("Mi Empresa");
        txtRuc.setText("1790012345001");
        txtEstab.setText("001");
        txtPtoEmi.setText("001");
        txtSecuencial.setText("000000001");
        txtDirMatriz.setText("Av. Amazonas N39-123 y Naciones Unidas");
        txtDirEstablecimiento.setText("Av. Amazonas N39-123");
        cmbTipoIdentificacion.setSelectedItem(TipoIdentificacion.CEDULA);
        txtRazonSocialComprador.setText("Juan Perez");
        txtIdentificacionComprador.setText("1710034065");
        txtDireccionComprador.setText("Av. Republica del Salvador");
    }
}
