import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Intérprete de Bitcoin Script que evalúa scripts utilizando una pila.
 *
 * El intérprete:
 * - Usa HashMap para mapear nombres de opcodes a sus implementaciones (O(1) lookup)
 * - Usa ArrayList para almacenar la secuencia de instrucciones
 * - Usa ArrayDeque (via Stack) para la pila de ejecución
 */
public class ScriptInterpreter {

    private Stack stack;
    private HashMap<String, OpCode> opcodeMap;
    private boolean traceMode;

    /**
     * Constructor del intérprete.
     */
    public ScriptInterpreter() {
        this.stack = new Stack();
        this.opcodeMap = new HashMap<>();
        this.traceMode = false;
        initializeOpcodes();
    }

    /**
     * Inicializa el HashMap con todos los opcodes soportados.
     * Complejidad: O(1) para cada inserción
     */
    private void initializeOpcodes() {
        // Literales
        opcodeMap.put("OP_0", new OpCodes.OP_0());
        opcodeMap.put("OP_FALSE", new OpCodes.OP_0());

        // Números del 1 al 16
        for (int i = 1; i <= 16; i++) {
            opcodeMap.put("OP_" + i, new OpCodes.OP_NUMBER(i));
        }

        // Operaciones de pila
        opcodeMap.put("OP_DUP", new OpCodes.OP_DUP());
        opcodeMap.put("OP_DROP", new OpCodes.OP_DROP());

        // Comparación
        opcodeMap.put("OP_EQUAL", new OpCodes.OP_EQUAL());
        opcodeMap.put("OP_EQUALVERIFY", new OpCodes.OP_EQUALVERIFY());

        // Criptografía
        opcodeMap.put("OP_HASH160", new OpCodes.OP_HASH160());

        // Firmas
        opcodeMap.put("OP_CHECKSIG", new OpCodes.OP_CHECKSIG());
    }

    /**
     * Activa o desactiva el modo trace.
     * En modo trace, se imprime el estado de la pila después de cada operación.
     *
     * @param enabled true para activar, false para desactivar
     */
    public void setTraceMode(boolean enabled) {
        this.traceMode = enabled;
    }

    /**
     * Evalúa un script de Bitcoin.
     * El script se ejecuta de izquierda a derecha.
     *
     * @param scriptTokens Lista de tokens del script (instrucciones y datos)
     * @return true si el script es válido, false en caso contrario
     */
    public boolean evaluate(List<String> scriptTokens) {
        stack.clear();

        if (traceMode) {
            System.out.println("\n=== INICIANDO EVALUACION DE SCRIPT ===");
            System.out.println("Tokens: " + scriptTokens);
            System.out.println();
        }

        try {
            // Ejecutar cada token del script
            for (int i = 0; i < scriptTokens.size(); i++) {
                String token = scriptTokens.get(i);

                if (traceMode) {
                    System.out.println("Ejecutando: " + token);
                }

                executeToken(token);

                if (traceMode) {
                    System.out.println(stack);
                }
            }

            // Verificar el resultado final
            boolean result = verifyFinalStack();

            if (traceMode) {
                System.out.println("=== RESULTADO FINAL: " + (result ? "EXITO" : "FALLO") + " ===\n");
            }

            return result;

        } catch (Exception e) {
            if (traceMode) {
                System.out.println("ERROR: " + e.getMessage());
                System.out.println("=== SCRIPT FALLO ===\n");
            }
            return false;
        }
    }

    /**
     * Ejecuta un token individual (opcode o dato).
     * Complejidad: O(1) para lookup en HashMap
     *
     * @param token El token a ejecutar
     */
    private void executeToken(String token) {
        // Verificar si es un opcode conocido
        if (opcodeMap.containsKey(token)) {
            OpCode opcode = opcodeMap.get(token);
            opcode.execute(stack);
        }
        // Si empieza con '<' y termina con '>', es un dato hexadecimal
        else if (token.startsWith("<") && token.endsWith(">")) {
            String hexData = token.substring(1, token.length() - 1);
            byte[] data = hexToBytes(hexData);
            OpCode pushData = new OpCodes.PUSHDATA(data);
            pushData.execute(stack);
        }
        // Intentar interpretar como número decimal
        else {
            try {
                int value = Integer.parseInt(token);
                byte[] data = Stack.intToBytes(value);
                OpCode pushData = new OpCodes.PUSHDATA(data);
                pushData.execute(stack);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Token desconocido: " + token);
            }
        }
    }

    /**
     * Verifica que el estado final de la pila sea válido.
     * Un script es válido si termina con un valor verdadero en la cima.
     *
     * @return true si la pila tiene un valor verdadero en la cima
     */
    private boolean verifyFinalStack() {
        if (stack.isEmpty()) {
            return false;
        }

        byte[] top = stack.peek();
        return Stack.isTrue(top);
    }

    /**
     * Convierte una cadena hexadecimal a un array de bytes.
     *
     * @param hex Cadena hexadecimal (sin espacios)
     * @return Array de bytes
     */
    private byte[] hexToBytes(String hex) {
        // Remover espacios
        hex = hex.replaceAll("\\s+", "");

        int len = hex.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }

        return data;
    }

    /**
     * Evalúa un script P2PKH (Pay-to-PubKey-Hash) completo.
     * Formato: scriptSig + scriptPubKey
     *
     * @param signature Firma (simulada, en hex)
     * @param pubKey Clave pública (simulada, en hex)
     * @param pubKeyHash Hash de la clave pública esperada (en hex)
     * @return true si el script P2PKH es válido
     */
    public boolean evaluateP2PKH(String signature, String pubKey, String pubKeyHash) {
        // Construir el script completo: scriptSig + scriptPubKey
        List<String> script = new ArrayList<>();

        // scriptSig: <firma> <pubKey>
        script.add("<" + signature + ">");
        script.add("<" + pubKey + ">");

        // scriptPubKey: OP_DUP OP_HASH160 <pubKeyHash> OP_EQUALVERIFY OP_CHECKSIG
        script.add("OP_DUP");
        script.add("OP_HASH160");
        script.add("<" + pubKeyHash + ">");
        script.add("OP_EQUALVERIFY");
        script.add("OP_CHECKSIG");

        return evaluate(script);
    }

    /**
     * Método auxiliar para obtener el estado actual de la pila.
     * Útil para debugging y testing.
     *
     * @return Representación string de la pila
     */
    public String getStackState() {
        return stack.toString();
    }
}
