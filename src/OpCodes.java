import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Clase que contiene las implementaciones de los opcodes básicos
 * requeridos para la Fase 1 del proyecto.
 */

public class OpCodes {

    // ==================== LITERALES Y CONSTANTES ====================

    /**
     * OP_0 / OP_FALSE: Empuja un array vacío a la pila (representa false/0)
     */
    public static class OP_0 implements OpCode {
        @Override
        public void execute(Stack stack) {
            stack.push(new byte[0]);
        }

        @Override
        public String getName() {
            return "OP_0";
        }
    }

    /**
     * OP_1 a OP_16: Empujan los números del 1 al 16 a la pila
     */
    public static class OP_NUMBER implements OpCode {
        private final int number;

        public OP_NUMBER(int number) {
            if (number < 1 || number > 16) {
                throw new IllegalArgumentException("OP_NUMBER solo soporta valores de 1 a 16");
            }
            this.number = number;
        }

        @Override
        public void execute(Stack stack) {
            stack.push(Stack.intToBytes(number));
        }

        @Override
        public String getName() {
            return "OP_" + number;
        }
    }

    /**
     * PUSHDATA: Empuja datos directamente a la pila
     */
    public static class PUSHDATA implements OpCode {
        private final byte[] data;

        public PUSHDATA(byte[] data) {
            this.data = data;
        }

        @Override
        public void execute(Stack stack) {
            stack.push(data);
        }

        @Override
        public String getName() {
            return "PUSHDATA";
        }
    }

    // ==================== OPERACIONES DE PILA ====================

    /**
     * OP_DUP: Duplica el elemento en la cima de la pila
     */
    public static class OP_DUP implements OpCode {
        @Override
        public void execute(Stack stack) {
            if (stack.isEmpty()) {
                throw new IllegalStateException("OP_DUP: Pila vacía");
            }
            byte[] top = stack.peek();
            stack.push(Arrays.copyOf(top, top.length));
        }

        @Override
        public String getName() {
            return "OP_DUP";
        }
    }

    /**
     * OP_DROP: Remueve el elemento en la cima de la pila
     */
    public static class OP_DROP implements OpCode {
        @Override
        public void execute(Stack stack) {
            if (stack.isEmpty()) {
                throw new IllegalStateException("OP_DROP: Pila vacía");
            }
            stack.pop();
        }

        @Override
        public String getName() {
            return "OP_DROP";
        }
    }

    // ==================== COMPARACIÓN ====================

    /**
     * OP_EQUAL: Compara los dos elementos superiores de la pila
     * Empuja 1 si son iguales, 0 si no lo son
     */
    public static class OP_EQUAL implements OpCode {
        @Override
        public void execute(Stack stack) {
            if (stack.size() < 2) {
                throw new IllegalStateException("OP_EQUAL: Se requieren al menos 2 elementos");
            }

            byte[] a = stack.pop();
            byte[] b = stack.pop();

            boolean equal = Arrays.equals(a, b);
            stack.push(Stack.intToBytes(equal ? 1 : 0));
        }

        @Override
        public String getName() {
            return "OP_EQUAL";
        }
    }

    /**
     * OP_EQUALVERIFY: Igual que OP_EQUAL, pero falla si no son iguales
     */
    public static class OP_EQUALVERIFY implements OpCode {
        @Override
        public void execute(Stack stack) {
            if (stack.size() < 2) {
                throw new IllegalStateException("OP_EQUALVERIFY: Se requieren al menos 2 elementos");
            }

            byte[] a = stack.pop();
            byte[] b = stack.pop();

            if (!Arrays.equals(a, b)) {
                throw new IllegalStateException("OP_EQUALVERIFY: Los elementos no son iguales");
            }
        }

        @Override
        public String getName() {
            return "OP_EQUALVERIFY";
        }
    }

    // ==================== CRIPTOGRAFÍA ====================

    /**
     * OP_HASH160: Calcula SHA-256 seguido de RIPEMD-160
     * Para la Fase 1, simplificamos usando solo SHA-256 truncado
     */
    public static class OP_HASH160 implements OpCode {
        @Override
        public void execute(Stack stack) {
            if (stack.isEmpty()) {
                throw new IllegalStateException("OP_HASH160: Pila vacía");
            }

            byte[] data = stack.pop();
            byte[] hash = hash160(data);
            stack.push(hash);
        }

        @Override
        public String getName() {
            return "OP_HASH160";
        }

        /**
         * Calcula el hash160 (SHA-256 + RIPEMD-160 simulado)
         * Para propósitos didácticos, usamos SHA-256 y tomamos los primeros 20 bytes
         */
        private byte[] hash160(byte[] data) {
            try {
                MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                byte[] sha256Hash = sha256.digest(data);
                // Simulamos RIPEMD-160 tomando solo 20 bytes
                return Arrays.copyOf(sha256Hash, 20);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("SHA-256 no disponible", e);
            }
        }
    }

    // ==================== FIRMAS (SIMULADAS) ====================

    /**
     * OP_CHECKSIG: Verifica una firma digital (simulada para propósitos didácticos)
     * En un entorno real, verificaría la firma ECDSA.
     *
     * Para la Fase 1, simulamos la verificación:
     * - Pop signature (firma)
     * - Pop pubKey (clave pública)
     * - Empuja 1 (true) si la "verificación" es exitosa
     */
    public static class OP_CHECKSIG implements OpCode {
        @Override
        public void execute(Stack stack) {
            if (stack.size() < 2) {
                throw new IllegalStateException("OP_CHECKSIG: Se requieren al menos 2 elementos (firma y pubKey)");
            }

            byte[] pubKey = stack.pop();
            byte[] signature = stack.pop();

            // SIMULACIÓN: En un sistema real, aquí se verificaría la firma ECDSA
            // Para propósitos didácticos, consideramos válida cualquier firma no vacía
            boolean valid = mockVerifySignature(signature, pubKey);

            stack.push(Stack.intToBytes(valid ? 1 : 0));
        }

        @Override
        public String getName() {
            return "OP_CHECKSIG";
        }

        /**
         * Función mock que simula la verificación de firma.
         * En producción, esto verificaría una firma ECDSA real.
         *
         * @param signature La firma a verificar
         * @param pubKey La clave pública
         * @return true si la firma es "válida" (simulado)
         */
        private boolean mockVerifySignature(byte[] signature, byte[] pubKey) {
            // Para la demostración, consideramos válida cualquier firma y pubKey no vacíos
            // y que tengan longitudes razonables
            if (signature == null || signature.length == 0) {
                return false;
            }
            if (pubKey == null || pubKey.length == 0) {
                return false;
            }

            // Simulación: firma válida si tiene al menos 64 bytes
            // y pubKey tiene al menos 33 bytes (compressed key)
            return signature.length >= 64 && pubKey.length >= 33;
        }
    }
}
