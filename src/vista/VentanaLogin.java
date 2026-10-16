package vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Pantalla de inicio de sesión. Se muestra antes que la ventana principal.
 *
 * NOTA: el usuario y la contraseña están fijos en el código
 * (usuario: Rosa / contraseña: 123) solo para fines del proyecto.
 * En un sistema real, los usuarios y contraseñas (encriptadas) se
 * guardarían en una tabla de la base de datos.
 */
public class VentanaLogin extends JFrame {

    private static final String USUARIO_VALIDO = "Rosa";
    private static final String CONTRASENA_VALIDA = "123";

    private JTextField campoUsuario;
    private JPasswordField campoContrasena;
    private JLabel mensajeError;

    public VentanaLogin() {
        super("Iniciar sesión");
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 380);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Estilos.FONDO);

        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearFormulario(), BorderLayout.CENTER);
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel();
        panel.setBackground(Estilos.VERDE_MENU);
        panel.setPreferredSize(new Dimension(0, 90));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Sistema de control de ventas");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Inicia sesión para continuar");
        subtitulo.setFont(Estilos.NORMAL);
        subtitulo.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(titulo);
        panel.add(subtitulo);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel crearFormulario() {
        JPanel caja = new JPanel();
        caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));
        caja.setBackground(Estilos.FONDO);
        caja.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setFont(Estilos.NEGRITA);
        lblUsuario.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        caja.add(lblUsuario);

        campoUsuario = new JTextField();
        campoUsuario.setMaximumSize(new Dimension(340, 32));
        campoUsuario.setAlignmentX(JTextField.LEFT_ALIGNMENT);
        campoUsuario.setBackground(Estilos.GRIS_INPUT);
        caja.add(campoUsuario);
        caja.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel lblContrasena = new JLabel("Contraseña");
        lblContrasena.setFont(Estilos.NEGRITA);
        lblContrasena.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        caja.add(lblContrasena);

        campoContrasena = new JPasswordField();
        campoContrasena.setMaximumSize(new Dimension(340, 32));
        campoContrasena.setAlignmentX(JPasswordField.LEFT_ALIGNMENT);
        campoContrasena.setBackground(Estilos.GRIS_INPUT);
        caja.add(campoContrasena);
        caja.add(Box.createRigidArea(new Dimension(0, 10)));

        mensajeError = new JLabel(" ");
        mensajeError.setForeground(java.awt.Color.RED);
        mensajeError.setFont(Estilos.NORMAL);
        mensajeError.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        caja.add(mensajeError);
        caja.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton botonEntrar = new JButton("Iniciar sesión");
        botonEntrar.setBackground(Estilos.VERDE_BOTON);
        botonEntrar.setFocusPainted(false);
        botonEntrar.setAlignmentX(JButton.CENTER_ALIGNMENT);
        botonEntrar.setMaximumSize(new Dimension(340, 36));
        botonEntrar.addActionListener(e -> intentarLogin());
        caja.add(botonEntrar);


        campoContrasena.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    intentarLogin();
                }
            }
        });

        return caja;
    }

    private void intentarLogin() {
        String usuario = campoUsuario.getText().trim();
        String contrasena = new String(campoContrasena.getPassword());

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mensajeError.setText("Ingresa usuario y contraseña");
            return;
        }

        if (usuario.equals(USUARIO_VALIDO) && contrasena.equals(CONTRASENA_VALIDA)) {
            dispose();
            new VentanaPrincipal().setVisible(true);
        } else {
            mensajeError.setText("Usuario o contraseña incorrectos");
            campoContrasena.setText("");
        }
    }
}
