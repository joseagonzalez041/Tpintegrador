import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase se encarga de la "persistencia".
 * Su única tarea es guardar y cargar la lista de tareas
 * desde un archivo de texto. No sabe nada de menús ni de lógica.
 */
public class GestorPersistencia {

    // El nombre del archivo donde guardaremos todo.
    private static final String NOMBRE_ARCHIVO = "tareas.txt";

    /**
     * Carga SÓLO la lista de tareas desde el archivo.
     * Lee el archivo línea por línea y convierte cada una en un objeto Tarea.
     */
    public List<Tarea> cargarTareas() {
        List<Tarea> tareas = new ArrayList<>();
        File archivo = new File(NOMBRE_ARCHIVO);

        // Si no existe el archivo, simplemente devolvemos una lista vacía.
        if (!archivo.exists()) {
            System.out.println("[Archivo] No se encontró " + NOMBRE_ARCHIVO + ". Se creará uno nuevo al salir.");
            return tareas;
        }

        // 'try-with-resources' se asegura de que el 'reader' se cierre solo
        // aunque haya un error. Es la forma moderna de leer archivos.
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {

            // Omitimos la primera línea (la del ID)
            reader.readLine();

            String linea;
            // Leemos el resto del archivo línea por línea
            while ((linea = reader.readLine()) != null) {
                Tarea tarea = Tarea.fromFileString(linea);
                if (tarea != null) { // fromFileString devuelve null si hay un error
                    tareas.add(tarea);
                }
            }
            System.out.println("[Archivo] Se cargaron " + tareas.size() + " tareas.");

        } catch (IOException e) {
            System.err.println("Error al leer el archivo de tareas: " + e.getMessage());
        }
        return tareas;
    }

    /**
     * Carga SÓLO el 'proximoId' desde la primera línea del archivo.
     */
    public int cargarProximoId() {
        File archivo = new File(NOMBRE_ARCHIVO);

        if (!archivo.exists()) {
            return 1; // Si no hay archivo, empezamos en 1
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            // Leemos solo la primera línea
            String lineaId = reader.readLine();
            if (lineaId != null && !lineaId.isEmpty()) {
                // Convertimos el texto a número y lo devolvemos
                return Integer.parseInt(lineaId);
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al leer el ID, se usará 1 por defecto: " + e.getMessage());
        }

        return 1; // Si la primera línea está vacía o corrupta, empezamos en 1.
    }

    /**
     * Guarda la lista de tareas y el próximo ID en el archivo.
     * Sobrescribe el archivo por completo.
     */
    public void guardar(List<Tarea> tareas, int proximoId) {
        // 'try-with-resources' para el 'writer'
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOMBRE_ARCHIVO))) {

            // 1. Escribimos el 'proximoId' en la primera línea
            writer.write(String.valueOf(proximoId));
            writer.newLine(); // Salto de línea

            // 2. Recorremos la lista y guardamos cada tarea
            for (Tarea tarea : tareas) {
                writer.write(tarea.toFileString());
                writer.newLine();
            }
            System.out.println("[Archivo] Se guardaron " + tareas.size() + " tareas.");

        } catch (IOException e) {
            System.err.println("Error al guardar el archivo: " + e.getMessage());
        }
    }
}
