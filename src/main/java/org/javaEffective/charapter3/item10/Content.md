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
¿Qué devuelve list.contains(s) en este punto? ¿Quién sabe? En la implementación actual de OpenJDK, podría devolver false, pero eso es solo un artefacto de la implementación. En otra implementación, podría igualmente devolver true o lanzar una excepción en tiempo de ejecución. Una vez que has violado el contrato de equals, simplemente no sabes cómo se comportarán otros objetos cuando se enfrenten a tu objeto.

Para eliminar el problema, simplemente elimina el intento mal concebido de interoperar con String del método equals en CaseInsensitiveString. Al hacerlo, puedes refactorizar el método en una sola declaración de retorno:

```java
@Override
public boolean equals(Object o) {
    return o instanceof CaseInsensitiveString &&
           ((CaseInsensitiveString) o).s.equalsIgnoreCase(s);
}

```
Transitividad — El tercer requisito del contrato de equals dice que si un objeto es igual a un segundo y el segundo objeto es igual a un tercero, entonces el primer objeto debe ser igual al tercero. Nuevamente, no es difícil imaginar violar este requisito involuntariamente. Consideremos el caso de una subclase que agrega un nuevo componente de valor a su superclase. En otras palabras, la subclase agrega una pieza de información que afecta las comparaciones de igualdad. Comencemos con una simple clase de punto inmutable bidimensional de enteros:

```java
public class Point {
    private final int x;
    private final int y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) {
            return false;
        }
        Point p = (Point) o;
        return p.x == x && p.y == y;
    }

// ... resto del código omitido
```

Supongamos que deseas extender esta clase, agregando la noción de color a un Point:

```java
public class ColorPoint extends Point {
    private final Color color;
    
    public ColorPoint(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }
    // Resto del código omitido
}
```
¿Cómo debería verse el método equals? Si lo omites por completo, la implementación se hereda de Point y la información de color se ignora en las comparaciones de igualdad. Aunque esto no viola el contrato de equals, claramente es inaceptable. Supongamos que escribes un método equals que devuelve true solo si su argumento es otro punto de color con la misma posición y color:
```java
@Override
public boolean equals(Object o) {
    if (!(o instanceof ColorPoint))
        return false;
    return super.equals(o) && ((ColorPoint) o).color == color;
}
```

Este método primero verifica si el objeto pasado es una instancia de ColorPoint. Luego, llama al método equals de la superclase (Point) para verificar la igualdad de posición y compara los colores directamente utilizando el operador ==.

El problema aquí es que, si bien la comparación de posición se delega correctamente a la superclase, la comparación de color se realiza utilizando el operador ==, que compara referencias de objetos en lugar de sus valores. Esto puede conducir a resultados incorrectos, especialmente si se están utilizando objetos Color con instancias diferentes pero valores iguales. Además, este método no garantiza la simetría de la igualdad, lo que puede provocar resultados inesperados en comparaciones entre objetos ColorPoint.

El problema con este método es que podrías obtener resultados diferentes al comparar un punto con un punto de color y viceversa. La primera comparación ignora el color, mientras que la segunda comparación siempre devuelve falso porque el tipo del argumento es incorrecto. Para ilustrar esto con un ejemplo, creemos un punto y un punto de color:

```java
Point p = new Point(1, 2);
ColorPoint cp = new ColorPoint(1, 2, Color.RED);
```

Entonces, p.equals(cp) devuelve true, mientras que cp.equals(p) devuelve false. Podrías intentar solucionar el problema haciendo que ColorPoint.equals ignore el color al hacer "comparaciones mixtas":

```java
// Broken - violates transitivity!
@Override
public boolean equals(Object o) {
    if (!(o instanceof Point))
        return false;
    
    // Si o es un Point normal, realiza una comparación sin considerar el color
    if (!(o instanceof ColorPoint))
        return o.equals(this);
    
    // o es un ColorPoint; realiza una comparación completa
    return super.equals(o) && ((ColorPoint) o).color == color;
}
```

