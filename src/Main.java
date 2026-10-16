import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import vista.VentanaLogin;

/**
 * Punto de entrada de la aplicación.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Si falla, se usa el look and feel por defecto de Java (no es grave)
            }
            new VentanaLogin().setVisible(true);
        });
    }
}
