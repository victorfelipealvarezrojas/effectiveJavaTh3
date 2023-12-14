Muchas clases dependen de uno o más recursos subyacentes. Por ejemplo, un corrector ortográfico depende de un diccionario. No es raro ver que estas clases se implementen como clases de utilidad estáticas (Item 4):


```java
// Uso inapropiado de utilidad estática: inflexible y no testeable
public class SpellChecker {
private static final Lexicon dictionary = ...;

    private SpellChecker() {} // No instanciable
    
    public static boolean isValid(String word) { ... }
    
    public static List<String> suggestions(String typo) { ... }
}
```

Igualmente, no es raro ver que se implementen como singletons (Item 3):

```java
// Uso inapropiado de singleton: inflexible y no testeable
public class SpellChecker {
    private final Lexicon dictionary = ...;
    
    private SpellChecker(...) {}
    
    public static final SpellChecker INSTANCE = new SpellChecker(...);
    
    public boolean isValid(String word) { ... }
    
    public List<String> suggestions(String typo) { ... }
}
```

Ninguno de estos enfoques es satisfactorio, ya que asumen que hay solo un diccionario que vale la pena usar. En la práctica, cada idioma tiene su propio diccionario, y se utilizan diccionarios especiales para vocabularios específicos. Además, puede ser deseable utilizar un diccionario especial para pruebas. Es ingenuo pensar que un solo diccionario será suficiente para siempre.

Podrías intentar que SpellChecker admita múltiples diccionarios haciendo que el campo del diccionario no sea final y agregando un método para cambiar el diccionario en un corrector ortográfico existente, pero esto sería incómodo, propenso a errores e ineficaz en un entorno concurrente. Las clases de utilidad estática y los singletons no son apropiados para clases cuyo comportamiento está parametrizado por un recurso subyacente.

Lo que se requiere es la capacidad de admitir múltiples instancias de la clase (en nuestro ejemplo, SpellChecker), cada una de las cuales utiliza el recurso deseado por el cliente (en nuestro ejemplo, el diccionario). Un patrón simple que satisface este requisito es pasar el recurso al constructor al crear una nueva instancia. Esto es una forma de inyección de dependencias: el diccionario es una dependencia del corrector ortográfico y se inyecta en el corrector ortográfico cuando se crea.
```java
// La inyección de dependencias proporciona flexibilidad y testeabilidad
public class SpellChecker {
    private final Lexicon dictionary;

    public SpellChecker(Lexicon dictionary) {
        this.dictionary = Objects.requireNonNull(dictionary);
    }

    public boolean isValid(String word) { ... }

    public List<String> suggestions(String typo) { ... }
}
```
El patrón de inyección de dependencias es tan simple que muchos programadores lo utilizan durante años sin saber que tiene un nombre. Mientras que nuestro ejemplo de corrector ortográfico tenía solo un recurso (el diccionario), la inyección de dependencias funciona con un número arbitrario de recursos y gráficos de dependencias arbitrarios. Preserva la inmutabilidad (Item 17), por lo que varios clientes pueden compartir objetos dependientes (si los clientes desean los mismos recursos subyacentes). La inyección de dependencias es igualmente aplicable a constructores, fábricas estáticas (Item 1) y constructores (Item 2).

Una variante útil del patrón es pasar un factory de recurso al constructor. Un factory es un objeto que se puede llamar repetidamente para crear instancias de un tipo. Tales factories encarnan el patrón Factory Method [Gamma95]. La interfaz Supplier<T>, introducida en Java 8, es perfecta para representar factories. Los métodos que toman un Supplier<T> como entrada deben limitar típicamente el parámetro de tipo del factory utilizando un tipo de comodín delimitado (Item 31) para permitir que el cliente pase un factory que cree cualquier subtipo de un tipo especificado. Por ejemplo, aquí hay un método que crea un mosaico utilizando un factory proporcionado por el cliente para producir cada azulejo:

```java
Mosaic create(Supplier<? extends Tile> tileFactory) { ... }
```
Aunque la inyección de dependencias mejora significativamente la flexibilidad y la testeabilidad, puede ensuciar proyectos grandes, que típicamente contienen miles de dependencias. Esta confusión puede eliminarse casi por completo utilizando un framework de inyección de dependencias, como Dagger [Dagger], Guice [Guice] o Spring [Spring]. El uso de estos frameworks está más allá del alcance de este libro, pero cabe destacar que las APIs diseñadas para la inyección de dependencias manual se adaptan fácilmente para su uso con estos frameworks.

En resumen, no uses un singleton o una clase de utilidad estática para implementar una clase que dependa de uno o más recursos subyacentes cuyo comportamiento afecte al de la clase, y no hagas que la clase cree estos recursos directamente. En su lugar, pasa los recursos o factories para crearlos al constructor (o fábrica estática o constructor). Esta práctica, conocida como inyección de dependencias, mejorará significativamente la flexibilidad, reutilización y testeabilidad de una clase.
