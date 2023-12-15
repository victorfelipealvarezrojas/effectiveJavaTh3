
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