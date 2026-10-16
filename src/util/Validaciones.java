package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * Métodos de validación reutilizados por todas las pantallas.
 */
public class Validaciones {

    private static final Pattern PATRON_NOMBRE =
        Pattern.compile("^[A-Za-zÀ-ÿ\\s.,'-]+$");

    private Validaciones() {
    }

    /** Solo letras (con acentos), espacios y . , ' - */
    public static boolean nombreValido(String texto) {
        return texto != null && PATRON_NOMBRE.matcher(texto).matches();
    }

    /** Verifica que el texto sea un número mayor o igual a cero. */
    public static boolean esNumero(String valor) {
        try {
            return Double.parseDouble(valor) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Solo admite fechas reales en formato AAAA-MM-DD. */
    public static boolean fechaValida(String texto) {
        if (texto == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(texto);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /** Teléfono: exactamente 10 dígitos numéricos. */
    public static boolean telefonoValido(String texto) {
        return texto != null && texto.matches("\\d{10}");
    }
}
