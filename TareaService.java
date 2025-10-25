import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate; // Importamos Predicate para la lambda
import java.util.stream.Collectors;

/**
 * Esta clase es el "cerebro" o la "lógica del negocio".
 * Se encarga de manejar la lista de tareas (agregar, borrar, etc.).
 * No sabe nada de 'Scanner' ni de cómo se guardan las cosas en el archivo,
 * solo llama al GestorPersistencia cuando lo necesita.
 */
public class TareaService {

    private List<Tarea> tareas; // La lista principal de tareas en memoria
    private int proximoId;
    private GestorPersistencia persistencia; // El ayudante que sabe de archivos

    public TareaService() {
        this.tareas = new ArrayList<>();
        this.persistencia = new GestorPersistencia();
        this.proximoId = 1;
    }

    /**
     * Llama al gestor de persistencia para cargar los datos en memoria
     * cuando arranca la app.
     */
    public void cargarDatos() {
        // Pide los datos al gestor y los guarda en sus variables
        this.tareas = persistencia.cargarTareas();
        this.proximoId = persistencia.cargarProximoId();
    }

    /**
     * Llama al gestor de persistencia para guardar los datos de la memoria
     * en el archivo, justo antes de cerrar la app.
     */
    public void guardarDatos() {
        persistencia.guardar(this.tareas, this.proximoId);
    }

    /**
     * Agrega una nueva tarea a la lista.
     * @param descripcion El texto de la tarea.
     * @return La Tarea que se acaba de crear.
     */
    public Tarea agregarTarea(String descripcion) {
        Tarea nuevaTarea = new Tarea(this.proximoId, descripcion);
        this.proximoId++; // Aumentamos el contador para que no se repitan IDs
        this.tareas.add(nuevaTarea);
        return nuevaTarea;
    }

    /**
     * Busca una tarea por su ID.
     * Este es un método "ayudante" (helper) que usaremos en otros métodos.
     * Es más simple que usar 'Optional' o 'stream().findFirst()'.
     *
     * @param id El ID a buscar.
     * @return La Tarea encontrada.
     * @throws TareaNoEncontradaException Si no la encuentra.
     */
    private Tarea buscarTareaPorId(int id) throws TareaNoEncontradaException {
        // Recorremos la lista con un 'for-each' simple
        for (Tarea tarea : tareas) {
            if (tarea.getId() == id) {
                return tarea; // La encontramos, la devolvemos
            }
        }
        // Si el 'for' termina y no la encontramos, lanzamos nuestro error
        throw new TareaNoEncontradaException("No se encontró ninguna tarea con el ID: " + id);
    }

    /**
     * Marca una tarea como completada, buscándola por su ID.
     * @param id El ID de la tarea a completar.
     * @return La Tarea ya actualizada.
     * @throws TareaNoEncontradaException Si el ID no existe.
     */
    public Tarea marcarComoCompletada(int id) throws TareaNoEncontradaException {
        // Usamos nuestro método ayudante para encontrarla
        Tarea tarea = buscarTareaPorId(id);
        // Modificamos el objeto
        tarea.setCompletada(true);
        return tarea;
    }

    /**
     * Elimina una tarea de la lista usando su ID.
     * @param id El ID de la tarea a eliminar.
     * @throws TareaNoEncontradaException Si el ID no existe.
     */
    public void eliminarTarea(int id) throws TareaNoEncontradaException {
        // Primero, nos aseguramos de que exista
        Tarea tarea = buscarTareaPorId(id);
        // Si existe, la eliminamos de la lista.
        this.tareas.remove(tarea);
    }

    /**
     * Devuelve la lista completa de tareas.
     */
    public List<Tarea> listarTodasLasTareas() {
        // Devuelve una copia para que no se pueda modificar la lista original
        // desde afuera por accidente (esto es una buena práctica).
        return new ArrayList<>(this.tareas);
    }

    /**
     * ¡REQUERIMIENTO DE LAMBDA!
     * Devuelve una lista de tareas, pero filtrada según una condición.
     * @param filtro Una "función lambda" (un Predicado) que dice si la tarea
     * pasa el filtro o no (ej: tarea -> tarea.isCompletada())
     */
    public List<Tarea> listarTareasFiltradas(Predicate<Tarea> filtro) {
        // Usamos la API Stream, que es la forma moderna de filtrar listas.
        // 1. .stream() -> "Abrimos" la lista para operarla.
        // 2. .filter(filtro) -> Dejamos pasar solo los elementos que cumplan la lambda.
        // 3. .collect(Collectors.toList()) -> Volvemos a armar una lista con los resultados.
        return this.tareas.stream()
                .filter(filtro)
                .collect(Collectors.toList());
    }
}
