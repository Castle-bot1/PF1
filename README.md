# Intérprete de Bitcoin Script - Fase 1

**Universidad del Valle de Guatemala**  
**Algoritmos y Estructura de Datos - Sección 30**  
**Proyecto #1 - Fase 1**

## Integrantes del Grupo
- Diego Castillo - 25779
- Edgar Guevara - 251154
- Hector Duarte - 25939

## Descripción del Proyecto

Este proyecto implementa un intérprete de un subconjunto de Bitcoin Script, un lenguaje stack-based usado para la validación de transacciones en Bitcoin. El intérprete permite validar scripts de bloqueo (scriptPubKey) y desbloqueo (scriptSig) en un entorno didáctico.

## Características de la Fase 1

### Opcodes Implementados

#### Literales y Empuje de Datos
- `OP_0` / `OP_FALSE`: Empuja un array vacío (representa false/0)
- `OP_1` a `OP_16`: Empujan números del 1 al 16
- `PUSHDATA`: Empuja datos hexadecimales a la pila

#### Operaciones de Pila
- `OP_DUP`: Duplica el elemento en la cima
- `OP_DROP`: Elimina el elemento en la cima

#### Operaciones de Comparación
- `OP_EQUAL`: Compara los dos elementos superiores
- `OP_EQUALVERIFY`: Igual que OP_EQUAL, pero falla si no son iguales

#### Operaciones Criptográficas (Simuladas)
- `OP_HASH160`: Calcula SHA-256 + RIPEMD-160 (simplificado para didáctica)

#### Verificación de Firmas (Simuladas)
- `OP_CHECKSIG`: Verifica firma digital (simulada con validación mock)

### Prueba P2PKH

La implementación incluye una prueba completa de Pay-to-PubKey-Hash (P2PKH), el tipo de transacción más común en Bitcoin:

```
<firma> <pubKey> OP_DUP OP_HASH160 <pubKeyHash> OP_EQUALVERIFY OP_CHECKSIG
```

## Estructuras de Datos Utilizadas (Java Collections Framework)

### 1. ArrayDeque (Stack)
**Uso:** Pila principal de ejecución  
**Complejidad:**
- `push()`: O(1)
- `pop()`: O(1)
- `peek()`: O(1)

**Justificación:** ArrayDeque es más eficiente que la clase Stack tradicional. No tiene overhead de sincronización y gestiona mejor la memoria. Es perfecta para operaciones LIFO (Last In, First Out) que son fundamentales en Bitcoin Script.

### 2. HashMap
**Uso:** Mapeo de nombres de opcodes a sus implementaciones  
**Complejidad:**
- `put()`: O(1) promedio
- `get()`: O(1) promedio
- `containsKey()`: O(1) promedio

**Justificación:** Permite búsqueda instantánea de opcodes por nombre. Hace el código más modular y facilita agregar nuevas instrucciones. La complejidad constante es crucial para mantener el rendimiento del intérprete.

### 3. ArrayList
**Uso:** Almacenamiento de la secuencia de instrucciones del script  
**Complejidad:**
- `add()`: O(1) amortizado
- `get(i)`: O(1)
- Iteración: O(n)

**Justificación:** Permite acceso secuencial eficiente a las instrucciones. Como Bitcoin Script se ejecuta de izquierda a derecha, ArrayList es ideal para mantener el orden y recorrer las instrucciones linealmente.

## Estructura del Proyecto

```
BitcoinScriptInterpreter/
├── src/
│   ├── Stack.java              # Implementación de la pila de datos
│   ├── OpCode.java             # Interfaz para opcodes
│   ├── OpCodes.java            # Implementaciones de opcodes
│   ├── ScriptInterpreter.java  # Intérprete principal
│   └── Main.java               # Clase principal con demos
├── test/
│   └── ScriptInterpreterTest.java  # Pruebas unitarias JUnit
└── README.md
```

## Compilación y Ejecución

### Compilar el proyecto

```bash
cd BitcoinScriptInterpreter/src
javac *.java
```

### Ejecutar el programa principal

```bash
java Main
```

### Ejecutar pruebas unitarias (requiere JUnit 5)

```bash
cd ../test
javac -cp .:../src:/path/to/junit-platform-console-standalone.jar *.java
java -jar /path/to/junit-platform-console-standalone.jar --class-path .:../src --scan-class-path
```

## Ejemplos de Uso

### Ejemplo 1: Script Simple
```java
List<String> script = Arrays.asList("5", "OP_DUP", "OP_EQUAL");
boolean valid = interpreter.evaluate(script);
// Resultado: true (5 == 5)
```

