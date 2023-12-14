## ITEM 4: FORZAR LA NO INSTANCIABILIDAD CON UN CONSTRUCTOR PRIVADO

En ocasiones, querrás escribir una clase que sea simplemente un conjunto de métodos y campos estáticos. Estas clases han adquirido mala reputación porque algunas personas abusan de ellas para evitar pensar en términos de objetos, pero tienen usos válidos. Pueden utilizarse para agrupar métodos relacionados en valores primitivos o matrices, al estilo de `java.lang.Math` o `java.util.Arrays`. También pueden ser usadas para agrupar métodos estáticos, incluyendo fábricas (Item 1), para objetos que implementan alguna interfaz, al estilo de `java.util.Collections`. (A partir de Java 8, también puedes colocar esos métodos en la interfaz, siempre y cuando tengas permiso para modificarla). Por último, estas clases pueden ser utilizadas para agrupar métodos en una clase final, ya que no se pueden colocar en una subclase. Estas clases de utilidad no fueron diseñadas para ser instanciadas: una instancia sería sin sentido. Sin embargo, en ausencia de constructores explícitos, el compilador proporciona un constructor público sin parámetros por defecto. Para un usuario, este constructor no se distingue de cualquier otro. No es raro ver clases inadvertidamente instanciables en APIs publicadas.

Intentar forzar la no instanciabilidad haciendo que una clase sea abstracta no funciona. La clase puede ser subclasificada y la subclase instanciada. Además, induce al usuario a pensar que la clase fue diseñada para la herencia (Item 19). Sin embargo, hay un sencillo modismo para garantizar la no instanciabilidad. Un constructor por defecto solo se genera si una clase no contiene constructores explícitos, por lo que una clase puede hacerse no instanciable incluyendo un constructor privado:


```java
// Noninstantiable utility class
public class UtilityClass {
    // Suppress default constructor for noninstantiability
    private UtilityClass() {
        throw new AssertionError();
    }
    ... // Remainder omitted
}
```

Debido a que el constructor explícito es privado, es inaccesible fuera de la clase.
Si bien no es estrictamente necesario, el AssertionError proporciona una garantía en caso de que el constructor se invoque accidentalmente desde dentro de la clase. Asegura que la clase nunca será instanciada bajo ninguna circunstancia. Este modismo es ligeramente contra intuitivo porque el constructor se proporciona expresamente para que no pueda ser invocado. Por lo tanto, es recomendable incluir un comentario, como se mostró anteriormente.
Como efecto secundario, este modismo también evita que la clase sea subclasificada. Todos los constructores deben invocar explícita o implícitamente un constructor de la superclase, y una subclase no tendría ningún constructor de superclase accesible para invocar.
