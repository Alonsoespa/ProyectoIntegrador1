package vista;

/**
 * Cualquier panel que necesite recargar datos de la base de datos
 * cada vez que el usuario navega hacia él implementa esta interfaz.
 */
public interface PanelActualizable {
    void actualizarDatos();
}
