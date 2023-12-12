Las fábricas estáticas y los constructores comparten una limitación: no escalan bien a un
gran número de parámetros opcionales. Considera el caso de una clase que representa la
etiqueta de Información Nutricional que aparece en los alimentos envasados. Estas etiquetas
tienen algunos campos obligatorios: tamaño de la porción, porciones por envase y calorías por
porción, y más de veinte campos opcionales: grasa total, grasa saturada, grasa trans, colesterol,
sodio, etc. La mayoría de los productos solo tienen valores distintos de cero para unos pocos
de estos campos opcionales.

¿Qué tipo de constructores o fábricas estáticas deberías escribir para tal clase?
Tradicionalmente, los programadores han utilizado el patrón de constructor telescópico, en
el que proporcionas un constructor solo con los parámetros obligatorios, otro con un único
parámetro opcional, un tercero con dos parámetros opcionales, y así sucesivamente, culminando
en un constructor con todos los parámetros opcionales. Así es cómo se ve en la práctica. Por
cuestiones de brevedad, solo se muestran cuatro campos opcionales:

```java
// Patrón de constructor telescópico - ¡no escala bien!
public class NutritionFacts {
    private final int servingSize; // (mL) requerido
    private final int servings; // (por envase) requerido
    private final int calories; // (por porción) opcional
    private final int fat; // (g/porción) opcional
    private final int sodium; // (mg/porción) opcional
    private final int carbohydrate; // (g/porción) opcional

    public NutritionFacts(int servingSize, int servings) {
        this(servingSize, servings, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories) {
        this(servingSize, servings, calories, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat) {
        this(servingSize, servings, calories, fat, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium) {
        this(servingSize, servings, calories, fat, sodium, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium, int carbohydrate) {
        this.servingSize = servingSize;
        this.servings = servings;
        this.calories = calories;
        this.fat = fat;
        this.sodium = sodium;
        this.carbohydrate = carbohydrate;
    }
}
```
Cuando quieres crear una instancia, usas el constructor con la lista de parámetros más corta
que contenga todos los parámetros que deseas establecer:

```java
NutritionFacts cocaCola = new NutritionFacts(240, 8, 100, 0, 35, 27);
```
Típicamente, esta invocación del constructor requerirá muchos parámetros que no deseas 
establecer, pero te ves obligado a pasar un valor para ellos de todos modos. En este caso, 
pasamos un valor de 0 para la grasa. Con "solo" seis parámetros esto puede no parecer tan malo, 
pero rápidamente se complica a medida que aumenta el número de parámetros.

En resumen, el patrón de constructor telescópico funciona, pero es difícil escribir código de 
cliente cuando hay muchos parámetros, y aún más difícil leerlo. El lector se queda preguntándose 
qué significan todos esos valores y debe contar cuidadosamente los parámetros para averiguarlo. 
Largas secuencias de parámetros del mismo tipo pueden causar errores sutiles. Si el cliente 
invierte accidentalmente dos de estos parámetros, el compilador no se quejará, pero el programa 
se comportará mal en tiempo de ejecución (Elemento 51).

Una segunda alternativa cuando te enfrentas a muchos parámetros opcionales en un 
constructor es el patrón JavaBeans, en el que llamas a un constructor sin parámetros para crear 
el objeto y luego llamas a métodos setter para establecer cada parámetro obligatorio y cada 
parámetro opcional de interés:
```java
// Patrón JavaBeans - permite inconsistencia, exige mutabilidad
public class NutritionFacts {
    // Parámetros inicializados a valores predeterminados (si los hay)
    private int servingSize = -1; // Obligatorio; sin valor predeterminado
    private int servings = -1; // Obligatorio; sin valor predeterminado
    private int calories = 0;
    private int fat = 0;
    private int sodium = 0;
    private int carbohydrate = 0;

    public NutritionFacts() { }

    // Setters
    public void setServingSize(int val) { servingSize = val; }
    public void setServings(int val) { servings = val; }
    public void setCalories(int val) { calories = val; }
    public void setFat(int val) { fat = val; }
    public void setSodium(int val) { sodium = val; }
    public void setCarbohydrate(int val) { carbohydrate = val; }
}
```
Este patrón no tiene ninguna de las desventajas del patrón de constructor telescópico.
Es fácil, aunque un poco verboso, crear instancias y fácil leer el código resultante:

```java
NutritionFacts cocaCola = new NutritionFacts();
cocaCola.setServingSize(240);
cocaCola.setServings(8);
cocaCola.setCalories(100);
cocaCola.setSodium(35);
cocaCola.setCarbohydrate(27);
```

