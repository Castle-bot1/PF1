import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Clase principal que demuestra el funcionamiento del intérprete
 * de Bitcoin Script con pruebas P2PKH.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("-------------------------------------------------------------");
        System.out.println("    INTÉRPRETE DE BITCOIN SCRIPT - FASE 1                   ");


        // Crear instancia del intérprete
        ScriptInterpreter interpreter = new ScriptInterpreter();

        // ============ PRUEBAS BÁSICAS ============
        System.out.println("--------------------------------------------------------------");
        System.out.println("PRUEBAS BÁSICAS DE OPCODES");
        System.out.println("--------------------------------------------------------------\n");

        pruebaBasica1(interpreter);
        pruebaBasica2(interpreter);
        pruebaBasica3(interpreter);

        // ============ PRUEBA P2PKH ============
        System.out.println("\n------------------------------------------------------------");
        System.out.println("PRUEBA P2PKH (Pay-to-PubKey-Hash)");
        System.out.println("--------------------------------------------------------------\n");

        pruebaP2PKH_Valida(interpreter);
        pruebaP2PKH_Invalida(interpreter);

        // ============ EJEMPLOS DEL DOCUMENTO ============
        System.out.println("\n------------------------------------------------------------");
        System.out.println("EJEMPLO DEL DOCUMENTO DEL PROYECTO");
        System.out.println("--------------------------------------------------------------\n");

        ejemploDocumento(interpreter);
    }

    /**
     * Prueba básica 1: Verificar OP_DUP y OP_EQUAL
     * Script: 5 OP_DUP OP_EQUAL
     * Resultado esperado: true (5 == 5)
     */
    private static void pruebaBasica1(ScriptInterpreter interpreter) {
        System.out.println("--- Prueba 1: OP_DUP y OP_EQUAL ---");
        System.out.println("Script: 5 OP_DUP OP_EQUAL");
        System.out.println("Descripcion: Duplica el 5 y verifica que sean iguales\n");

        interpreter.setTraceMode(true);
        List<String> script = Arrays.asList("5", "OP_DUP", "OP_EQUAL");
        boolean resultado = interpreter.evaluate(script);

        System.out.println("Resultado: " + (resultado ? "EXITO" : "FALLO"));
        System.out.println();
    }

    /**
     * Prueba básica 2: Verificar OP_DROP
     * Script: 3 5 OP_DROP
     * Resultado esperado: true (queda 3 en la pila)
     */
    private static void pruebaBasica2(ScriptInterpreter interpreter) {
        System.out.println("--- Prueba 2: OP_DROP ---");
        System.out.println("Script: 3 5 OP_DROP");
        System.out.println("Descripcion: Apila 3 y 5, luego elimina el 5\n");

        interpreter.setTraceMode(true);
        List<String> script = Arrays.asList("3", "5", "OP_DROP");
        boolean resultado = interpreter.evaluate(script);

        System.out.println("Resultado: " + (resultado ? "EXITO" : "FALLO"));
        System.out.println();
    }

    /**
     * Prueba básica 3: Verificar OP_EQUALVERIFY (debe fallar si no son iguales)
     * Script: 3 5 OP_EQUALVERIFY
     * Resultado esperado: false (3 != 5)
     */
    private static void pruebaBasica3(ScriptInterpreter interpreter) {
        System.out.println("--- Prueba 3: OP_EQUALVERIFY (fallo esperado) ---");
        System.out.println("Script: 3 5 OP_EQUALVERIFY");
        System.out.println("Descripcion: Compara 3 y 5, debe fallar porque no son iguales\n");

        interpreter.setTraceMode(true);
        List<String> script = Arrays.asList("3", "5", "OP_EQUALVERIFY");
        boolean resultado = interpreter.evaluate(script);

        System.out.println("Resultado: " + (resultado ? "EXITO" : "FALLO (esperado)"));
        System.out.println();
    }

    /**
     * Prueba P2PKH válida
     * Simula una transacción Pay-to-PubKey-Hash correcta
     */
    private static void pruebaP2PKH_Valida(ScriptInterpreter interpreter) {
        System.out.println("--- Prueba P2PKH VALIDA ---");
        System.out.println("Estructura: <firma> <pubKey> OP_DUP OP_HASH160 <pubKeyHash> OP_EQUALVERIFY OP_CHECKSIG\n");

        // Datos simulados (en hexadecimal)
        // En un sistema real, estos serían datos criptográficos reales

        // Firma simulada (64 bytes)
        String signature = "3045022100" + "a".repeat(60) + "022100" + "b".repeat(60);

        // Clave pública simulada (33 bytes - compressed)
        String pubKey = "02" + "c".repeat(64);

        // Para la prueba, calculamos el hash de la pubKey que esperamos
        // En la simulación, usaremos un hash pre-calculado
        String pubKeyHash = "89abcdef" + "1".repeat(32); // 20 bytes en hex

        System.out.println("Firma (hex): " + signature.substring(0, 40) + "...");
        System.out.println("PubKey (hex): " + pubKey.substring(0, 20) + "...");
        System.out.println("PubKeyHash (hex): " + pubKeyHash.substring(0, 20) + "...\n");

        interpreter.setTraceMode(true);

        // Construir el script P2PKH manualmente para tener control total
        List<String> script = new ArrayList<>();
        script.add("<" + signature + ">");
        script.add("<" + pubKey + ">");
        script.add("OP_DUP");
        script.add("OP_HASH160");
        script.add("<" + pubKeyHash + ">");
        script.add("OP_EQUALVERIFY");
        script.add("OP_CHECKSIG");

        boolean resultado = interpreter.evaluate(script);

        System.out.println("Resultado P2PKH: " + (resultado ? "TRANSACCION VALIDA" : "TRANSACCION INVALIDA"));
        System.out.println();
    }

    /**
     * Prueba P2PKH inválida (firma incorrecta)
     * Simula una transacción con firma que no pasa la verificación
     */
    private static void pruebaP2PKH_Invalida(ScriptInterpreter interpreter) {
        System.out.println("--- Prueba P2PKH INVÁLIDA (firma incorrecta) ---");
        System.out.println("Descripcion: Simula una transaccion con firma muy corta (invalida)\n");

        // Firma simulada inválida (demasiado corta)
        String signatureInvalida = "3045"; // Solo 2 bytes, necesita al menos 64

        // Clave pública simulada válida
        String pubKey = "02" + "c".repeat(64);

        String pubKeyHash = "89abcdef" + "1".repeat(32);

        System.out.println("Firma invalida (hex): " + signatureInvalida);
        System.out.println("PubKey (hex): " + pubKey.substring(0, 20) + "...\n");

        interpreter.setTraceMode(false); // Sin trace para esta prueba

        List<String> script = new ArrayList<>();
        script.add("<" + signatureInvalida + ">");
        script.add("<" + pubKey + ">");
        script.add("OP_DUP");
        script.add("OP_HASH160");
        script.add("<" + pubKeyHash + ">");
        script.add("OP_EQUALVERIFY");
        script.add("OP_CHECKSIG");

        boolean resultado = interpreter.evaluate(script);

        System.out.println("Resultado P2PKH: " + (resultado ? "TRANSACCION VALIDA" : "TRANSACCION INVALIDA (esperado)"));
        System.out.println();
    }

    /**
     * Ejemplo del documento del proyecto
     * Script: 1 2 OP_ADD 5 OP_GREATERTHAN
     * Nota: OP_ADD y OP_GREATERTHAN están en Fase 2, este ejemplo fallará
     */
    private static void ejemploDocumento(ScriptInterpreter interpreter) {
        System.out.println("--- Ejemplo del Documento ---");
        System.out.println("Script: 1 2 OP_ADD 5 OP_GREATERTHAN");
        System.out.println("Nota: Este script requiere OP_ADD y OP_GREATERTHAN (Fase 2)");
        System.out.println("Resultado esperado: FALLO (instrucciones no implementadas aún)\n");

        interpreter.setTraceMode(false);

        try {
            List<String> script = Arrays.asList("1", "2", "OP_ADD", "5", "OP_GREATERTHAN");
            boolean resultado = interpreter.evaluate(script);
            System.out.println("Resultado: " + (resultado ? "EXITO" : "FALLO"));
        } catch (Exception e) {
            System.out.println("Error (esperado): " + e.getMessage());
        }

        System.out.println("\nNota: Los opcodes aritmeticos serán implementados en la Fase 2");
    }
}
