package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Movimiento;
import modelo.Pago;

public class PagoDAO {

    public void insertar(Pago p) throws SQLException {
        String sql = "INSERT INTO pagos (cliente, monto, fecha) VALUES (?, ?, ?)";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getCliente());
            ps.setDouble(2, p.getMonto());
            ps.setDate(3, Date.valueOf(p.getFecha()));
            ps.executeUpdate();
        }
    }

    public void actualizar(Pago p) throws SQLException {
        String sql = "UPDATE pagos SET cliente=?, monto=?, fecha=? WHERE id=?";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getCliente());
            ps.setDouble(2, p.getMonto());
            ps.setDate(3, Date.valueOf(p.getFecha()));
            ps.setInt(4, p.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM pagos WHERE id=?";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void eliminarPorCliente(String cliente) throws SQLException {
        String sql = "DELETE FROM pagos WHERE cliente=?";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente);
            ps.executeUpdate();
        }
    }

    public void renombrarCliente(String nombreAnterior, String nombreNuevo) throws SQLException {
        String sql = "UPDATE pagos SET cliente=? WHERE cliente=?";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreNuevo);
            ps.setString(2, nombreAnterior);
            ps.executeUpdate();
        }
    }

    public int contarPorCliente(String cliente) throws SQLException {
        String sql = "SELECT COUNT(*) FROM pagos WHERE cliente=?";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public List<Pago> listarTodos() throws SQLException {
        List<Pago> lista = new ArrayList<>();
        String sql = "SELECT id, cliente, monto, fecha FROM pagos ORDER BY fecha DESC, id DESC";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Pago(rs.getInt("id"), rs.getString("cliente"),
                        rs.getDouble("monto"), rs.getDate("fecha").toString()));
            }
        }
        return lista;
    }

    public List<Pago> listarPorCliente(String cliente) throws SQLException {
        List<Pago> lista = new ArrayList<>();
        String sql = "SELECT id, cliente, monto, fecha FROM pagos WHERE cliente=? ORDER BY fecha DESC";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Pago(rs.getInt("id"), rs.getString("cliente"),
                            rs.getDouble("monto"), rs.getDate("fecha").toString()));
                }
            }
        }
        return lista;
    }

    public List<Movimiento> ultimosMovimientos(int cantidad) throws SQLException {
        List<Movimiento> lista = new ArrayList<>();
        String sql = "SELECT cliente, monto, fecha FROM pagos ORDER BY fecha DESC LIMIT ?";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Movimiento(rs.getString("cliente"), rs.getDate("fecha").toString(),
                            "Pago", "", rs.getDouble("monto")));
                }
            }
        }
        return lista;
    }

    public double sumaTotalGeneral() throws SQLException {
        String sql = "SELECT SUM(monto) FROM pagos";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0;
    }

    public double sumaPorCliente(String cliente) throws SQLException {
        String sql = "SELECT SUM(monto) FROM pagos WHERE cliente=?";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0;
    }

    public double sumaPorClienteHasta(String cliente, String fecha) throws SQLException {
        String sql = "SELECT SUM(monto) FROM pagos WHERE cliente=? AND fecha<=?";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente);
            ps.setDate(2, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0;
    }

    /** Suma de pagos recibidos en el mes y año actuales (para la tarjeta del dashboard). */
    public double sumaMesActual() throws SQLException {
        String sql = "SELECT SUM(monto) FROM pagos WHERE DATE_FORMAT(fecha, '%Y-%m') = DATE_FORMAT(CURDATE(), '%Y-%m')";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0;
    }
}
