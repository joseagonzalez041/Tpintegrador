/**
 * Esta es nuestra propia clase de Error (Excepción).
 * La "lanzaremos" (throw) cuando el usuario pida un ID de tarea
 * que no exista en nuestra lista.
 */
public class TareaNoEncontradaException extends Exception {

    /**
     * Constructor que guarda el mensaje de error.
     * @param mensaje El texto que explica qué pasó (ej: "No existe la tarea con ID 5")
     */
    public TareaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