Lamentablemente, el patrón JavaBeans tiene serias desventajas propias.
Debido a que la construcción se divide en múltiples llamadas, un JavaBean puede estar en un
estado inconsistente durante su construcción. La clase no tiene la opción de imponer
consistencia simplemente verificando la validez de los parámetros del constructor. Intentar
usar un objeto cuando está en un estado inconsistente puede causar fallos que están muy
alejados del código que contiene el error y, por lo tanto, son difíciles de depurar. Una
desventaja relacionada es que el patrón JavaBeans impide la posibilidad de hacer una clase
inmutable (Elemento 17) y requiere un esfuerzo adicional por parte del programador para
garantizar la seguridad en el manejo de hilos.

Es posible reducir estas desventajas mediante el "congelamiento" manual del objeto cuando su
construcción está completa y no permitiendo su uso hasta que esté congelado, pero esta
variante es engorrosa y raramente se utiliza en la práctica. Además, puede causar errores en
tiempo de ejecución porque el compilador no puede asegurar que el programador llame al
método de congelamiento en un objeto antes de usarlo.

Afortunadamente, hay una tercera alternativa que combina la seguridad del patrón de
constructor telescópico con la legibilidad del patrón JavaBeans. Es una forma del patrón
Builder [Gamma95]. En lugar de hacer el objeto deseado directamente, el cliente llama a un
constructor (o fábrica estática) con todos los parámetros requeridos y obtiene un objeto builder.
Luego, el cliente llama a métodos similares a setters en el objeto builder para establecer cada
parámetro opcional de interés. Finalmente, el cliente llama a un método build sin parámetros
para generar el objeto, que típicamente es inmutable. El builder es típicamente una clase
miembro estática (Elemento 24) de la clase que construye. Así es como se ve en la práctica:

```java
// Patrón Builder
public class NutritionFacts {
    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int sodium;
    private final int carbohydrate;

    public static class Builder {
        // Parámetros obligatorios
        private final int servingSize;
        private final int servings;

        // Parámetros opcionales - inicializados a valores predeterminados
        private int calories = 0;
        private int fat = 0;
        private int sodium = 0;
        private int carbohydrate = 0;

        public Builder(int servingSize, int servings) {
            this.servingSize = servingSize;
            this.servings = servings;
        }

        public Builder calories(int val) { calories = val; return this; }
        public Builder fat(int val) { fat = val; return this; }
        public Builder sodium(int val) { sodium = val; return this; }
        public Builder carbohydrate(int val) { carbohydrate = val; return this; }

        public NutritionFacts build() {
            return new NutritionFacts(this);
        }
    }

    private NutritionFacts(Builder builder) {
        servingSize = builder.servingSize;
        servings = builder.servings;
        calories = builder.calories;
        fat = builder.fat;
        sodium = builder.sodium;
        carbohydrate = builder.carbohydrate;
    }
}
```

La clase `NutritionFacts` es inmutable, y todos los valores predeterminados de los parámetros 
están en un solo lugar. Los métodos setter del builder devuelven el propio builder para que 
las invocaciones se puedan encadenar, lo que resulta en una API fluida. Así es como se ve el 
código del cliente:

```java
NutritionFacts cocaCola = new NutritionFacts.Builder(240, 8)
    .calories(100).sodium(35).carbohydrate(27).build();
```

Este código de cliente es fácil de escribir y, lo que es más importante, fácil de leer. El patrón
Builder simula parámetros opcionales nombrados como se encuentran en Python y Scala.

Las comprobaciones de validez se omitieron por brevedad. Para detectar parámetros inválidos lo
antes posible, verifica la validez de los parámetros en el constructor del builder y en sus métodos.
Comprueba las invariantes que involucran múltiples parámetros en el constructor invocado por el
método build. Para asegurar estas invariantes contra ataques, realiza las comprobaciones en los
campos del objeto después de copiar los parámetros del builder (Elemento 50). Si una comprobación
falla, lanza una IllegalArgumentException (Elemento 72) cuyo mensaje detallado indica qué
parámetros son inválidos (Elemento 75).

El patrón Builder es adecuado para jerarquías de clases. Usa una jerarquía paralela de builders,
cada uno anidado en la clase correspondiente. Las clases abstractas tienen builders abstractos; las
clases concretas tienen builders concretos. Por ejemplo, considera una clase abstracta en la raíz de
una jerarquía que representa varios tipos de pizza