Este enfoque sí proporciona simetría, pero a expensas de la transitividad.
Este enfoque sí proporciona simetría al considerar equals entre Point y ColorPoint, ya que ahora p.equals(cp) y cp.equals(p) devolverán el mismo resultado (true o false) según la posición y la igualdad de color. Sin embargo, este enfoque sacrifica la transitividad, como se mencionó anteriormente, ya que la relación de igualdad entre Point y ColorPoint no se mantiene consistente en todas las situaciones posibles. Por lo tanto, aunque se logra la simetría, se viola la transitividad, lo que no cumple con los requisitos del contrato de equals.

```java
ColorPoint p1 = new ColorPoint(1, 2, Color.RED); // Un ColorPoint con posición (1, 2) y color rojo
Point p2 = new Point(1, 2); // Un Point con posición (1, 2)
ColorPoint p3 = new ColorPoint(1, 2, Color.BLUE); // Un ColorPoint con posición (1, 2) y color azul

```

Ahora, p1.equals(p2) y p2.equals(p3) devuelven true, mientras que p1.equals(p3) devuelve false, una clara violación de la transitividad. Las dos primeras comparaciones son "ciegas al color", mientras que la tercera tiene en cuenta el color.

Además, este enfoque puede causar una recursión infinita: Supongamos que hay dos subclases de Point, digamos ColorPoint y SmellPoint, cada una con este tipo de método equals. Entonces, una llamada a myColorPoint.equals(mySmellPoint) lanzará un StackOverflowError.

Entonces, ¿cuál es la solución? Resulta que este es un problema fundamental de relaciones de equivalencia en lenguajes orientados a objetos. No hay forma de extender una clase instanciable y agregar un componente de valor mientras se preserva el contrato de equals, a menos que estés dispuesto a renunciar a los beneficios de la abstracción orientada a objetos. Puede que escuches que puedes extender una clase instanciable y agregar un componente de valor mientras se preserva el contrato de equals usando una prueba getClass en lugar de la prueba instanceof en el método equals.

Este método equals viola el principio de sustitución de Liskov. Aquí está el método proporcionado:

```java
// Broken - violates Liskov substitution principle (page 43)
@Override
public boolean equals(Object o) {
    if (o == null || o.getClass() != getClass())
        return false;
    Point p = (Point) o;
    return p.x == x && p.y == y;
}
```
Este método tiene el efecto de igualar objetos solo si tienen la misma clase de implementación. Esto puede no parecer tan malo, pero las consecuencias son inaceptables: una instancia de una subclase de Point sigue siendo un Point, y aún debe funcionar como tal, ¡pero falla en hacerlo si tomas este enfoque!

Supongamos que queremos escribir un método para determinar si un punto está en el círculo unitario. Aquí hay una forma en que podríamos hacerlo:

```java
// Inicializa unitCircle para contener todos los Points en el círculo unitario
private static final Set<Point> unitCircle = Set.of(
    new Point(1, 0), new Point(0, 1),
    new Point(-1, 0), new Point(0, -1));

public static boolean onUnitCircle(Point p) {
    return unitCircle.contains(p);
}

```

Si bien esta puede no ser la forma más rápida de implementar la funcionalidad, funciona bien. Supongamos que extendemos Point de alguna manera trivial que no agregue un componente de valor, por ejemplo, haciendo que su constructor lleve un registro de cuántas instancias se han creado:

```java
public class CounterPoint extends Point {
    private static final AtomicInteger counter = new AtomicInteger();

    public CounterPoint(int x, int y) {
        super(x, y);
        counter.incrementAndGet();
    }

    public static int numberCreated() {
        return counter.get();
    }
}
```

Este nuevo tipo de punto, CounterPoint, hereda de Point pero agrega funcionalidad adicional para rastrear el número de instancias creadas. Sin embargo, debido a la implementación defectuosa del método equals en Point, CounterPoint no se comportará correctamente cuando se utilice en contextos donde se espera un Point, como en el método onUnitCircle. Esto viola el principio de sustitución de Liskov.

