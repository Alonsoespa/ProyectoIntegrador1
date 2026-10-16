package modelo;

/**
 * Representa un pago (abono) realizado por un cliente.
 */
public class Pago {

    private int id;
    private String cliente;
    private double monto;
    private String fecha;

    public Pago() {
    }

    public Pago(String cliente, double monto, String fecha) {
        this.cliente = cliente;
        this.monto = monto;
        this.fecha = fecha;
    }

    public Pago(int id, String cliente, double monto, String fecha) {
        this.id = id;
        this.cliente = cliente;
        this.monto = monto;
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

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
