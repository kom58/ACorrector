//package corrector;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;

public class BlocDeTexto extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final boolean ES_MAC_OS = System.getProperty("os.name", "")
            .toLowerCase(Locale.ROOT)
            .contains("mac");
    private static final MetodosLb METODOS = new MetodosLb();
    private final JTextArea texto = new JTextArea();
    private final JFileChooser selector = new JFileChooser();
    private File ultimoDirectorio = obtenerDirectorioPersonal();
    private final JLabel contador = new JLabel();

    private final String etiquetaPalabras;
    private final String tituloError;
    private final String errorAbrirArchivo;
    private final String errorGuardarInforme;

    private final int numPreg;
    private final int totalPreg;

    public BlocDeTexto(int numPreg, int totalPreg) {

        this.numPreg = numPreg;
        this.totalPreg = totalPreg;

        etiquetaPalabras = mensajeSeguro("2001", "Palabras");
        tituloError = mensajeSeguro("1031", "Error");
        errorAbrirArchivo = mensajeSeguro("2020", "No se pudo abrir el archivo");
        errorGuardarInforme = mensajeSeguro("2030", "No se pudo guardar el informe");

        setTitle(mensajeSeguro("2010", "Respondiendo a") + ": " + Datos.nombreFch);
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);         // No deja cerrar !!!!
        setLocationRelativeTo(null);

        // Área de escritura
        texto.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        add(new JScrollPane(texto), BorderLayout.CENTER);

        // Contador de palabras
        contador.setText(etiquetaPalabras + ": 0 " + "     " + Datos.usuarioActual);
        contador.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(contador, BorderLayout.SOUTH);

        texto.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarContador();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarContador();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizarContador();
            }
        });

        // Menú Archivo
        JMenuBar barra = new JMenuBar();
        JMenu archivo = new JMenu(mensajeSeguro("1052", "Archivo"));
        JMenu ayuda = new JMenu(mensajeSeguro("1100", "Ayuda"));

        //JMenuItem nuevo = new JMenuItem("Nuevo");
        //JMenuItem abrir = new JMenuItem("Abrir");
        JMenuItem idioma = new JMenuItem(mensajeSeguro("1007", "Idioma"));
        JMenuItem guardar = new JMenuItem(mensajeSeguro("1053", "Guardar"));

        //nuevo.addActionListener(e -> texto.setText(""));
        //abrir.addActionListener(e -> abrirArchivo());
        idioma.addActionListener(e -> mostrarSelectorIdioma());
        guardar.addActionListener(e -> guardarSalir());

        //archivo.add(nuevo);
        //archivo.add(abrir);
        archivo.add(idioma);
        archivo.add(guardar);

        JMenuItem acercaDe = new JMenuItem(
                mensajeSeguro("1101", "Acerca de")
        );
        acercaDe.addActionListener(e -> mostrarAcercaDe());
        ayuda.add(acercaDe);

        barra.add(archivo);
        barra.add(ayuda);
        setJMenuBar(barra);
    }

    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(
                this,
                "ACorrector\n"
                        + mensajeSeguro("1102", "Versión") + ": "
                        + METODOS.versionACrr(),
                mensajeSeguro("1101", "Acerca de"),
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void mostrarSelectorIdioma() {
        JComboBox<String> selectorIdioma = new JComboBox<>(
                new String[]{
                        "Español",
                        "Català",
                        "Valencià",
                        "Galego",
                        "Euskara",
                        "Français",
                        "English"
                }
        );
        selectorIdioma.setSelectedItem(new Datos().getIdioma());

        int resultado = JOptionPane.showConfirmDialog(
                this,
                selectorIdioma,
                mensajeSeguro("1007", "Idioma"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (resultado != JOptionPane.OK_OPTION) {
            return;
        }

        String idioma = (String) selectorIdioma.getSelectedItem();
        if (idioma == null) {
            return;
        }
        if (idioma.equals(new Datos().getIdioma())) {
            return;
        }

        try {
            METODOS.guardarIdioma(idioma);
            JOptionPane.showMessageDialog(
                    this,
                    mensajeSeguro(
                            "1008",
                            "Se debe reiniciar el programa para que los cambios "
                                    + "de idioma tengan efecto"
                    ),
                    mensajeSeguro("1007", "Idioma"),
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IOException | SecurityException e) {
            JOptionPane.showMessageDialog(
                    this,
                    mensajeSeguro("1013", "No se pudo actualizar")
                            + " acr.ini:\n" + e.getMessage(),
                    mensajeSeguro("1011", "Error de configuración"),
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void actualizarContador() {
        String contenido = texto.getText().strip();

        int palabras = contenido.isEmpty()
                ? 0
                : contenido.split("\\s+").length;

        contador.setText(etiquetaPalabras + ": " + palabras + "     " + Datos.usuarioActual);
    }

    private void abrirArchivo() {
        File archivo = seleccionarArchivo(selector);
        if (archivo != null) {
            cargarArchivo(archivo);
        }
    }

    private File seleccionarArchivo(
            JFileChooser selectorSwing,
            String... extensiones
    ) {
        File archivo;
        if (ES_MAC_OS) {
            archivo = seleccionarArchivoNativo(
                    selectorSwing.getDialogTitle(),
                    extensiones
            );
        } else {
            if (ultimoDirectorio.isDirectory()) {
                selectorSwing.setCurrentDirectory(ultimoDirectorio);
            }
            if (selectorSwing.showOpenDialog(this)
                    != JFileChooser.APPROVE_OPTION) {
                return null;
            }
            archivo = selectorSwing.getSelectedFile();
        }

        if (archivo != null) {
            File directorio = archivo.getParentFile();
            if (directorio != null && directorio.isDirectory()) {
                ultimoDirectorio = directorio;
            }
        }
        return archivo;
    }

    private File seleccionarArchivoNativo(
            String titulo,
            String... extensiones
    ) {
        FileDialog dialogo = new FileDialog(this, titulo, FileDialog.LOAD);
        dialogo.setMultipleMode(false);
        if (ultimoDirectorio.isDirectory()) {
            dialogo.setDirectory(ultimoDirectorio.getAbsolutePath());
        }
        if (extensiones.length > 0) {
            dialogo.setFilenameFilter(
                    (directorio, nombre) -> tieneExtensionPermitida(
                            nombre,
                            extensiones
                    )
            );
        }
        dialogo.setVisible(true);

        String nombre = dialogo.getFile();
        String directorio = dialogo.getDirectory();
        dialogo.dispose();
        if (nombre == null) {
            return null;
        }
        return directorio == null
                ? new File(nombre)
                : new File(directorio, nombre);
    }

    private static boolean tieneExtensionPermitida(
            String nombre,
            String... extensiones
    ) {
        String nombreMinusculas = nombre.toLowerCase(Locale.ROOT);
        for (String extension : extensiones) {
            if (nombreMinusculas.endsWith(
                    "." + extension.toLowerCase(Locale.ROOT)
            )) {
                return true;
            }
        }
        return false;
    }

    private static File obtenerDirectorioPersonal() {
        String rutaPersonal = System.getProperty("user.home", ".");
        File directorioPersonal = new File(rutaPersonal);
        return directorioPersonal.isDirectory()
                ? directorioPersonal
                : new File(".").getAbsoluteFile();
    }

    public void cargarArchivo(File archivo) {

        try {
            String contenido = Files.readString(
                    archivo.toPath(),
                    StandardCharsets.UTF_8
            );

            texto.setText(contenido);
            texto.setCaretPosition(0);
        } catch (IOException e) {
            mostrarError(errorAbrirArchivo);
        }

    }

    private void guardarSalir() {
        Datos.respUsuario  = texto.getText();
        Datos d = new Datos();
        d.setHoraFin(METODOS.horaActual());

        try {
            METODOS.escribirInforme();
            System.exit(0);
        } catch (IOException | SecurityException e) {
            mostrarError(errorGuardarInforme + ":\n" + e.getMessage());
        }

        /*
        if (selector.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Files.writeString(
                        selector.getSelectedFile().toPath(),
                        texto.getText(),
                        StandardCharsets.UTF_8
                );
            } catch (IOException e) {
                mostrarError("No se pudo guardar el archivo.");
            }
        }
         */
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                tituloError,
                JOptionPane.ERROR_MESSAGE
        );
    }

    private String mensajeSeguro(String codigo, String mensajePredeterminado) {
        try {
            return METODOS.leerMensajeIdioma(codigo);
        } catch (IOException | SecurityException e) {
            return mensajePredeterminado;
        }
    }
}