```java
// Patrón Builder para jerarquías de clases
public abstract class Pizza {
    public enum Topping { HAM, MUSHROOM, ONION, PEPPER, SAUSAGE }
    final Set<Topping> toppings;

    abstract static class Builder<T extends Builder<T>> {
        EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);

        public T addTopping(Topping topping) {
            toppings.add(Objects.requireNonNull(topping));
            return self();
        }

        abstract Pizza build();

        // Las subclases deben sobrescribir este método para devolver "this"
        protected abstract T self();
    }

    Pizza(Builder<?> builder) {
        toppings = builder.toppings.clone(); // Ver Elemento 50
    }
}
```
Nótese que `Pizza.Builder` es un tipo genérico con un parámetro de tipo recursivo 
(Elemento 30). Esto, junto con el método `self` abstracto, permite que el encadenamiento de 
métodos funcione correctamente en subclases, sin la necesidad de casts. Este método para 
compensar el hecho de que Java carece de un tipo self se conoce como el idiomático self-type 
simulado.


Aquí hay dos subclases concretas de `Pizza`, una de las cuales representa una pizza 
estilo Nueva York estándar, y la otra un calzone. La primera tiene un parámetro de tamaño 
requerido, mientras que la segunda te permite especificar si la salsa debe estar dentro o fuera
         
```java
public class NyPizza extends Pizza {
    public enum Size { SMALL, MEDIUM, LARGE }
    private final Size size;

    public static class Builder extends Pizza.Builder<Builder> {
        private final Size size;

        public Builder(Size size) {
            this.size = Objects.requireNonNull(size);
        }

        @Override 
        public NyPizza build() {
            return new NyPizza(this);
        }

        @Override 
        protected Builder self() { 
            return this; 
        }
    }

    private NyPizza(Builder builder) {
        super(builder);
        size = builder.size;
    }
}

public class Calzone extends Pizza {
    private final boolean sauceInside;

    public static class Builder extends Pizza.Builder<Builder> {
        private boolean sauceInside = false; // Por defecto

        public Builder sauceInside() {
            sauceInside = true;
            return this;
        }

        @Override 
        public Calzone build() {
            return new Calzone(this);
        }

        @Override 
        protected Builder self() { 
            return this; 
        }
    }

    private Calzone(Builder builder) {
        super(builder);
        sauceInside = builder.sauceInside;
    }
}
```
Nótese que el método `build` en el builder de cada subclase está declarado para devolver la 
subclase correcta: el método `build` de `NyPizza.Builder` devuelve `NyPizza`, mientras que 
el de `Calzone.Builder` devuelve `Calzone`. Esta técnica, en la que un método de subclase 
se declara para devolver un subtipo del tipo de retorno declarado en la superclase, se conoce 
como tipificación de retorno covariante. Permite a los clientes usar estos builders sin la 
necesidad de realizar casting.

El código del cliente para estos "builders jerárquicos" es esencialmente idéntico al código 
para el simple builder de `NutritionFacts`. El siguiente código de ejemplo del cliente asume 
importaciones estáticas de constantes enum por brevedad:

```java
NyPizza pizza = new NyPizza.Builder(SMALL)
    .addTopping(SAUSAGE).addTopping(ONION).build();

Calzone calzone = new Calzone.Builder()
    .addTopping(HAM).sauceInside().build();
```

Una ventaja menor de los builders sobre los constructores es que los builders pueden tener 
múltiples parámetros varargs, ya que cada parámetro se especifica en su propio método. 
Alternativamente, los builders pueden agregar los parámetros pasados en múltiples llamadas a 
un método en un solo campo, como se demostró en el método `addTopping` anteriormente.

El patrón Builder es bastante flexible. Un solo builder puede utilizarse repetidamente 
para construir múltiples objetos. Los parámetros del builder pueden ajustarse entre 
invocaciones del método `build` para variar los objetos que se crean. Un builder puede 
rellenar automáticamente algunos campos al crear el objeto, como un número de serie que 
aumenta cada vez que se crea un objeto.

El patrón Builder también tiene desventajas. Para crear un objeto, primero debes crear su 
builder. Aunque el costo de crear este builder probablemente no sea notable en la práctica, 
podría ser un problema en situaciones críticas de rendimiento. Además, el patrón Builder es 
más verboso que el patrón de constructor telescópico, por lo que solo debe usarse si hay 
suficientes parámetros para que valga la pena, digamos cuatro o más. Pero ten en cuenta que 
puedes querer agregar más parámetros en el futuro. Pero si comienzas con constructores o 
fábricas estáticas y cambias a un builder cuando la clase evoluciona al punto en que el número 
de parámetros se sale de control, los constructores o fábricas estáticas obsoletos resaltarán 
como un pulgar dolorido. Por lo tanto, a menudo es mejor comenzar con un builder desde el 
principio.

En resumen, el patrón Builder es una buena elección al diseñar clases cuyos constructores 
o fábricas estáticas tendrían más que un puñado de parámetros, especialmente si muchos de 
los parámetros son opcionales o del mismo tipo. El código del cliente es mucho más fácil de 
leer y escribir con builders que con constructores telescópicos, y los builders son mucho más 
seguros que los JavaBeans.