### Ejemplo 2: Prueba P2PKH
```java
String signature = "3045022100..."; // 64+ bytes
String pubKey = "02...";            // 33+ bytes  
String pubKeyHash = "89abcdef...";  // 20 bytes

List<String> script = Arrays.asList(
    "<" + signature + ">",
    "<" + pubKey + ">",
    "OP_DUP",
    "OP_HASH160",
    "<" + pubKeyHash + ">",
    "OP_EQUALVERIFY",
    "OP_CHECKSIG"
);

boolean valid = interpreter.evaluate(script);
```

## Modo Trace

El intérprete incluye un modo trace que imprime el estado de la pila después de cada operación:

```java
interpreter.setTraceMode(true);
interpreter.evaluate(script);
```

Salida de ejemplo:
```
=== INICIANDO EVALUACIÓN DE SCRIPT ===
Tokens: [5, OP_DUP, OP_EQUAL]

Ejecutando: 5
Stack (1 elementos):
  [0] [05] (int: 5)

Ejecutando: OP_DUP
Stack (2 elementos):
  [0] [05] (int: 5)
  [1] [05] (int: 5)

Ejecutando: OP_EQUAL
Stack (1 elementos):
  [0] [01] (int: 1)

=== RESULTADO FINAL: ÉXITO ===
```

## Pruebas Implementadas

El proyecto incluye pruebas unitarias exhaustivas con JUnit 5:

1. **Pruebas de Literales**: OP_0, OP_1-16, PUSHDATA
2. **Pruebas de Pila**: OP_DUP, OP_DROP con casos válidos e inválidos
3. **Pruebas de Comparación**: OP_EQUAL, OP_EQUALVERIFY
4. **Pruebas Criptográficas**: OP_HASH160
5. **Pruebas de Firmas**: OP_CHECKSIG con firmas válidas e inválidas
6. **Pruebas P2PKH**: Transacciones completas válidas e inválidas
7. **Casos Borde**: Pila vacía, opcodes inválidos, scripts vacíos

## Decisiones de Diseño

### Simulación de Criptografía

Para la Fase 1, las operaciones criptográficas están simuladas:

- **OP_HASH160**: Usa SHA-256 y toma los primeros 20 bytes (en lugar de SHA-256 + RIPEMD-160)
- **OP_CHECKSIG**: Valida que la firma tenga al menos 64 bytes y la pubKey al menos 33 bytes

Esto permite enfocarse en la lógica del intérprete sin complejidad criptográfica real.

### Representación de Datos

Todos los datos se almacenan como `byte[]` (arrays de bytes):
- Los números se convierten a/desde bytes usando little-endian
- Los booleanos se interpretan como: vacío/ceros = false, cualquier otro valor = true

### Manejo de Errores

El intérprete usa excepciones para manejar errores:
- `IllegalStateException`: Para errores de ejecución (pila vacía, valores inválidos)
- `IllegalArgumentException`: Para tokens o datos inválidos

## Recursos Utilizados

1. **Bitcoin IDE**: https://siminchen.github.io/bitcoinIDE/build/editor.html
2. **Learn Me A Bitcoin**: https://learnmeabitcoin.com/technical/script/
3. **Opcode Explained**: https://opcodeexplained.com/opcodes/
4. **Java Collections Framework**: https://docs.oracle.com/javase/8/docs/technotes/guides/collections/

## Próximos Pasos (Fase 2)

La Fase 2 incluirá:
- Operaciones aritméticas: OP_ADD, OP_SUB, comparaciones numéricas
- Control de flujo: OP_IF, OP_NOTIF, OP_ELSE, OP_ENDIF
- Operaciones lógicas: OP_NOT, OP_BOOLAND, OP_BOOLOR
- Más operaciones de pila: OP_SWAP, OP_OVER
- OP_CHECKMULTISIG (opcional avanzado)

## Conclusiones

Este prototipo demuestra:
1. La implementación de un evaluador stack-based funcional
2. El uso efectivo del Java Collections Framework
3. La importancia de seleccionar estructuras de datos apropiadas (ArrayDeque, HashMap, ArrayList)
4. Comprensión de los fundamentos de Bitcoin Script
5. Buenas prácticas de programación: documentación, pruebas, manejo de errores

---

**Fecha de Entrega Fase 1**: Semana del 9 al 13 de febrero 2026  
**Catedrático**: Ing. Sebastián Arriola
