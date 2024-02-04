### Item 8: Evita los finalizadores y los limpiadores

Los finalizadores son impredecibles, a menudo peligrosos y generalmente innecesarios. Su uso puede causar un comportamiento errático, un rendimiento deficiente y problemas de portabilidad. Los finalizadores tienen algunos usos válidos, que cubriremos más adelante en este apartado, pero como regla general, deberías evitarlos. A partir de Java 9, los finalizadores han sido deprecados, pero aún están siendo utilizados por las bibliotecas de Java. El reemplazo de Java 9 para los finalizadores son los limpiadores. Los limpiadores son menos peligrosos que los finalizadores, pero aún son impredecibles, lentos y generalmente innecesarios.

Los programadores de C++ deben tener cuidado de no pensar en los finalizadores o limpiadores como el análogo en Java de los destructores de C++. En C++, los destructores son la forma normal de recuperar los recursos asociados con un objeto, un complemento necesario para los constructores. En Java, el recolector de basura reclama el almacenamiento asociado con un objeto cuando se vuelve inaccesible, sin requerir ningún esfuerzo especial por parte del programador. Los destructores de C++ también se utilizan para recuperar otros recursos que no son de memoria. En Java, un bloque try-with-resources o try-finally se utiliza para este propósito (Apartado 9).

Una limitación de los finalizadores y limpiadores es que no hay garantía de que se ejecuten de manera oportuna [JLS, 12.6]. Puede pasar un tiempo arbitrariamente largo entre el momento en que un objeto se vuelve inaccesible y el momento en que se ejecuta su finalizador o limpiador. Esto significa que nunca deberías hacer nada crítico en términos de tiempo en un finalizador o limpiador. Por ejemplo, es un error grave depender de un finalizador o limpiador para cerrar archivos porque los descriptores de archivos abiertos son un recurso limitado. Si se dejan muchos archivos abiertos como resultado de la tardanza del sistema en ejecutar los finalizadores o limpiadores, un programa puede fallar porque ya no puede abrir archivos.

La rapidez con la que se ejecutan los finalizadores y limpiadores es principalmente una función del algoritmo de recolección de basura, que varía ampliamente entre las implementaciones. El comportamiento de un programa que depende de la prontitud de la ejecución del finalizador o limpiador también puede variar. Es completamente posible que dicho programa se ejecute perfectamente en la JVM en la que lo pruebas y luego falle miserablemente en la que prefiera tu cliente más importante.

La finalización tardía no es solo un problema teórico. Proporcionar un finalizador para una clase puede retrasar arbitrariamente la reclamación de sus instancias. Un colega depuró una aplicación de GUI de larga duración que moría misteriosamente con un OutOfMemoryError. El análisis reveló que en el momento de su muerte, la aplicación tenía miles de objetos gráficos en su cola de finalización esperando ser finalizados y reclamados. Desafortunadamente, el hilo de finalización se estaba ejecutando con una prioridad más baja que otro hilo de aplicación, por lo que los objetos no se finalizaban al ritmo al que se volvían elegibles para la finalización.

La especificación del lenguaje no garantiza qué hilo ejecutará los finalizadores, por lo que no hay una manera portátil de prevenir este tipo de problema aparte de abstenerse de usar finalizadores. Los limpiadores son un poco mejores que los finalizadores en este aspecto porque los autores de clases tienen control sobre sus propios hilos de limpieza, pero los limpiadores aún se ejecutan en segundo plano, bajo el control del recolector de basura, por lo que no puede garantizarse una limpieza oportuna. No solo la especificación no garantiza que los finalizadores o limpiadores se ejecuten de manera oportuna; tampoco garantiza que se ejecuten en absoluto. Es completamente posible, e incluso probable, que un programa termine sin ejecutarlos en algunos objetos que ya no son alcanzables. Como consecuencia, nunca debes depender de un finalizador o limpiador para actualizar un estado persistente. Por ejemplo, depender de un finalizador o limpiador para liberar un bloqueo persistente en un recurso compartido como una base de datos es una buena manera de detener por completo tu sistema distribuido.

No te dejes seducir por los métodos System.gc y System.runFinalization. Pueden aumentar las probabilidades de que los finalizadores o limpiadores se ejecuten, pero no lo garantizan. Una vez se afirmó que dos métodos garantizaban esto: System.runFinalizersOnExit y su gemelo maligno, Runtime.runFinalizersOnExit. Estos métodos tienen fallas fatales y han sido deprecados durante décadas. Otro problema con los finalizadores es que se ignora una excepción no capturada lanzada durante la finalización, y la finalización de ese objeto termina. Las excepciones no capturadas pueden dejar otros objetos en un estado corrupto. Si otro hilo intenta usar un objeto corrupto, puede resultar en un comportamiento no determinístico arbitrario. Normalmente, una excepción no capturada terminará el hilo e imprimirá una traza de pila, pero no si ocurre en un finalizador; ni siquiera imprimirá una advertencia. Los limpiadores no tienen este problema porque una biblioteca que utiliza un limpiador tiene control sobre su hilo.

