import java.util.ArrayDeque;
import java.util.Arrays;

/**
 * Clase que representa la pila de datos del intérprete de Bitcoin Script.
 * Utiliza ArrayDeque para operaciones O(1) en push, pop y peek.
 *
 * @author Diego Castillo, Edgar Guevara, Hector Duarte
 * @version 1.0
 */
public class Stack {
    private ArrayDeque<byte[]> stack;

    /**
     * Constructor que inicializa la pila vacía.
     */
    public Stack() {
        this.stack = new ArrayDeque<>();
    }

    /**
     * Apila un elemento en la cima de la pila.
     * Complejidad: O(1)
     *
     * @param data Array de bytes a apilar
     */
    public void push(byte[] data) {
        stack.push(data);
    }

    /**
     * Remueve y retorna el elemento en la cima de la pila.
     * Complejidad: O(1)
     *
     * @return Array de bytes en la cima
     * @throws IllegalStateException si la pila está vacía
     */
    public byte[] pop() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Stack underflow: No se puede hacer pop en una pila vacía");
        }
        return stack.pop();
    }

    /**
     * Retorna el elemento en la cima sin removerlo.
     * Complejidad: O(1)
     *
     * @return Array de bytes en la cima
     * @throws IllegalStateException si la pila está vacía
     */
    public byte[] peek() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Stack vacía: No se puede hacer peek");
        }
        return stack.peek();
    }

    /**
     * Verifica si la pila está vacía.
     * Complejidad: O(1)
     *
     * @return true si la pila está vacía, false en caso contrario
     */
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    /**
     * Retorna el tamaño actual de la pila.
     * Complejidad: O(1)
     *
     * @return Número de elementos en la pila
     */
    public int size() {
        return stack.size();
    }

    /**
     * Limpia todos los elementos de la pila.
     * Complejidad: O(n)
     */
    public void clear() {
        stack.clear();
    }

    /**
     * Convierte un byte array a su representación entera.
     * Los valores vacíos se interpretan como 0.
     *
     * @param data Array de bytes
     * @return Valor entero correspondiente
     */
    public static int bytesToInt(byte[] data) {
        if (data == null || data.length == 0) {
            return 0;
        }

        // Interpretación little-endian
        int result = 0;
        for (int i = 0; i < data.length; i++) {
            result |= (data[i] & 0xFF) << (8 * i);
        }
        return result;
    }

    /**
     * Convierte un entero a su representación en byte array.
     *
     * @param value Valor entero
     * @return Array de bytes correspondiente
     */
    public static byte[] intToBytes(int value) {
        if (value == 0) {
            return new byte[0];
        }

        // Representación little-endian
        byte[] result = new byte[4];
        int index = 0;

        for (int i = 0; i < 4; i++) {
            byte b = (byte) (value & 0xFF);
            if (b != 0 || index > 0) {
                result[index++] = b;
            }
            value >>= 8;
        }

        return Arrays.copyOf(result, index);
    }

    /**
     * Interpreta un byte array como booleano.
     * Cualquier valor diferente de vacío o cero es verdadero.
     *
     * @param data Array de bytes
     * @return true si es verdadero, false en caso contrario
     */
    public static boolean isTrue(byte[] data) {
        if (data == null || data.length == 0) {
            return false;
        }

        for (byte b : data) {
            if (b != 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retorna una representación en String del estado actual de la pila.
     * Útil para el modo trace.
     *
     * @return String representando la pila
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Stack (").append(stack.size()).append(" elementos):\n");

        int index = 0;
        for (byte[] element : stack) {
            sb.append("  [").append(index++).append("] ");
            sb.append(bytesToHex(element));
            sb.append(" (int: ").append(bytesToInt(element)).append(")\n");
        }

        return sb.toString();
    }

    /**
     * Convierte un byte array a su representación hexadecimal.
     *
     * @param bytes Array de bytes
     * @return String en formato hexadecimal
     */
    private static String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < bytes.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(String.format("%02x", bytes[i]));
        }
        sb.append("]");
        return sb.toString();
    }
}
