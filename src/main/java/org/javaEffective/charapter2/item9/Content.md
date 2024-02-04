# Item 9: Preferir try-with-resources a try-finally

Las bibliotecas de Java incluyen muchos recursos que deben cerrarse manualmente mediante la invocación de un método close. Ejemplos incluyen InputStream, OutputStream y java.sql.Connection. El cierre de recursos a menudo es pasado por alto por los clientes, con consecuencias previsiblemente desastrosas para el rendimiento. Aunque muchos de estos recursos utilizan finalizadores como red de seguridad, los finalizadores no funcionan muy bien (Item 8).

Históricamente, una declaración try-finally era la mejor manera de garantizar que un recurso se cerrara correctamente, incluso en caso de una excepción o un retorno.


```java
// try-finally - ¡Ya no es la mejor manera de cerrar recursos!
static String firstLineOfFile(String path) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(path));
    try {
        return br.readLine();
    } finally {
        br.close();
    }
}

// try-finally es feo cuando se usa con más de un recurso.
static void copy(String src, String dst) throws IOException {
    InputStream in = new FileInputStream(src);
    try {
        OutputStream out = new FileOutputStream(dst);
        try {
            byte[] buf = new byte[BUFFER_SIZE];
            int n;
            while ((n = in.read(buf)) >= 0)
                out.write(buf, 0, n);
        } finally {
            out.close();
        }
    } finally {
        in.close();
    }
}
```

Puede ser difícil de creer, pero incluso buenos programadores lo hicieron mal la mayor parte del tiempo. Para empezar, lo hice mal en la página 88 de Java Puzzlers [Bloch05], y nadie lo notó durante años. De hecho, dos tercios de los usos del método close en las bibliotecas de Java estaban mal en 2007.

Incluso el código correcto para cerrar recursos con declaraciones try-finally, como se ilustra en los dos ejemplos de código anteriores, tiene una deficiencia sutil. El código tanto en el bloque try como en el bloque finally es capaz de lanzar excepciones. Por ejemplo, en el método firstLineOfFile, la llamada a readLine podría lanzar una excepción debido a un fallo en el dispositivo físico subyacente, y la llamada a close también podría fallar por la misma razón. En estas circunstancias, la segunda excepción obliterate completamente la primera. No hay ningún registro de la primera excepción en el rastreo de la pila de excepciones, lo que puede complicar enormemente la depuración en sistemas reales, por lo general es la primera excepción la que desea ver para diagnosticar el problema. Aunque es posible escribir código para suprimir la segunda excepción en favor de la primera, prácticamente nadie lo hizo porque es demasiado verbose.

Todos estos problemas se resolvieron de una vez cuando Java 7 introdujo la declaración 'try-with-resources'. Para ser utilizable con esta construcción, un recurso debe implementar la interfaz AutoCloseable, que consiste en un único método close que devuelve void. Muchas clases e interfaces en las bibliotecas de Java y en bibliotecas de terceros ahora implementan o extienden AutoCloseable. Si escribes una clase que representa un recurso que debe cerrarse, tu clase también debería implementar AutoCloseable.


```java
// try-with-resources - ¡la mejor manera de cerrar recursos!
static String firstLineOfFile(String path) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        return br.readLine();
    }
}

// try-with-resources en múltiples recursos - corto y dulce
static void copy(String src, String dst) throws IOException {
    try (InputStream in = new FileInputStream(src);
         OutputStream out = new FileOutputStream(dst)) {
        byte[] buf = new byte[BUFFER_SIZE];
        int n;
        while ((n = in.read(buf)) >= 0) {
            out.write(buf, 0, n);
        }
    }
}

```
No solo las versiones try-with-resources son más cortas y legibles que las originales, sino que también proporcionan diagnósticos mucho mejores. Considera el método firstLineOfFile

El método firstLineOfFile. Si se lanzan excepciones tanto por la llamada readLine como por el cierre (invisible), la última excepción se suprime en favor de la primera. De hecho, varias excepciones pueden ser suprimidas para preservar la excepción que realmente desea ver. Estas excepciones suprimidas no son simplemente descartadas; se imprimen en la traza de pila con una notación que indica que fueron suprimidas. También puedes acceder a ellas programáticamente con el método getSuppressed, que se agregó a Throwable en Java 7.

Puedes poner cláusulas catch en las declaraciones try-with-resources, al igual que en las declaraciones try-finally regulares. Esto te permite manejar excepciones sin ensuciar tu código con otra capa de anidamiento. Como ejemplo un poco forzado, aquí tienes una versión de nuestro método firstLineOfFile que no lanza excepciones, sino que toma un valor predeterminado para devolver si no puede abrir el archivo o leer de él.

```java
// try-with-resources con una cláusula catch
static String firstLineOfFile(String path, String defaultVal) {
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        return br.readLine();
    } catch (IOException e) {
        return defaultVal;
    }
}
```

La lección es clara: Siempre usa try-with-resources en lugar de try-finally cuando trabajes con recursos que deben cerrarse. El código resultante es más corto y claro, y las excepciones que genera son más útiles. La declaración try-with-resources facilita la escritura de código correcto usando recursos que deben cerrarse, lo que era prácticamente imposible usando try-finally.
