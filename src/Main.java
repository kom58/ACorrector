import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
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
        Datos.inicializar();

        String usuarioActual;

        do {
            usuarioActual = JOptionPane.showInputDialog(
                    null,
                    "Introduce tu nombre:",
                    "Inicio de sesión",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (usuarioActual == null) {
                return;
            }

            usuarioActual = usuarioActual.trim();

            if (usuarioActual.isEmpty()) {
                JOptionPane.showMessageDialog(
                        null,
                        "El nombre no puede estar vacío.",
                        "Nombre no válido",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        } while (usuarioActual.isEmpty());

        Datos.usuarioActual = usuarioActual;

        JOptionPane.showMessageDialog(
                null,
                "¡Bienvenido, " + Datos.usuarioActual + "!",
                "Bienvenida",
                JOptionPane.INFORMATION_MESSAGE
        );

        File archivoHtm = seleccionarArchivoHtm();
        if (archivoHtm == null) {
            return;
        }

        Datos d = new Datos();
        Datos.nombreFch = archivoHtm.getName();
        Datos.archivoInicialFch = archivoHtm.getAbsolutePath();
        d.setArchivoInicialFch(archivoHtm.getAbsolutePath());               /// !!!

        abrirEnNavegador(archivoHtm);

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

    private static void abrirEnNavegador(File archivoHtm) {
        if (!Desktop.isDesktopSupported()
                || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            JOptionPane.showMessageDialog(
                    null,
                    "No se puede abrir el navegador predeterminado.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            Desktop.getDesktop().browse(archivoHtm.toURI());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "No se pudo abrir el archivo en el navegador.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private static File seleccionarArchivoHtm() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Selecciona un archivo HTML o PDF");
        selector.setAcceptAllFileFilterUsed(false);
        selector.setFileFilter(
                new FileNameExtensionFilter(
                        "Archivos HTML y PDF (*.htm, *.html, *.pdf, *,jpg, *,gif, *.png)",
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
}
