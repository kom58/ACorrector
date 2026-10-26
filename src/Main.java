import java.awt.Desktop;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.Locale;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileView;

public class Main {
    private static final String SISTEMA_OPERATIVO = System.getProperty(
            "os.name",
            ""
    ).toLowerCase(Locale.ROOT);
    private static final boolean USA_SELECTOR_NATIVO =
            SISTEMA_OPERATIVO.contains("mac")
                    || SISTEMA_OPERATIVO.contains("win");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::iniciarAplicacion);
    }

    private static void iniciarAplicacion() {

        MetodosLb m = new MetodosLb();
        Path archivoAcrIni = m.rutaArchivoAcrIni();
        Datos.inicializar();

        try {
            if (Files.notExists(archivoAcrIni)) {
                if (!m.crearArchivoAcrIni()) {
                    return;
                }
            }
        } catch (IOException | SecurityException e) {
            JOptionPane.showMessageDialog(
                    null,
                    mensajeSeguro(m, "1010", "No se pudo crear")
                            + " acr.ini:\n" + e.getMessage(),
                    mensajeSeguro(m, "1011", "Error de configuración"),
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            m.leerArchivoAcrIni();
        } catch (IOException | SecurityException e) {
            JOptionPane.showMessageDialog(
                    null,
                    mensajeSeguro(m, "1012", "No se pudo leer")
                            + " acr.ini:\n" + e.getMessage(),
                    mensajeSeguro(m, "1011", "Error de configuración"),
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Datos d = new Datos();
        d.setHoraInicio(m.horaActual());

        final String tituloInicioSesion;
        final String mensajeNombre;
        final String mensajeNombreVacio;
        final String tituloNombreNoValido;
        final String mensajeBienvenida;
        final String tituloBienvenida;
        final String errorAplicacionPredeterminada;
        final String tituloError;
        final String errorAbrirArchivo;
        final String tituloSeleccionarArchivo;
        final String descripcionArchivos;
        final String formatoNoPermitido;
        try {
            tituloInicioSesion = m.leerMensajeIdioma("1001");
            mensajeNombre = m.leerMensajeIdioma("1002");
            mensajeNombreVacio = m.leerMensajeIdioma("1003");
            tituloNombreNoValido = m.leerMensajeIdioma("1004");
            mensajeBienvenida = m.leerMensajeIdioma("1005");
            tituloBienvenida = m.leerMensajeIdioma("1006");
            errorAplicacionPredeterminada = m.leerMensajeIdioma("1030");
            tituloError = m.leerMensajeIdioma("1031");
            errorAbrirArchivo = m.leerMensajeIdioma("1040");
            tituloSeleccionarArchivo = m.leerMensajeIdioma("1050");
            descripcionArchivos = m.leerMensajeIdioma("1051");
            formatoNoPermitido = mensajeSeguro(
                    m,
                    "1092",
                    "El formato del archivo seleccionado no está permitido."
            );
        } catch (IOException | SecurityException e) {
            JOptionPane.showMessageDialog(
                    null,
                    mensajeSeguro(
                            m,
                            "1020",
                            "No se pudieron cargar los mensajes del idioma"
                    ) + ":\n" + e.getMessage(),
                    mensajeSeguro(m, "1021", "Error de idioma"),
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String usuarioActual;

        do {
            usuarioActual = (String) JOptionPane.showInputDialog(
                    null,
                    mensajeNombre,
                    tituloInicioSesion,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    null,
                    d.getUltimoUsuario()
            );

            if (usuarioActual == null) {
                return;
            }

            usuarioActual = usuarioActual.trim();

            if (usuarioActual.isEmpty()) {
                JOptionPane.showMessageDialog(
                        null,
                        mensajeNombreVacio,
                        tituloNombreNoValido,
                        JOptionPane.WARNING_MESSAGE
                );
            }
        } while (usuarioActual.isEmpty());

        try {
            m.guardarUltimoUsuario(usuarioActual);
        } catch (IOException | SecurityException e) {
            JOptionPane.showMessageDialog(
                    null,
                    mensajeSeguro(m, "1013", "No se pudo actualizar")
                            + " acr.ini:\n" + e.getMessage(),
                    mensajeSeguro(m, "1011", "Error de configuración"),
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        d.setUsuarioActual(usuarioActual);

        JOptionPane.showMessageDialog(
                null,
                mensajeBienvenida + ", " + Datos.usuarioActual + "!",
                tituloBienvenida,
                JOptionPane.INFORMATION_MESSAGE
        );

        File archivo = seleccionarArchivo(
                tituloSeleccionarArchivo,
                descripcionArchivos,
                formatoNoPermitido
        );
        if (archivo == null) {
            return;
        }

        //Datos d = new Datos();
        Datos.nombreFch = archivo.getName();
        Datos.archivoInicialFch = archivo.getAbsolutePath();
        d.setArchivoInicialFch(archivo.getAbsolutePath());               // !!!

        abrirArchivo(
                archivo,
                errorAplicacionPredeterminada,
                tituloError,
                errorAbrirArchivo
        );

        BlocDeTexto blocDeTexto = new BlocDeTexto(1, 1);
        mostrarBlocEnPrimerPlano(blocDeTexto);
    }

    private static void mostrarBlocEnPrimerPlano(BlocDeTexto blocDeTexto) {
        Timer temporizador = new Timer(800, e -> {
            blocDeTexto.setAlwaysOnTop(true);
            blocDeTexto.setVisible(true);
            blocDeTexto.toFront();
            blocDeTexto.requestFocus();

            Timer quitarPrimerPlano = new Timer(
                    1000,
                    evento -> blocDeTexto.setAlwaysOnTop(false)
            );
            quitarPrimerPlano.setRepeats(false);
            quitarPrimerPlano.start();
        });

        temporizador.setRepeats(false);
        temporizador.start();
    }

    private static void abrirArchivo(
            File archivo,
            String errorAplicacionPredeterminada,
            String tituloError,
            String errorAbrirArchivo
    ) {
        if (!archivo.isFile()) {
            JOptionPane.showMessageDialog(
                    null,
                    errorAbrirArchivo + ":\n" + archivo.getAbsolutePath(),
                    tituloError,
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (!Desktop.isDesktopSupported()
                || !Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            JOptionPane.showMessageDialog(
                    null,
                    errorAplicacionPredeterminada,
                    tituloError,
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            Desktop.getDesktop().open(archivo);
        } catch (IOException | SecurityException e) {
            String detalle = e.getMessage();
            JOptionPane.showMessageDialog(
                    null,
                    errorAbrirArchivo
                            + ":\n"
                            + archivo.getAbsolutePath()
                            + (detalle == null || detalle.isBlank()
                                    ? ""
                                    : "\n" + detalle),
                    tituloError,
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private static File seleccionarArchivo(
            String tituloSeleccionarArchivo,
            String descripcionArchivos,
            String formatoNoPermitido
    ) {
        String titulo = tituloSeleccionarArchivo + " HTML o PDF";
        String[] extensiones = {
                "htm",
                "html",
                "pdf",
                "jpg",
                "gif",
                "png"
        };

        File archivo;
        if (USA_SELECTOR_NATIVO) {
            archivo = seleccionarArchivoNativo(titulo, extensiones);
        } else {
            JFileChooser selector = new JFileChooser();
            normalizarNombresSelector(selector);
            selector.setDialogTitle(titulo);
            selector.setAcceptAllFileFilterUsed(false);
            selector.setFileFilter(
                    new FileNameExtensionFilter(
                            descripcionArchivos
                                    + ": HTML, PDF, JPG, GIF, PNG "
                                    + "(*.htm, *.html, *.pdf, *.jpg, *.gif, *.png)",
                            "htm",
                            "html",
                            "pdf",
                            "jpg",
                            "gif",
                            "png"
                    )
            );

            if (selector.showOpenDialog(null)
                    != JFileChooser.APPROVE_OPTION) {
                return null;
            }
            archivo = selector.getSelectedFile();
        }

        if (archivo == null) {
            return null;
        }
        if (!tieneExtensionPermitida(archivo.getName(), extensiones)) {
            JOptionPane.showMessageDialog(
                    null,
                    formatoNoPermitido,
                    titulo,
                    JOptionPane.WARNING_MESSAGE
            );
            return null;
        }
        return archivo;
    }

    private static void normalizarNombresSelector(JFileChooser selector) {
        selector.setFileView(new FileView() {
            @Override
            public String getName(File archivo) {
                String nombre = selector.getFileSystemView()
                        .getSystemDisplayName(archivo);
                if (nombre == null || nombre.isBlank()) {
                    return null;
                }
                return Normalizer.normalize(nombre, Normalizer.Form.NFC);
            }
        });
    }

    private static File seleccionarArchivoNativo(
            String titulo,
            String... extensiones
    ) {
        FileDialog dialogo = new FileDialog(
                (Frame) null,
                titulo,
                FileDialog.LOAD
        );
        dialogo.setMultipleMode(false);

        File directorioPersonal = new File(
                System.getProperty("user.home", ".")
        );
        if (directorioPersonal.isDirectory()) {
            dialogo.setDirectory(directorioPersonal.getAbsolutePath());
        }

        dialogo.setFilenameFilter(
                (directorio, nombre) -> tieneExtensionPermitida(
                        nombre,
                        extensiones
                )
        );
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

    private static String mensajeSeguro(
            MetodosLb metodos,
            String codigo,
            String mensajePredeterminado
    ) {
        try {
            return metodos.leerMensajeIdioma(codigo);
        } catch (IOException | SecurityException e) {
            return mensajePredeterminado;
        }
    }
}
