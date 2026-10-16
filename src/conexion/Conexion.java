package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;


public class Conexion {

    private static final String URL =
    "jdbc:mysql://localhost:3306/proyecto_integrador?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
private static final String USUARIO = "ventas_user";
private static final String CONTRASENA = "ventas123";

    private static Connection conexion = null;

    private Conexion() {
 
    }

    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "No se encontró el driver de MySQL (mysql-connector-j).\n"
                + "Verifica que agregaste el .jar como librería del proyecto.",
                "Error de conexión", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "No se pudo conectar a la base de datos:\n" + e.getMessage(),
                "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
        return conexion;
    }

    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
