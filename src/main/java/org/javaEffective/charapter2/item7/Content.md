Si cambiaste de un lenguaje con gestión manual de memoria, como C o C++, a un lenguaje con recolector de basura, como Java, tu trabajo como programador se simplificó considerablemente debido a que tus objetos se reclaman automáticamente cuando terminas de usarlos. Parece casi como magia la primera vez que lo experimentas. Puede dar la impresión de que no tienes que preocuparte por la gestión de memoria, pero esto no es del todo cierto.


```java
public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }
    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }
    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        return elements[--size];
    }

    private void ensureCapacity() {
        if (elements.length == size)
            elements = Arrays.copyOf(elements, 2 * size + 1);
    }

    public int getSize() {
        return this.size;
    }
}
```

No hay nada evidentemente incorrecto en este programa (pero consulta el Ítem 29 para una versión genérica). Podrías probarlo exhaustivamente y pasaría cada prueba con colores brillantes, pero hay un problema acechando. Hablando de manera general, el programa tiene una "fuga de memoria", que puede manifestarse silenciosamente como un rendimiento reducido debido a un aumento en la actividad del recolector de basura o un mayor uso de memoria. En casos extremos, estas fugas de memoria pueden causar paginación de disco e incluso fallos en el programa con un OutOfMemoryError, aunque tales fallos son relativamente raros.

Entonces, ¿dónde está la fuga de memoria? Si una pila crece y luego disminuye, los objetos que se sacaron de la pila no serán recolectados por el recolector de basura, incluso si el programa que utiliza la pila no tiene más referencias a ellos. Esto se debe a que la pila mantiene referencias obsoletas a estos objetos. Una referencia obsoleta es simplemente una referencia que nunca se desreferenciará de nuevo. En este caso, cualquier referencia fuera de la "porción activa" del arreglo de elementos es obsoleta. La porción activa consiste en los elementos cuyo índice es menor que el tamaño.

Las fugas de memoria en lenguajes con recolección de basura (más correctamente conocidas como retenciones no intencionales de objetos) son insidiosas. Si se retiene una referencia de objeto sin intención, no solo se excluye ese objeto de la recolección de basura, sino también cualquier objeto referenciado por ese objeto, y así sucesivamente. Incluso si solo se retienen unas pocas referencias de objetos sin intención, muchos objetos pueden quedar fuera de la recolección de basura, con efectos potencialmente grandes en el rendimiento.

La solución para este tipo de problema es simple: anula las referencias una vez que se vuelven obsoletas. En el caso de nuestra clase Stack, la referencia a un elemento se vuelve obsoleta tan pronto como se saca de la pila. La versión corregida del método pop se ve así:

```java
    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        Object result = elements[--size];
        elements[size] = null;
        return result;
    }
```
Un beneficio adicional de anular las referencias obsoletas es que, si posteriormente se desreferencian por error, el programa fallará de inmediato con una NullPointerException, en lugar de realizar silenciosamente la acción incorrecta. Siempre es beneficioso detectar errores de programación lo más rápido posible.

Cuando los programadores son picados por este problema por primera vez, pueden reaccionar exageradamente anulando cada referencia de objeto tan pronto como el programa haya terminado de usarla. Esto ni es necesario ni deseable; simplemente ensucia innecesariamente el programa. Anular referencias de objetos debería ser la excepción más que la norma.

La mejor manera de eliminar una referencia obsoleta es permitir que la variable que contenía la referencia caiga fuera de alcance. Esto ocurre naturalmente si defines cada variable en el alcance más estrecho posible 


Entonces, ¿cuándo deberías anular una referencia? ¿Qué aspecto de la clase Stack la hace propensa a fugas de memoria? En pocas palabras, gestiona su propia memoria. El conjunto de almacenamiento consiste en los elementos del arreglo de elementos (las celdas de referencia de objetos, no los objetos en sí). Los elementos en la porción activa del arreglo (como se definió anteriormente) están asignados, y aquellos en el resto del arreglo están libres. El recolector de basura no tiene forma de saber esto; para el recolector de basura, todas las referencias de objetos en el arreglo de elementos son igualmente válidas. Solo el programador sabe que la porción inactiva del arreglo no es importante. El programador comunica efectivamente este hecho al recolector de basura anulando manualmente los elementos del arreglo tan pronto como se convierten en parte de la porción inactiva.

En general, siempre que una clase gestione su propia memoria, el programador debe estar atento a las fugas de memoria. Siempre que se libere un elemento, cualquier referencia de objeto contenida en el elemento debería anularse.

Otra fuente común de fugas de memoria son las cachés. Una vez que colocas una referencia de objeto en una caché, es fácil olvidar que está allí y dejarla en la caché mucho después de que sea irrelevante. Hay varias soluciones para este problema. Si tienes la suerte de implementar una caché para la cual una entrada es relevante exactamente mientras haya referencias a su clave fuera de la caché, representa la caché como un WeakHashMap; las entradas se eliminarán automáticamente después de volverse obsoletas. Recuerda que WeakHashMap es útil solo si la vida útil deseada de las entradas de la caché está determinada por referencias externas a la clave, no al valor.

Más comúnmente, la vida útil útil de una entrada de caché está menos definida, con entradas que se vuelven menos valiosas con el tiempo. En estas circunstancias, la caché debería limpiarse ocasionalmente de entradas que han caído en desuso. Esto se puede hacer mediante un hilo en segundo plano (quizás un ScheduledThreadPoolExecutor) o como un efecto secundario al agregar nuevas entradas a la caché. La clase LinkedHashMap facilita este último enfoque con su método removeEldestEntry. Para cachés más sofisticadas, es posible que necesites usar java.lang.ref directamente.

Una tercera fuente común de fugas de memoria son los listeners y otros callbacks. Si implementas una API donde los clientes registran callbacks pero no los cancelan explícitamente, se acumularán a menos que tomes alguna medida. Una forma de asegurarte de que los callbacks se recojan de manera pronta es almacenar solo referencias débiles a ellos, por ejemplo, almacenándolos solo como claves en un WeakHashMap.

Dado que las fugas de memoria generalmente no se manifiestan como fallas obvias, pueden permanecer presentes en un sistema durante años. Típicamente, se descubren solo como resultado de una inspección cuidadosa del código o con la ayuda de una herramienta de depuración conocida como un perfilador de montón. Por lo tanto, es muy deseable aprender a anticipar problemas como este antes de que ocurran y evitar que sucedan.




