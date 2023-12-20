
A menudo es apropiado reutilizar un solo objeto en lugar de crear uno nuevo cada vez que se necesita. La reutilización puede ser más rápida y elegante. Un objeto siempre puede ser reutilizado si es inmutable (Ítem 17).
Como un ejemplo extremo de lo que no se debe hacer, considera esta declaración:

```java
String s = new String("bikini"); // DON'T DO THIS!
```
La declaración crea una nueva instancia de String cada vez que se ejecuta, y ninguna de esas creaciones de objetos es necesaria. El argumento del constructor de String ("bikini") es en sí mismo una instancia de String, funcionalmente idéntica a todos los objetos creados por el constructor. Si este uso ocurre en un bucle o en un método invocado con frecuencia, se pueden crear millones de instancias de String innecesariamente.
La versión mejorada es simplemente la siguiente:

```java
 String s = "bikini";
```

Esta versión utiliza una única instancia de String en lugar de crear una nueva cada vez que se ejecuta. Además, se garantiza que el objeto será reutilizado por cualquier otro código que se ejecute en la misma máquina virtual y que contenga el mismo literal de cadena [JLS, 3.10.5].
A menudo puedes evitar la creación de objetos innecesarios utilizando métodos de fábrica estáticos (Ítem 1) en lugar de constructores en clases inmutables que los proporcionan.
Por ejemplo, el método de fábrica Boolean.valueOf(String) es preferible al constructor Boolean(String), que fue deprecado en Java 9. El constructor debe crear un nuevo objeto cada vez que se llama, mientras que el método de fábrica nunca está obligado a hacerlo y en la práctica no lo hará. Además de reutilizar objetos inmutables, también puedes reutilizar objetos mutables si sabes que no serán modificados.
Algunas creaciones de objetos son mucho más costosas que otras. Si necesitas dicho "objeto costoso" repetidamente, puede ser aconsejable almacenarlo en caché para su reutilización. Desafortunadamente, no siempre es obvio cuando estás creando dicho objeto.
Supongamos que quieres escribir un método para determinar si una cadena es un numeral romano válido. Aquí está la forma más sencilla de hacerlo usando una expresión regular:


```java
// Performance can be greatly improved!
static boolean isRomanNumeral(String s) {
     return s.matches("^(?=.)M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
}
```
El problema con esta implementación es que depende del método String.matches. Si bien String.matches es la forma más sencilla de verificar si una cadena coincide con una expresión regular, no es adecuado para su uso repetido en situaciones críticas de rendimiento. El problema es que internamente crea una instancia de Pattern para la expresión regular y la utiliza solo una vez, después de lo cual se vuelve elegible para la recolección de basura. Crear una instancia de Pattern es costoso porque requiere compilar la expresión regular en una máquina de estados finitos.
Para mejorar el rendimiento, compila explícitamente la expresión regular en una instancia de Pattern (que es inmutable) como parte de la inicialización de la clase, cáchela y reutiliza la misma instancia para cada invocación del método isRomanNumeral:

```java
// Reusing expensive object for improved performance
public class RomanNumerals {
   private static final Pattern ROMAN = Pattern.compile("^(?=.)M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
   static boolean isRomanNumeral(String s) {
      return ROMAN.matcher(s).matches();
   }
}
```
La versión mejorada de `isRomanNumeral` proporciona ganancias significativas de rendimiento si se invoca con frecuencia. En mi máquina, la versión original tarda 1.1 µs en una cadena de entrada de 8 caracteres, mientras que la versión mejorada tarda 0.17 µs, lo que es 6.5 veces más rápido. No solo mejora el rendimiento, sino que también se podría argumentar que mejora la claridad.

Crear un campo estático final para la instancia de `Pattern`, que de otro modo sería invisible, nos permite darle un nombre, que es mucho más legible que la expresión regular en sí.

Si la clase que contiene la versión mejorada del método `isRomanNumeral` se inicializa pero el método nunca se invoca, el campo `ROMAN` se inicializará innecesariamente. Sería posible eliminar la inicialización mediante la inicialización perezosa del campo (Elemento 83) la primera vez que se invoque el método `isRomanNumeral`, pero esto no se recomienda. Como suele ser el caso con la inicialización perezosa, complicaría la implementación sin una mejora de rendimiento mensurable (Elemento 67).

Cuando un objeto es inmutable, es obvio que se puede reutilizar de manera segura, pero hay otras situaciones donde es mucho menos obvio, incluso contraintuitivo. Considera el caso de los adaptadores [Gamma95], también conocidos como vistas. Un adaptador es un objeto que delega a un objeto de respaldo, proporcionando una interfaz alternativa. Dado que un adaptador no tiene estado más allá del objeto de respaldo, no hay necesidad de crear más de una instancia de un adaptador dado para un objeto dado.

