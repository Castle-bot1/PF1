import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BitcoinScriptTest {

    @Test
    public void testStackPushYPop() {
        Stack stack = new Stack();
        byte[] dato = {0x01, 0x02, 0x03};

        stack.push(dato);
        byte[] resultado = stack.pop();

        assertArrayEquals(dato, resultado);
    }

    @Test
    public void testStackVaciaAlInicio() {
        Stack stack = new Stack();
        assertTrue(stack.isEmpty());
    }

    @Test
    public void testStackNoVaciaDespuesDePush() {
        Stack stack = new Stack();
        stack.push(new byte[]{0x01});
        assertFalse(stack.isEmpty());
    }
    
    @Test
    public void testP2PKH_firmaValida_retornaTrue() {
        ScriptInterpreter interpreter = new ScriptInterpreter();

        // Firma válida: >= 64 bytes en hex = >= 128 caracteres hex
        String firma  = "3045022100" + "aa".repeat(30) + "022100" + "bb".repeat(27);
        // PubKey válida: >= 33 bytes en hex = >= 66 caracteres hex
        String pubKey = "02" + "cc".repeat(33);
        // Hash incorrecto → OP_EQUALVERIFY fallará → retorna false
        String hashIncorrecto = "00".repeat(20);

        assertFalse(interpreter.evaluateP2PKH(firma, pubKey, hashIncorrecto));
    }
    
}
