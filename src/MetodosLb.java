import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;
import java.util.regex.Pattern;

public class MetodosLb {

    private static final String NOMBRE_ARCHIVO_CONFIGURACION = "acr.ini";
    private static final Pattern PATRON_EMAIL = Pattern.compile(
            "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"
    );

    public Path rutaArchivoAcrIni() {
        try {
            Path ubicacionAplicacion = Path.of(
                    MetodosLb.class.getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI()
            ).toAbsolutePath().normalize();

            // Al ejecutar un JAR, su carpeta es la carpeta de la aplicacion.
            if (Files.isRegularFile(ubicacionAplicacion)) {
                Path carpetaJar = ubicacionAplicacion.getParent();
                if (carpetaJar != null) {
                    return carpetaJar.resolve(NOMBRE_ARCHIVO_CONFIGURACION);
                }
            }
        } catch (NullPointerException | SecurityException | URISyntaxException e) {
            // Si no se puede obtener la ubicacion del codigo, se usa el
            // directorio desde el que se ha iniciado la aplicacion.
        }

        return Path.of(System.getProperty("user.dir"))
                .toAbsolutePath()
                .normalize()
                .resolve(NOMBRE_ARCHIVO_CONFIGURACION);
    }

    public boolean crearArchivoAcrIni() throws IOException {
        String email = pedirEmailValido();
        if (email == null) {
            return false;
        }

        JComboBox<String> selectorIdioma = new JComboBox<>(
                new String[]{"Español", "Català"}
        );
        int resultado = JOptionPane.showConfirmDialog(
                null,
                new Object[]{"Selecciona el idioma:", selectorIdioma},
                "Idioma",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (resultado != JOptionPane.OK_OPTION) {
            return false;
        }

        String idioma = (String) selectorIdioma.getSelectedItem();
        String contenido = "email=" + email + System.lineSeparator()
                + "idioma=" + idioma + System.lineSeparator();

        Path archivoAcrIni = rutaArchivoAcrIni();
        Files.writeString(
                archivoAcrIni,
                contenido,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE
        );
        return true;
    }

    public String leerArchivoAcrIni() throws IOException {
        String contenido = Files.readString(
                rutaArchivoAcrIni(),
                StandardCharsets.UTF_8
        );

        Datos datos = new Datos();
        for (String linea : contenido.split("\\R")) {
            String[] propiedad = linea.split("=", 2);
            if (propiedad.length != 2) {
                continue;
            }

            String clave = propiedad[0].trim();
            String valor = propiedad[1].trim();
            if (clave.equals("email")) {
                datos.setEmailUsuario(valor);
            } else if (clave.equals("idioma")) {
                datos.setIdioma(valor);
            }
        }

        return contenido;
    }

    private String pedirEmailValido() {
        while (true) {
            String email = JOptionPane.showInputDialog(
                    null,
                    "Introduce tu email:",
                    "Configuración inicial",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (email == null) {
                return null;
            }

            email = email.trim();
            if (PATRON_EMAIL.matcher(email).matches()) {
                return email;
            }

            JOptionPane.showMessageDialog(
                    null,
                    "Introduce un email válido.",
                    "Email no válido",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    public String detectarSistemaOperativo() {

        String sistema;
        String sistemaOperativo = System.getProperty("os.name").toLowerCase();

        if (sistemaOperativo.contains("win")) {
            sistema = "win";
        } else if (sistemaOperativo.contains("mac")) {
            sistema = "mac";
        } else {
            sistema = "otro";
        }

        return sistema;

    }

    public void abrirHTML(String archHtml, boolean conDir) {

        String rutaFichero;
        String sistema = detectarSistemaOperativo();            // Detecta Sistema Operativo

        if (conDir) {
            rutaFichero = archHtml;                             // Ruta completa de la HTML
        } else {
            String ruta = directorioMWL(sistema);               // Comprueba directorio Mac Win Lin
            rutaFichero = ruta + "/" + archHtml;                // Ruta completa de la página HTML
        }

        try {
            // Especifica la ruta de la página HTML
            File htmlFile = new File(rutaFichero);

            if (sistema.equals("mac")) {                                                     // Mac

                // Abre la página HTML en el navegador predeterminado
                Desktop.getDesktop().browse(htmlFile.toURI());

                // Simula una pulsación de tecla para devolver el foco al formulario Java
                Robot robot = new Robot();
                robot.keyPress(KeyEvent.VK_META); // Simula presionar la tecla Command (⌘)
                robot.keyPress(KeyEvent.VK_TAB); // Simula presionar la tecla TAB
                robot.keyRelease(KeyEvent.VK_TAB); // Libera la tecla TAB
                robot.keyRelease(KeyEvent.VK_META); // Libera la tecla Command (⌘)
            }

            if (sistema.equals("win")) {                                                     // Windows

                // Abre la página HTML en el navegador predeterminado
                Desktop.getDesktop().browse(htmlFile.toURI());

                // Crea un marco en blanco para asegurarte de que tu aplicación tenga un foco para volver
                JFrame frame = new JFrame();
                frame.setUndecorated(true); // Sin decoraciones
                frame.setSize(1, 1); // Tamaño mínimo
                frame.setLocationRelativeTo(null); // Centrado en la pantalla
                frame.setAlwaysOnTop(true); // Mantener en primer plano
                frame.setVisible(true);

                // Simula una pulsación de tecla para devolver el foco al formulario Java
                Robot robot = new Robot();
                robot.keyPress(KeyEvent.VK_ALT); // Simula presionar la tecla ALT
                robot.keyPress(KeyEvent.VK_TAB); // Simula presionar la tecla TAB
                robot.keyRelease(KeyEvent.VK_TAB); // Libera la tecla TAB
                robot.keyRelease(KeyEvent.VK_ALT); // Libera la tecla ALT

                // Espera un momento para que el cambio de foco se complete
                Thread.sleep(500);

                // Cierra el marco
                frame.dispose();
            }
        }
        //catch (IOException e) {e.printStackTrace(); }
        //catch (AWTException e) {throw new RuntimeException(e); }
        catch (IOException | InterruptedException | AWTException e) {
            e.printStackTrace();
        }

    }


    public String directorioMWL(String sist) {

        File d;                                      // Comprueba y crea directorios
        String ruta = "";

        if (sist.equals("mac")) {
            String directorio = "/Users/Shared/JCorrector";     // En Mac
            d = new File(directorio);
            if (!d.exists()) d.mkdirs();                        // Si no existe lo crea
            ruta = d.getAbsolutePath();                         // Ruta raíz
        }

        if (sist.equals("win")) {
            String directorio = "C:/Users/Public/JCorrector";     // En Windows
            d = new File(directorio);
            if (!d.exists()) d.mkdirs();                        // Si no existe lo crea
            ruta = d.getAbsolutePath();                         // Ruta raíz
        }

        return ruta;
    }


    public Path escribirInforme() throws IOException {

        Datos d = new Datos();

        if (d.getCarpetaFch() == null || d.getCarpetaFch().isBlank()) {
            throw new IOException("No se ha podido determinar la carpeta del informe.");
        }

        String nombreInforme = nombreArchivoSeguro(d.getUsuarioActual()) + ".lgx";
        final Path rutaFichero;
        try {
            rutaFichero = Path.of(d.getCarpetaFch()).resolve(nombreInforme);
        } catch (InvalidPathException e) {
            throw new IOException("La ruta del informe no es válida.", e);
        }

        StringBuilder txt = new StringBuilder();

        txt.append("\n       *********************************\n\n");
        txt.append("                  ").append(d.getUsuarioActual()).append("\n\n");
        txt.append("                  ").append(fechaActual()).append("\n");
        txt.append("                     ").append(horaActual()).append("\n");
        txt.append("\n            ***********************\n\n");
        txt.append("FICHA    :    ").append(d.getNombreFch()).append("\n\n");
        txt.append("Hora de inicio       : ").append(d.getHoraInicio()).append("\n");
        txt.append("Hora de finalización : ").append(d.getHoraFin()).append("\n\n");
        txt.append("[[[ Respuesta ]]]\n\n");
        txt.append(d.getRespUsuario()).append("\n");

        Files.writeString(
                rutaFichero,
                txt.toString(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

        return rutaFichero;
    }

    private String nombreArchivoSeguro(String nombre) {
        String nombreSeguro = nombre == null ? "" : nombre.trim();

        // Caracteres no admitidos por Windows y caracteres de control.
        nombreSeguro = nombreSeguro.replaceAll("[\\\\/:*?\"<>|\\p{Cntrl}]", "_");
        // Windows tampoco permite que un nombre termine en un punto o espacio.
        nombreSeguro = nombreSeguro.replaceAll("[. ]+$", "");

        if (nombreSeguro.isBlank()) {
            nombreSeguro = "informe";
        }

        // Nombres reservados por Windows, incluso cuando llevan extensión.
        if (nombreSeguro.matches("(?i)CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9]")) {
            nombreSeguro = "_" + nombreSeguro;
        }

        return nombreSeguro;
    }


    public String fechaActual() {

        String fechaAc;
        Calendar ahora = Calendar.getInstance();
        int diaA = ahora.get(Calendar.DAY_OF_MONTH);
        int mesA = ahora.get(Calendar.MONTH) + 1;
        int anoA = ahora.get(Calendar.YEAR);

        fechaAc = String.format("%02d.%02d.%04d", diaA, mesA, anoA);

        return fechaAc;
    }

    public String horaActual() {

        String horaAc;
        Calendar ahora = Calendar.getInstance();
        int horaA = ahora.get(Calendar.HOUR_OF_DAY);
        int minA = ahora.get(Calendar.MINUTE);

        horaAc = String.format("%02d:%02d", horaA, minA);

        return horaAc;
    }

}
