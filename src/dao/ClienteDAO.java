package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Cliente;

/**
 * Acceso a datos para la tabla "clientes".
 * Cada método lanza SQLException para que la pantalla (vista) decida
 * cómo mostrar el error al usuario, igual que los try/except del
 * código original.
 */
public class ClienteDAO {

    public void insertar(Cliente c) throws SQLException {
        String sql = "INSERT INTO clientes (nombre, telefono, direccion) VALUES (?, ?, ?)";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getTelefono());
            ps.setString(3, c.getDireccion());
            ps.executeUpdate();
        }
    }

    public void actualizar(Cliente c) throws SQLException {
        String sql = "UPDATE clientes SET nombre=?, telefono=?, direccion=? WHERE id=?";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getTelefono());
            ps.setString(3, c.getDireccion());
            ps.setInt(4, c.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id=?";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** Devuelve el cliente con ese nombre exacto, o null si no existe. */
    public Cliente buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT id, nombre, telefono, direccion FROM clientes WHERE nombre=?";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(rs.getInt("id"), rs.getString("nombre"),
                            rs.getString("telefono"), rs.getString("direccion"));
                }
            }
        }
        return null;
    }

    public boolean existeNombre(String nombre) throws SQLException {
        return buscarPorNombre(nombre) != null;
    }

    /** true si existe OTRO cliente (id distinto) con ese nombre. Útil al editar. */
    public boolean existeOtroConNombre(String nombre, int idActual) throws SQLException {
        String sql = "SELECT id FROM clientes WHERE nombre=? AND id<>?";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, idActual);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, telefono, direccion FROM clientes ORDER BY nombre";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Cliente(rs.getInt("id"), rs.getString("nombre"),
                        rs.getString("telefono"), rs.getString("direccion")));
            }
        }
        return lista;
    }

    /** Nombres de clientes que contienen el texto buscado (para los buscadores). */
    public List<String> buscarNombresLike(String texto) throws SQLException {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT nombre FROM clientes WHERE nombre LIKE ? ORDER BY nombre";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(rs.getString("nombre"));
                }
            }
        }
        return lista;
    }

    public int contarClientes() throws SQLException {
        String sql = "SELECT COUNT(*) FROM clientes";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
