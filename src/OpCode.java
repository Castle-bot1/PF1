/**
 * Interfaz que define el contrato para todas las operaciones (opcodes)
 * del intérprete de Bitcoin Script.
 *
 * Cada opcode debe implementar el método execute que recibe la pila
 * y modifica su estado según la operación correspondiente.
 *
 */
public interface OpCode {

    /**
     * Ejecuta la operación sobre la pila dada.
     *
     * @param stack La pila de datos del intérprete
     * @throws IllegalStateException si la operación no puede ejecutarse
     *         (por ejemplo, pila vacía cuando se requieren elementos)
     */
    void execute(Stack stack);

    /**
     * Retorna el nombre de la operación.
     *
     * @return Nombre del opcode (ej: "OP_DUP", "OP_HASH160")
     */
    String getName();
}
