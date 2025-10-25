import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

/**
 * Clase principal de la aplicación (Capa de Vista/UI de Consola).
 * Su única responsabilidad es:
 * 1. Mostrar el menú.
 * 2. Pedir datos al usuario (usar Scanner).
 * 3. Llamar al "cerebro" (TareaService) para que haga el trabajo.
 * 4. Manejar los errores de forma amigable.
 */
public class AppTareas {

    // El servicio (cerebro) y el scanner (para leer) se crean una sola vez.
    private static final TareaService servicio = new TareaService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // 1. Al arrancar, le pedimos al servicio que cargue los datos del archivo.
        servicio.cargarDatos();

        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            try {
                int opcion = scanner.nextInt();
                scanner.nextLine(); // ¡Importante! Limpiar el "Enter" que queda en el buffer.

                switch (opcion) {
                    case 1:
                        uiAgregarTarea();
                        break;
                    case 2:
                        uiListarTareas(); // Ahora tiene un submenú
                        break;
                    case 3:
                        uiMarcarComoCompletada();
                        break;
                    case 4:
                        uiEliminarTarea();
                        break;
                    case 0:
                        salir = true;
                        break;
                    default:
                        System.out.println("Opción no válida. Intente de nuevo.");
                }

            } catch (InputMismatchException e) {
                // Si el usuario escribe "hola" en vez de un número.
                System.out.println("Error: Debe ingresar un número.");
                scanner.nextLine(); // Limpiamos la entrada incorrecta
            } catch (TareaNoEncontradaException e) {
                // Si el servicio nos avisa que no encontró el ID.
                System.out.println("Error: " + e.getMessage());
            }

            if (!salir) {
                System.out.println("\nPresione [Enter] para continuar...");
                scanner.nextLine();
            }
        }

        // 2. Al salir del bucle, le pedimos al servicio que guarde todo.
        servicio.guardarDatos();
        System.out.println("¡Datos guardados! Hasta pronto. 👋");
        scanner.close(); // Cerramos el scanner al final.
    }

    /**
     * Método simple que solo imprime las opciones del menú.
     */
    private static void mostrarMenu() {
        System.out.println("\n--- GESTOR DE TAREAS ---");
        System.out.println("1. Agregar nueva tarea");
        System.out.println("2. Listar tareas");
        System.out.println("3. Marcar tarea como completada");
        System.out.println("4. Eliminar tarea por ID");
        System.out.println("0. Salir y Guardar");
        System.out.print("Seleccione una opción: ");
    }

    // --- Métodos de Interfaz de Usuario (UI) ---

    private static void uiAgregarTarea() {
        System.out.println("\n--- 1. Agregar Tarea ---");
        String descripcion;
        // (Bonus) Validación para que no ingresen una descripción vacía
        do {
            System.out.print("Ingrese la descripción: ");
            descripcion = scanner.nextLine().trim(); // .trim() saca espacios en blanco
            if (descripcion.isEmpty()) {
                System.out.println("La descripción no puede estar vacía.");
            }
        } while (descripcion.isEmpty());

        // Le pasamos la descripción al servicio y él se encarga de todo
        Tarea nueva = servicio.agregarTarea(descripcion);
        System.out.println("¡Tarea agregada con éxito!");
        System.out.println(nueva); // Imprimimos la tarea (llama al .toString())
    }

    private static void uiListarTareas() {
        System.out.println("\n--- 2. Listar Tareas ---");
        System.out.println("1. Listar TODAS");
        System.out.println("2. Listar solo PENDIENTES");
        System.out.println("3. Listar solo COMPLETADAS");
        System.out.print("Elija una opción: ");
        int opcion = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer

        List<Tarea> tareas;
        String titulo;

        // ¡AQUÍ USAMOS LA LAMBDA!
        if (opcion == 2) {
            titulo = "--- Tareas Pendientes ---";
            // Le pasamos al servicio una "regla" (la lambda)
            // "tarea -> !tarea.isCompletada()" significa:
            // "Por cada tarea, chequeá si NO está completada"
            tareas = servicio.listarTareasFiltradas(tarea -> !tarea.isCompletada());
        } else if (opcion == 3) {
            titulo = "--- Tareas Completadas ---";
            // "tarea -> tarea.isCompletada()" significa:
            // "Por cada tarea, chequeá si SÍ está completada"
            tareas = servicio.listarTareasFiltradas(tarea -> tarea.isCompletada());
        } else {
            titulo = "--- Todas las Tareas ---";
            tareas = servicio.listarTodasLasTareas();
        }

        // Mostramos los resultados
        System.out.println(titulo);
        if (tareas.isEmpty()) {
            System.out.println("No hay tareas para mostrar.");
        } else {
            // Usamos forEach con una lambda simple para imprimir cada tarea
            tareas.forEach(tarea -> System.out.println(tarea));
        }
    }

    private static void uiMarcarComoCompletada() throws TareaNoEncontradaException {
        System.out.println("\n--- 3. Marcar como Completada ---");
        System.out.print("Ingrese el ID de la tarea a completar: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer

        // Llamamos al servicio para que haga el trabajo
        Tarea actualizada = servicio.marcarComoCompletada(id);
        System.out.println("¡Tarea actualizada!");
        System.out.println(actualizada);
    }

    private static void uiEliminarTarea() throws TareaNoEncontradaException {
        System.out.println("\n--- 4. Eliminar Tarea ---");
        System.out.print("Ingrese el ID de la tarea a eliminar: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer

        // Llamamos al servicio
        servicio.eliminarTarea(id);
        System.out.println("Tarea con ID " + id + " eliminada correctamente.");
    }
}
