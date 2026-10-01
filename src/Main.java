import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {
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
        final String errorNavegador;
        final String tituloError;
        final String errorAbrirArchivo;
        final String tituloSeleccionarArchivo;
        final String descripcionArchivos;
        try {
            tituloInicioSesion = m.leerMensajeIdioma("1001");
            mensajeNombre = m.leerMensajeIdioma("1002");
            mensajeNombreVacio = m.leerMensajeIdioma("1003");
            tituloNombreNoValido = m.leerMensajeIdioma("1004");
            mensajeBienvenida = m.leerMensajeIdioma("1005");
            tituloBienvenida = m.leerMensajeIdioma("1006");
            errorNavegador = m.leerMensajeIdioma("1030");
            tituloError = m.leerMensajeIdioma("1031");
            errorAbrirArchivo = m.leerMensajeIdioma("1040");
            tituloSeleccionarArchivo = m.leerMensajeIdioma("1050");
            descripcionArchivos = m.leerMensajeIdioma("1051");
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
            usuarioActual = JOptionPane.showInputDialog(
                    null,
                    mensajeNombre,
                    tituloInicioSesion,
                    JOptionPane.QUESTION_MESSAGE
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

        Datos.usuarioActual = usuarioActual;

        JOptionPane.showMessageDialog(
                null,
                mensajeBienvenida + ", " + Datos.usuarioActual + "!",
                tituloBienvenida,
                JOptionPane.INFORMATION_MESSAGE
        );

        File archivoHtm = seleccionarArchivoHtm(
                tituloSeleccionarArchivo,
                descripcionArchivos
        );
        if (archivoHtm == null) {
            return;
        }

        //Datos d = new Datos();
        Datos.nombreFch = archivoHtm.getName();
        Datos.archivoInicialFch = archivoHtm.getAbsolutePath();
        d.setArchivoInicialFch(archivoHtm.getAbsolutePath());               /// !!!

        abrirEnNavegador(
                archivoHtm,
                errorNavegador,
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

    private static void abrirEnNavegador(
            File archivoHtm,
            String errorNavegador,
            String tituloError,
            String errorAbrirArchivo
    ) {
        if (!Desktop.isDesktopSupported()
                || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            JOptionPane.showMessageDialog(
                    null,
                    errorNavegador,
                    tituloError,
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            Desktop.getDesktop().browse(archivoHtm.toURI());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    errorAbrirArchivo,
                    tituloError,
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private static File seleccionarArchivoHtm(
            String tituloSeleccionarArchivo,
        String descripcionArchivos
    ) {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle(tituloSeleccionarArchivo + " HTML o PDF");
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

        int resultado = selector.showOpenDialog(null);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            return selector.getSelectedFile();
        }

        return null;
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
