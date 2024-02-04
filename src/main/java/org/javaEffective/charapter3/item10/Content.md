Cumple el contrato general al sobrescribir equals

Sobrescribir el método equals parece simple, pero hay muchas formas de hacerlo mal, y las consecuencias pueden ser graves. La forma más fácil de evitar problemas es no sobrescribir el método equals, en cuyo caso cada instancia de la clase es igual solo a sí misma. Esto es lo correcto si se cumple alguna de las siguientes condiciones:
- Cada instancia de la clase es inherentemente única. Esto es cierto para clases como Thread que representan entidades activas en lugar de valores. La implementación de equals proporcionada por Object tiene exactamente el comportamiento adecuado para estas clases.
- No hay necesidad de que la clase proporcione una prueba de "igualdad lógica". Por ejemplo, java.util.regex.Pattern podría haber sobrescrito equals para verificar si dos instancias de Pattern representaban exactamente la misma expresión regular, pero los diseñadores no pensaron que los clientes necesitarían o querrían esta funcionalidad. En estas circunstancias, la implementación de equals heredada de Object es ideal.
- Una superclase ya ha sobrescrito equals, y el comportamiento de la superclase es apropiado para esta clase. Por ejemplo, la mayoría de las implementaciones de Set heredan su implementación de equals de AbstractSet, las implementaciones de List de AbstractList y las implementaciones de Map de AbstractMap.
- La clase es privada o de paquete, y estás seguro de que su método equals nunca será invocado. Si eres extremadamente cauteloso, puedes sobrescribir el método equals para asegurarte de que no se invoque accidentalmente.

```java
@Override public boolean equals(Object o) {
    throw new AssertionError(); // Method is never called
}
```
Entonces, ¿cuándo es apropiado sobrescribir equals? Lo es cuando una clase tiene una noción de igualdad lógica que difiere de la mera identidad de objeto y una superclase aún no ha sobrescrito equals. Este es generalmente el caso para las clases de valor. Una clase de valor es simplemente una clase que representa un valor, como Integer o String. Un programador que compara referencias a objetos de valor usando el método equals espera averiguar si son lógicamente equivalentes, no si se refieren al mismo objeto. No solo es necesario sobrescribir el método equals para satisfacer las expectativas del programador, sino que también permite que las instancias sirvan como claves de mapa o elementos de conjunto con un comportamiento predecible y deseable.

Un tipo de clase de valor que no requiere que se sobrescriba el método equals es una clase que utiliza control de instancias (Elemento 1) para asegurar que exista como máximo un objeto con cada valor. Los tipos de enumeración (Elemento 34) entran en esta categoría. Para estas clases, la igualdad lógica es la misma que la identidad de objeto, por lo que el método equals de Object funciona como un método equals lógico.

Cuando sobrescribes el método equals, debes adherirte a su contrato general. Aquí está el contrato, de la especificación de Object:

El método equals implementa una relación de equivalencia. Tiene estas propiedades:

1. Reflexiva: Para cualquier valor de referencia no nulo x, x.equals(x) debe devolver true.
2. Simétrica: Para cualquier valor de referencia no nulo x e y, x.equals(y) debe devolver true si y solo si y.equals(x) devuelve true.
3. Transitiva: Para cualquier valor de referencia no nulo x, y, z, si x.equals(y) devuelve true y y.equals(z) devuelve true, entonces x.equals(z) debe devolver true.
4. Consistente: Para cualquier valor de referencia no nulo x e y, múltiples invocaciones de x.equals(y) deben devolver consistentemente true o consistentemente false, siempre que no se modifique ninguna información utilizada en comparaciones de equals.
5. Para cualquier valor de referencia no nulo x, x.equals(null) debe devolver false.

A menos que tengas afinidad por las matemáticas, esto podría parecer un poco intimidante, ¡pero no lo ignores! Si lo violas, es posible que te encuentres con que tu programa se comporta de forma errática o se bloquea, y puede ser muy difícil identificar la fuente del fallo. Parafraseando a John Donne, ninguna clase es una isla. Las instancias de una clase se pasan con frecuencia a otra. Muchas clases, incluidas todas las clases de colecciones, dependen de que los objetos que se les pasan obedezcan el contrato de equals.

Ahora que eres consciente de los peligros de violar el contrato de equals, repasemos el contrato en detalle. La buena noticia es que, a pesar de las apariencias, realmente no es muy complicado. Una vez que lo entiendes, no es difícil adherirse a él.

Entonces, ¿qué es una relación de equivalencia? En términos generales, es un operador que divide un conjunto de elementos en subconjuntos cuyos elementos se consideran iguales entre sí. Estos subconjuntos se conocen como clases de equivalencia. Para que un método equals sea útil, todos los elementos en cada clase de equivalencia deben ser intercambiables desde la perspectiva del usuario.

Ahora examinemos los cinco requisitos uno por uno:

Reflexividad: El primer requisito simplemente dice que un objeto debe ser igual a sí mismo. Es difícil imaginar violar este requisito accidentalmente. Si lo violaras y luego agregaras una instancia de tu clase a una colección, el método contains bien podría decir que la colección no contenía la instancia que acabas de agregar.

Simetría: El segundo requisito dice que dos objetos deben estar de acuerdo en si son iguales. A diferencia del primer requisito, no es difícil imaginar violar este requisito accidentalmente. Por ejemplo, considera la siguiente clase, que implementa una cadena que ignora las diferencias de mayúsculas y minúsculas. El caso de la cadena se preserva por toString pero se ignora en las comparaciones de equals:

```java
// Broken - violates symmetry!
public final class CaseInsensitiveString {
    private final String s;

    public CaseInsensitiveString(String s) {
        this.s = Objects.requireNonNull(s);
    }

    // Broken - violates symmetry!
    @Override
    public boolean equals(Object o) {
        if (o instanceof CaseInsensitiveString)
            return s.equalsIgnoreCase(((CaseInsensitiveString) o).s);
        if (o instanceof String) // One-way interoperability!
            return s.equalsIgnoreCase((String) o);
        return false;
    }

    // Remainder omitted
}
```

El método equals bien intencionado en esta clase intenta ingenuamente interoperar con cadenas ordinarias. Supongamos que tenemos una cadena insensible a mayúsculas y minúsculas y una cadena ordinaria.

```java
CaseInsensitiveString cis = new CaseInsensitiveString("Polish");
String s = "polish";
```


Como era de esperar, cis.equals(s) devuelve true. El problema es que mientras que el método equals en CaseInsensitiveString conoce las cadenas ordinarias, el método equals en String no tiene conocimiento de las cadenas insensibles a mayúsculas y minúsculas. Por lo tanto, s.equals(cis) devuelve false, una clara violación de la simetría. Supongamos que colocas una cadena insensible a mayúsculas y minúsculas en una colección: 

```java
List<CaseInsensitiveString> list = new ArrayList<>();
list.add(cis);
```