Por ejemplo, el método `keySet` de la interfaz `Map` devuelve una vista de tipo `Set` del objeto `Map`, que consiste en todas las claves en el mapa. Ingenuamente, parecería que cada llamada a `keySet` tendría que crear una nueva instancia de `Set`, pero cada llamada a `keySet` en un objeto `Map` dado puede devolver la misma instancia de `Set`. Aunque la instancia de `Set` devuelta suele ser mutable, todos los objetos devueltos son funcionalmente idénticos: cuando uno de los objetos devueltos cambia, también lo hacen los demás, porque todos están respaldados por la misma instancia de `Map`. Aunque crear múltiples instancias del objeto de vista `keySet` generalmente no causa problemas, es innecesario y no ofrece beneficios.

Otra manera de crear objetos innecesarios es mediante el autoboxing, que permite al programador mezclar tipos primitivos y tipos primitivos en caja, realizando el envasado y desenvasado automáticamente según sea necesario. El autoboxing difumina pero no borra la distinción entre tipos primitivos y tipos primitivos en caja. Hay distinciones semánticas sutiles y diferencias de rendimiento no tan sutiles (Elemento 61). Considera el siguiente método, que calcula la suma de todos los valores enteros positivos. Para hacer esto, el programa tiene que usar aritmética `long` porque un `int` no es lo suficientemente grande para contener la suma de todos los valores enteros positivos.

```java
// Hideously slow! Can you spot the object creation?
private static long sum() {
  Long sum = 0L;
  for (long i = 0; i <= Integer.MAX_VALUE; i++)
    sum += i;
    return sum;
  }
}
```

El tipo Long es un objeto, y al usarlo en un contexto donde se espera un tipo primitivo long, se realiza una operación llamada autoboxing, que convierte automáticamente el tipo primitivo en su correspondiente tipo de objeto (Long en este caso). La creación repetida de objetos Long en cada iteración del bucle puede ser ineficiente, especialmente en bucles grandes.

Para mejorar la eficiencia, puedes utilizar el tipo primitivo long directamente en lugar de Long. Aquí está el código optimizado:

```java
private static long sum() {
  long sum = 0L;
  for (long i = 0; i <= Integer.MAX_VALUE; i++)
    sum += i;
  return sum;
}
```
Este programa obtiene la respuesta correcta, pero es mucho más lento de lo que debería debido a un error tipográfico de un solo carácter. La variable `sum` está declarada como un `Long` en lugar de un `long`, lo que significa que el programa construye alrededor de 2^31 instancias innecesarias de `Long` (aproximadamente una por cada vez que se suma el `long i` al `Long sum`). Cambiar la declaración de `sum` de `Long` a `long` reduce el tiempo de ejecución de 6.3 segundos a 0.59 segundos en mi máquina. La lección es clara: prefiera los primitivos a los primitivos en caja y tenga cuidado con el autoboxing no intencional.

Este elemento no debe malinterpretarse para implicar que la creación de objetos es cara y debe evitarse. Por el contrario, la creación y recuperación de objetos pequeños cuyos constructores hacen poco trabajo explícito son baratas, especialmente en las implementaciones modernas de JVM. Crear objetos adicionales para mejorar la claridad, simplicidad o potencia de un programa generalmente es algo positivo.

En cambio, evitar la creación de objetos mediante el mantenimiento de tu propio grupo de objetos es una mala idea a menos que los objetos en el grupo sean extremadamente pesados. El ejemplo clásico de un objeto que justifica un grupo de objetos es una conexión a la base de datos. El costo de establecer la conexión es lo suficientemente alto como para tener sentido reutilizar estos objetos. Sin embargo, en general, mantener tus propios grupos de objetos ensucia tu código, aumenta la huella de memoria y perjudica el rendimiento. Las implementaciones modernas de la JVM tienen recolectores de basura altamente optimizados que superan fácilmente a dichos grupos de objetos en objetos livianos.

El contrapunto a este ítem es el ítem 50 sobre copias defensivas. Este ítem dice: "No crees un nuevo objeto cuando deberías reutilizar uno existente", mientras que el ítem 50 dice: "No reutilices un objeto existente cuando deberías crear uno nuevo". Ten en cuenta que la penalización por reutilizar un objeto cuando se requiere copia defensiva es mucho mayor que la penalización por crear innecesariamente un objeto duplicado. No hacer copias defensivas cuando sea necesario puede conducir a errores insidiosos y agujeros de seguridad; crear objetos innecesariamente solo afecta al estilo y al rendimiento.
