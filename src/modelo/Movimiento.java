package modelo;

/**
 * Representa un movimiento genérico (una Venta o un Pago) ya combinado,
 * para mostrarlo en tablas de historial sin importar de qué tabla vino.
 */
public class Movimiento {

    private String cliente;
    private String fecha;
    private String tipo;      // "Venta" o "Pago"
    private String detalle;   // producto (vacío si es pago)
    private double monto;

    public Movimiento(String cliente, String fecha, String tipo, String detalle, double monto) {
        this.cliente = cliente;
        this.fecha = fecha;
        this.tipo = tipo;
        this.detalle = detalle;
        this.monto = monto;
    }

    public String getCliente() {
        return cliente;
    }

    public String getFecha() {
        return fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDetalle() {
        return detalle;
    }

    public double getMonto() {
        return monto;
    }
}
