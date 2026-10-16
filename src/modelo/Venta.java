package modelo;

/**
 * Representa una venta a crédito realizada a un cliente.
 * La fecha se maneja como texto en formato AAAA-MM-DD para que sea
 * fácil de validar y mostrar, igual que en la versión original.
 */
public class Venta {

    private int id;
    private String cliente;
    private String producto;
    private double total;
    private String fecha;

    public Venta() {
    }

    public Venta(String cliente, String producto, double total, String fecha) {
        this.cliente = cliente;
        this.producto = producto;
        this.total = total;
        this.fecha = fecha;
    }

    public Venta(int id, String cliente, String producto, double total, String fecha) {
        this.id = id;
        this.cliente = cliente;
        this.producto = producto;
        this.total = total;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
