//package corrector;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class BlocDeTexto extends JFrame {
    private final JTextArea texto = new JTextArea();
    private final JFileChooser selector = new JFileChooser();
    private final JLabel contador = new JLabel("Palabras: 0");

    private final int numPreg;
    private final int totalPreg;

    public BlocDeTexto(int numPreg, int totalPreg) {

        this.numPreg = numPreg;
        this.totalPreg = totalPreg;

        setTitle("Respondiendo a :  " + Datos.nombreFch);
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
        JMenu archivo = new JMenu("Archivo");

        //JMenuItem nuevo = new JMenuItem("Nuevo");
        //JMenuItem abrir = new JMenuItem("Abrir");
        JMenuItem guardar = new JMenuItem("Guardar");

        //nuevo.addActionListener(e -> texto.setText(""));
        //abrir.addActionListener(e -> abrirArchivo());
        guardar.addActionListener(e -> guardarSalir());

        //archivo.add(nuevo);
        //archivo.add(abrir);
        archivo.add(guardar);

        barra.add(archivo);
        setJMenuBar(barra);
    }

    private void actualizarContador() {
        String contenido = texto.getText().strip();

        int palabras = contenido.isEmpty()
                ? 0
                : contenido.split("\\s+").length;

        contador.setText("Palabras: " + palabras);
    }

    private void abrirArchivo() {
        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            cargarArchivo(selector.getSelectedFile());
        }
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
            mostrarError("No se pudo abrir el archivo.");
        }

    }

    private void guardarSalir() {
        Datos.respUsuario  = texto.getText();
        MetodosLb m = new MetodosLb();
        Datos d = new Datos();
        d.setHoraFin(m.horaActual());

        try {
            m.escribirInforme();
            System.exit(0);
        } catch (IOException | SecurityException e) {
            mostrarError("No se pudo guardar el informe:\n" + e.getMessage());
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
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
