import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class MetodosLb {

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

}
