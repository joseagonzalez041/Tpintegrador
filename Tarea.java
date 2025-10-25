import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Esta clase es el "molde" para crear objetos Tarea.
 * Cada objeto Tarea guardará la información de una sola tarea.
 * Su responsabilidad es contener los datos.
 */
public class Tarea {

    // Atributos privados para guardar la información.
    // Solo se pueden modificar a través de los métodos (encapsulamiento).
    private int id;
    private String descripcion;
    private boolean completada;
    private LocalDate fechaCreacion;

    /**
     * Constructor para crear una TAREA NUEVA desde el menú.
     * La fecha se pone sola y 'completada' arranca en 'false'.
     */
    public Tarea(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
        this.completada = false; // Por defecto, una tarea nueva está pendiente
        this.fechaCreacion = LocalDate.now(); // Se pone la fecha de hoy
    }

    /**
     * Constructor para RECONSTRUIR una Tarea desde el archivo .txt.
     * Este lo usa el GestorPersistencia para volver a crear los objetos
     * que estaban guardados.
     */
    public Tarea(int id, String descripcion, boolean completada, LocalDate fechaCreacion) {
        this.id = id;
        this.descripcion = descripcion;
        this.completada = completada;
        this.fechaCreacion = fechaCreacion;
    }

    // --- Métodos Getters (para LEER datos) ---

    public int getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isCompletada() {
        return completada;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    // --- Métodos Setters (para MODIFICAR datos) ---
    // Solo ponemos 'set' para lo que queremos que se pueda cambiar desde afuera.
    // El ID o la fecha de creación no deberían cambiarse.

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    // --- Métodos para Persistencia (Guardar/Cargar) ---

    /**
     * Convierte el objeto Tarea a una sola línea de texto para guardarla.
     * Usamos un separador simple como "|" (pipe).
     * Formato: id|descripcion|completada|fechaCreacion
     */
    public String toFileString() {
        // Usamos la barra "|" como separador. Es fácil de leer.
        return id + "|" + descripcion + "|" + completada + "|" + fechaCreacion;
    }

    /**
     * Método "estático" (de clase) que hace lo contrario a toFileString.
     * Recibe una línea de texto del archivo y la convierte en un objeto Tarea.
     */
    public static Tarea fromFileString(String fileString) {
        try {
            // 1. Partimos la línea de texto usando el separador "|"
            // El ", 4" es un truco para que, si la descripción tiene un "|",
            // no se rompa (aunque es mejor evitarlo).
            String[] partes = fileString.split("\\|", 4);

            // 2. Convertimos cada parte al tipo de dato correcto
            int id = Integer.parseInt(partes[0]);
            String descripcion = partes[1];
            boolean completada = Boolean.parseBoolean(partes[2]);
            LocalDate fecha = LocalDate.parse(partes[3]);

            // 3. Creamos y devolvemos la Tarea usando el segundo constructor
            return new Tarea(id, descripcion, completada, fecha);

        } catch (Exception e) {
            // Si algo falla (archivo corrupto, formato incorrecto),
            // avisamos y devolvemos 'null' para que quien lo llamó sepa del error.
            System.err.println("Error al leer línea de tarea: " + fileString);
            return null;
        }
    }

    /**
     * Sobrescribimos el método toString() para que cuando imprimamos
     * un objeto Tarea, se vea bonito en la consola.
     */
    @Override
    public String toString() {
        // [ ] Pendiente   o   [X] Completada
        String estado = completada ? "[X] Completada" : "[ ] Pendiente";

        // Formateamos la fecha a "día/mes/año"
        String fechaFormateada = fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // Usamos String.format para alinear todo en columnas.
        return String.format("ID: %-3d | %-13s | %s | %s", id, estado, fechaFormateada, descripcion);
    }
}