Hay una penalización de rendimiento severa por usar finalizadores y limpiadores. En mi máquina, el tiempo para crear un objeto AutoCloseable simple, cerrarlo usando try-with-resources y que el recolector de basura lo reclame es de aproximadamente 12 ns. Usar un finalizador en su lugar aumenta el tiempo a 550 ns. En otras palabras, es aproximadamente 50 veces más lento crear y destruir objetos con finalizadores. Esto se debe principalmente a que los finalizadores inhiben una recolección de basura eficiente. Los limpiadores son comparables en velocidad a los finalizadores si los usas para limpiar todas las instancias de la clase (alrededor de 500 ns por instancia en mi máquina), pero los limpiadores son mucho más rápidos si los usas solo como una red de seguridad, como se discute a continuación. En estas circunstancias, crear, limpiar y destruir un objeto toma alrededor de 66 ns en mi máquina, lo que significa que pagas un factor de cinco (no cincuenta) por el seguro de una red de seguridad si no lo usas.

Los finalizadores tienen un grave problema de seguridad: abren tu clase a ataques de finalizadores. La idea detrás de un ataque de finalizador es simple: si se lanza una excepción desde un constructor o sus equivalentes de serialización, los métodos readObject y readResolve (Capítulo 12), el finalizador de una subclase maliciosa puede ejecutarse en el objeto parcialmente construido que debería haber "muerto en la parra". Este finalizador puede registrar una referencia al objeto en un campo estático, evitando que sea recolectado por el recolector de basura. Una vez que el objeto malformado ha sido registrado, es un simple asunto invocar métodos arbitrarios en este objeto que nunca deberían haber sido permitidos existir en primer lugar. Lanzar una excepción desde un constructor debería ser suficiente para evitar que un objeto llegue a existir; en presencia de finalizadores, no lo es. Tales ataques pueden tener consecuencias graves. Las clases finales son inmunes a los ataques de finalizadores porque nadie puede escribir una subclase maliciosa de una clase final. Para proteger las clases no finales de los ataques de finalizadores, escribe un método final finalize que no haga nada.

Entonces, ¿qué deberías hacer en lugar de escribir un finalizador o limpiador para una clase cuyos objetos encapsulan recursos que requieren terminación, como archivos o hilos? Simplemente haz que tu clase implemente AutoCloseable y requiere que sus clientes invoquen el método close en cada instancia cuando ya no sea necesario, típicamente usando try-with-resources para garantizar la terminación incluso en caso de excepciones (Item 9). Un detalle que vale la pena mencionar es que la instancia debe llevar un registro de si ha sido cerrada: el método close debe registrar en un campo que el objeto ya no es válido, y otros métodos deben verificar este campo y lanzar una IllegalStateException si son llamados después de que el objeto haya sido cerrado.

Entonces, ¿para qué sirven los limpiadores y finalizadores, si es que sirven para algo? Tienen quizás dos usos legítimos. Uno es actuar como una red de seguridad en caso de que el propietario de un recurso descuide llamar a su método close. Aunque no hay garantía de que el limpiador o finalizador se ejecute de manera oportuna (o en absoluto), es mejor liberar el recurso tarde que nunca si el cliente falla en hacerlo. Si estás considerando escribir un finalizador de red de seguridad, piensa detenidamente si la protección vale la pena el costo. Algunas clases de biblioteca de Java, como FileInputStream, FileOutputStream, ThreadPoolExecutor y java.sql.Connection, tienen finalizadores que actúan como redes de seguridad.

Un segundo uso legítimo de los limpiadores concierne a los objetos con pares nativos. Un par nativo es un objeto nativo (no Java) al que un objeto normal delega mediante métodos nativos. Como un par nativo no es un objeto normal, el recolector de basura no lo conoce y no puede reclamarlo cuando su par de Java es reclamado. Un limpiador o finalizador puede ser un vehículo apropiado para esta tarea, asumiendo que el rendimiento es aceptable y que el par nativo no tiene recursos críticos. Si el rendimiento no es aceptable o el par nativo tiene recursos que deben ser reclamados de manera oportuna, la clase debería tener un método close, como se describió anteriormente.


Los limpiadores son un poco difíciles de usar. A continuación se muestra una clase Room simple que demuestra la funcionalidad. Supongamos que las habitaciones deben limpiarse antes de ser reclamadas. La clase Room implementa AutoCloseable; el hecho de que su red de seguridad de limpieza automática utilice un limpiador es simplemente un detalle de implementación. A diferencia de los finalizadores, los limpiadores no contaminan la API pública de una clase.

```java
// An autocloseable class using a cleaner as a safety net
public class Room implements AutoCloseable {
    private static final Cleaner cleaner = Cleaner.create();

    // Resource that requires cleaning. Must not refer to Room!
    private static class State implements Runnable {
        int numJunkPiles; // Number of junk piles in this room

        State(int numJunkPiles) {
            this.numJunkPiles = numJunkPiles;
        }

        // Invoked by close method or cleaner
        @Override
        public void run() {
            System.out.println("Cleaning room");
            numJunkPiles = 0;
        }
    }

    // The state of this room, shared with our cleanable
    private final State state;

    // Our cleanable. Cleans the room when it’s eligible for gc
    private final Cleaner.Cleanable cleanable;

    public Room(int numJunkPiles) {
        state = new State(numJunkPiles);
        cleanable = cleaner.register(this, state);
    }

    @Override
    public void close() {
        cleanable.clean();
    }
}

```