El principio de sustitución de Liskov establece que cualquier propiedad importante de un tipo también debe cumplirse para todos sus subtipos, de modo que cualquier método escrito para el tipo también funcione igualmente bien en sus subtipos [Liskov87]. Esta es la declaración formal de nuestra afirmación anterior de que una subclase de Point (como CounterPoint) sigue siendo un Point y debe actuar como tal. Pero supongamos que pasamos un CounterPoint al método onUnitCircle. Si la clase Point utiliza un método equals basado en getClass, el método onUnitCircle devolverá false independientemente de las coordenadas x e y de la instancia de CounterPoint. Esto se debe a que la mayoría de las colecciones, incluido el HashSet utilizado por el método onUnitCircle, usan el método equals para probar la contención, y ninguna instancia de CounterPoint es igual a ninguna instancia de Point. Sin embargo, si utilizas un método equals basado en instanceof adecuado en Point, el mismo método onUnitCircle funciona bien cuando se le presenta una instancia de CounterPoint.

Si bien no hay una forma satisfactoria de extender una clase instanciable y agregar un componente de valor, hay una solución elegante: sigue el consejo del Elemento 18, "Prefiere la composición sobre la herencia". En lugar de que ColorPoint extienda Point, dale a ColorPoint un campo privado de tipo Point y un método de vista público (Elemento 6) que devuelva el punto en la misma posición que este punto de color.

```java
import java.awt.Color;
import java.util.Objects;

public class ColorPoint {
    private final Point point;
    private final Color color;
    
    public ColorPoint(int x, int y, Color color) {
        this.point = new Point(x, y);
        this.color = Objects.requireNonNull(color);
    }
    
    public Point asPoint() {
        return point;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ColorPoint)) return false;
        ColorPoint cp = (ColorPoint) o;
        return cp.point.equals(point) && cp.color.equals(color);
    }
    
    // Remainder omitted
}
```
Este es un excelente ejemplo de cómo utilizar la composición para agregar un componente de valor sin violar el contrato de igualdad. La clase ColorPoint contiene un campo de tipo Point y un campo para el color. Al hacerlo, ColorPoint puede representar un punto en un plano con un color asociado.

El método asPoint() proporciona una vista del punto subyacente sin exponer directamente el campo point, lo que ayuda a encapsular la implementación y a prevenir modificaciones no deseadas.

El método equals compara tanto el punto como el color de dos ColorPoint, asegurando que dos instancias de ColorPoint sean iguales si tienen el mismo punto y el mismo color.

Este enfoque de composición permite agregar funcionalidad adicional (en este caso, el color) a un tipo existente (en este caso, Point) sin heredar directamente de él y sin violar el contrato de igualdad. ¡Es una forma elegante de extender la funcionalidad de las clases existentes!

Hay algunas clases en las bibliotecas de la plataforma Java que extienden una clase instanciable y agregan un componente de valor. Por ejemplo, java.sql.Timestamp extiende java.util.Date y agrega un campo de nanosegundos. La implementación de equals para Timestamp viola la simetría y puede causar un comportamiento errático si objetos Timestamp y Date se utilizan en la misma colección o se mezclan de alguna otra manera. La clase Timestamp tiene una advertencia que alerta a los programadores sobre la mezcla de fechas y marcas de tiempo. Aunque no tendrás problemas mientras los mantengas separados, no hay nada que te impida mezclarlos, y los errores resultantes pueden ser difíciles de depurar. Este comportamiento de la clase Timestamp fue un error y no debe ser emulado.

Es importante destacar que puedes agregar un componente de valor a una subclase de una clase abstracta sin violar el contrato de equals. Esto es relevante para el tipo de jerarquías de clases que se obtienen al seguir el consejo en el Elemento 23, "Prefiere jerarquías de clases a clases etiquetadas". Por ejemplo, podrías tener una clase abstracta Shape sin componentes de valor, una subclase Circle que agrega un campo de radio, y una subclase Rectangle que agrega campos de longitud y ancho. Los problemas del tipo mostrado anteriormente no ocurrirán siempre que sea imposible crear una instancia de la superclase directamente.