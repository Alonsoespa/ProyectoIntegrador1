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
import modelo.Venta;

public class VentaDAO {

    public void insertar(Venta v) throws SQLException {
        String sql = "INSERT INTO ventas (cliente, producto, total, fecha) VALUES (?, ?, ?, ?)";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getCliente());
            ps.setString(2, v.getProducto());
            ps.setDouble(3, v.getTotal());
            ps.setDate(4, Date.valueOf(v.getFecha()));
            ps.executeUpdate();
        }
    }

    public void actualizar(Venta v) throws SQLException {
        String sql = "UPDATE ventas SET cliente=?, producto=?, total=?, fecha=? WHERE id=?";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getCliente());
            ps.setString(2, v.getProducto());
            ps.setDouble(3, v.getTotal());
            ps.setDate(4, Date.valueOf(v.getFecha()));
            ps.setInt(5, v.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM ventas WHERE id=?";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void eliminarPorCliente(String cliente) throws SQLException {
        String sql = "DELETE FROM ventas WHERE cliente=?";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente);
            ps.executeUpdate();
        }
    }

    /** Cuando se renombra un cliente, sus ventas ya guardadas se actualizan. */
    public void renombrarCliente(String nombreAnterior, String nombreNuevo) throws SQLException {
        String sql = "UPDATE ventas SET cliente=? WHERE cliente=?";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreNuevo);
            ps.setString(2, nombreAnterior);
            ps.executeUpdate();
        }
    }

    public int contarPorCliente(String cliente) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ventas WHERE cliente=?";
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

    public List<Venta> listarTodas() throws SQLException {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT id, cliente, producto, total, fecha FROM ventas ORDER BY fecha DESC, id DESC";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Venta(rs.getInt("id"), rs.getString("cliente"),
                        rs.getString("producto"), rs.getDouble("total"),
                        rs.getDate("fecha").toString()));
            }
        }
        return lista;
    }

    public List<Venta> listarPorCliente(String cliente) throws SQLException {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT id, cliente, producto, total, fecha FROM ventas WHERE cliente=? ORDER BY fecha DESC";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Venta(rs.getInt("id"), rs.getString("cliente"),
                            rs.getString("producto"), rs.getDouble("total"),
                            rs.getDate("fecha").toString()));
                }
            }
        }
        return lista;
    }

    /** Últimas N ventas convertidas a Movimiento, para el panel de inicio. */
    public List<Movimiento> ultimosMovimientos(int cantidad) throws SQLException {
        List<Movimiento> lista = new ArrayList<>();
        String sql = "SELECT cliente, producto, total, fecha FROM ventas ORDER BY fecha DESC LIMIT ?";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Movimiento(rs.getString("cliente"), rs.getDate("fecha").toString(),
                            "Venta", rs.getString("producto"), rs.getDouble("total")));
                }
            }
        }
        return lista;
    }

    public double sumaTotalGeneral() throws SQLException {
        String sql = "SELECT SUM(total) FROM ventas";
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
        String sql = "SELECT SUM(total) FROM ventas WHERE cliente=?";
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

    /** Suma de ventas de un cliente hasta cierta fecha (incluida). Sirve para calcular el saldo acumulado. */
    public double sumaPorClienteHasta(String cliente, String fecha) throws SQLException {
        String sql = "SELECT SUM(total) FROM ventas WHERE cliente=? AND fecha<=?";
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

    /** Lista de clientes distintos que tienen al menos una venta registrada. */
    public List<String> clientesConVentas() throws SQLException {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT cliente FROM ventas";
        Connection con = Conexion.getConexion();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(rs.getString(1));
            }
        }
        return lista;
    }
}
