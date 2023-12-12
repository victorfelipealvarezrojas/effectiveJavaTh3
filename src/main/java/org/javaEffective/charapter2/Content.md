Este capítulo trata sobre la creación y destrucción de objetos: cuándo y cómo crearlos,
cuándo y cómo evitar crearlos, cómo asegurarse de que se destruyan de manera oportuna
y cómo gestionar cualquier acción de limpieza que deba preceder a su destrucción.

## Elemento 1: Considera métodos de fábrica estáticos en lugar de constructores

La forma tradicional de que una clase permita a un cliente obtener una instancia es
proporcionar un constructor público. Hay otra técnica que debería ser parte del
kit de herramientas de todo programador. Una clase puede proporcionar un método
de fábrica estático público, que es simplemente un método estático que devuelve una
instancia de la clase. Aquí hay un ejemplo simple de Boolean (la clase primitiva
envuelta para boolean). Este método traduce un valor primitivo boolean en una
referencia de objeto Boolean:

```java
public static Boolean valueOf(boolean b) {
    return b ? Boolean.TRUE : Boolean.FALSE;
}
```
Nótese que un método de fábrica estático no es lo mismo que el patrón Factory Method
de Design Patterns [Gamma95]. El método de fábrica estático descrito en este
elemento no tiene un equivalente directo en Design Patterns.

Una clase puede proporcionar a sus clientes métodos de fábrica estáticos en lugar de,
o además de, constructores públicos. Proporcionar un método de fábrica estático en lugar
de un constructor público tiene tanto ventajas como desventajas.

>Una ventaja de los métodos de fábrica estáticos es que, a diferencia de los constructores,
tienen nombres. Si los parámetros de un constructor no describen por sí mismos el objeto
que se devuelve, un método de fábrica estático con un nombre bien elegido es más fácil
de usar y el código del cliente resultante es más fácil de leer. Por ejemplo, el
constructor `BigInteger(int, int, Random)`, que devuelve un `BigInteger` que
probablemente sea primo, habría sido mejor expresado como un método de fábrica estático
llamado `BigInteger.probablePrime`. (Este método se agregó en Java 4).

Una clase solo puede tener un único constructor con una firma dada. Se sabe que los
programadores han eludido esta restricción proporcionando dos constructores cuyas listas
de parámetros solo difieren en el orden de sus tipos de parámetros. Esta es una idea
realmente mala. El usuario de tal API nunca podrá recordar qué constructor es cuál y
terminará llamando al incorrecto por error. Las personas que lean código que usa estos
constructores no sabrán qué hace el código sin referirse a la documentación de la clase.
Debido a que tienen nombres, los métodos de fábrica estáticos no comparten la restricción
discutida en el párrafo anterior. En casos donde una clase parece requerir múltiples
constructores con la misma firma, reemplace los constructores con métodos de fábrica
estáticos y nombres cuidadosamente elegidos para resaltar sus diferencias.

>Una segunda ventaja de los métodos de fábrica estáticos es que, a diferencia de los
constructores, no están obligados a crear un objeto nuevo cada vez que se invocan. Esto
permite a las clases inmutables (Elemento 17) usar instancias preconstruidas, o almacenar
en caché instancias a medida que se construyen, y dispensarlas repetidamente para evitar
crear objetos duplicados innecesarios. El método `Boolean.valueOf(boolean)` ilustra
esta técnica: nunca crea un objeto. Esta técnica es similar al patrón Flyweight [Gamma95].
Puede mejorar enormemente el rendimiento si se solicitan objetos equivalentes con frecuencia,
especialmente si son costosos de crear.

La capacidad de los métodos de fábrica estáticos para devolver el mismo objeto de
invocaciones repetidas permite a las clases mantener un control estricto sobre qué instancias
existen en cualquier momento. A las clases que hacen esto se les dice que están controladas
por instancias. Hay varias razones para escribir clases controladas por instancias. El control
de instancias permite a una clase garantizar que es un singleton (Elemento 3) o no
instantiable (Elemento 4). Además, permite a una clase de valor inmutable (Elemento 17)
garantizar que no existen dos instancias iguales: a.equals(b) si y solo si a == b. Esto
es la base del patrón Flyweight [Gamma95]. Los tipos Enum (Elemento 34) proporcionan
esta garantía.

>Una tercera ventaja de los métodos de fábrica estáticos es que, a diferencia de los
constructores, pueden devolver un objeto de cualquier subtipo de su tipo de retorno. Esto
te da gran flexibilidad en la elección de la clase del objeto devuelto.

Una aplicación de esta flexibilidad es que una API puede devolver objetos sin hacer públicas
sus clases. Ocultar clases de implementación de esta manera conduce a una API muy compacta.
Esta técnica se presta a marcos basados en interfaces (Elemento 20), donde las interfaces
proporcionan tipos de retorno naturales para los métodos de fábrica estáticos.

Antes de Java 8, las interfaces no podían tener métodos estáticos. Por convención, los
métodos de fábrica estáticos para una interfaz llamada Tipo se ponían en una clase
compañera no instantiable (Elemento 4) llamada Tipos. Por ejemplo, el Java Collections
Framework tiene cuarenta y cinco implementaciones de utilidad de sus interfaces,
proporcionando colecciones inmodificables, colecciones sincronizadas y similares.
Casi todas estas implementaciones se exportan a través de métodos de fábrica estáticos
en una clase no instantiable (java.util.Collections). Las clases de los objetos devueltos
son todas no públicas.

El API del Framework de Colecciones es mucho más pequeña de lo que habría sido si
hubiera exportado cuarenta y cinco clases públicas separadas, una para cada implementación
de conveniencia. No es solo el volumen del API lo que se reduce, sino también el peso
conceptual: el número y la dificultad de los conceptos que los programadores deben dominar
para usar el API. El programador sabe que el objeto devuelto tiene precisamente el API
especificado por su interfaz, por lo que no es necesario leer documentación adicional de
la clase de implementación. Además, el uso de tal método de fábrica estático requiere que
el cliente se refiera al objeto devuelto por la interfaz en lugar de la clase de implementación,
lo cual es generalmente una buena práctica (Elemento 64).

A partir de Java 8, la restricción de que las interfaces no puedan contener métodos estáticos fue
eliminada, por lo que normalmente hay pocas razones para proporcionar una clase compañera no
instantiable para una interfaz. Muchos miembros estáticos públicos que habrían estado en
dicha clase deben en cambio colocarse en la interfaz misma. Sin embargo, cabe señalar que
todavía puede ser necesario poner la mayor parte del código de implementación detrás de estos
métodos estáticos en una clase separada de paquete privado. Esto se debe a que Java 8
requiere que todos los miembros estáticos de una interfaz sean públicos. Java 9 permite
métodos estáticos privados, pero los campos estáticos y las clases de miembros estáticos
todavía deben ser públicos.

>Una cuarta ventaja de las fábricas estáticas es que la clase del objeto devuelto puede variar
de llamada en llamada como una función de los parámetros de entrada. Cualquier subtipo del
tipo de retorno declarado es permisible. La clase del objeto devuelto también puede variar de
versión en versión.

>La clase `EnumSet` (Elemento 36) no tiene constructores públicos, solo fábricas estáticas.
En la implementación de OpenJDK, devuelven una instancia de una de dos subclases,
dependiendo del tamaño del tipo enum subyacente: si tiene sesenta y cuatro o menos
elementos, como la mayoría de los tipos enum, las fábricas estáticas devuelven una instancia
de `RegularEnumSet`, respaldada por un solo long; si el tipo enum tiene sesenta y cinco o
más elementos, las fábricas devuelven una instancia de `JumboEnumSet`, respaldada por un
array de long. La existencia de estas dos clases de implementación es invisible para los
clientes. Si `RegularEnumSet` dejara de ofrecer ventajas de rendimiento para tipos enum
pequeños, podría eliminarse en una futura versión sin efectos negativos. De manera similar,
una futura versión podría agregar una tercera o cuarta implementación de `EnumSet` si
resultara beneficioso para el rendimiento. Los clientes ni saben ni les importa la clase del
objeto que obtienen de la fábrica; solo les importa que sea alguna subclase de `EnumSet`.

>Una quinta ventaja de las fábricas estáticas es que la clase del objeto devuelto no necesita
existir cuando se escribe la clase que contiene el método. Tales métodos de fábrica estáticos
flexibles forman la base de marcos de proveedores de servicios, como la API de Conectividad
de Bases de Datos de Java (JDBC). Un marco de proveedor de servicios es un sistema en el
que los proveedores implementan un servicio y el sistema hace disponibles las
implementaciones a los clientes, desacoplando a los clientes de las implementaciones.

Hay tres componentes esenciales en un marco de proveedor de servicios: una interfaz de
servicio, que representa una implementación; una API de registro de proveedores, que los
proveedores utilizan para registrar implementaciones; y una API de acceso al servicio, que los
clientes usan para obtener instancias del servicio. La API de acceso al servicio puede permitir
a los clientes especificar criterios para elegir una implementación. En ausencia de tales
criterios, la API devuelve una instancia de una implementación predeterminada, o permite al
cliente recorrer todas las implementaciones disponibles. La API de acceso al servicio es la
fábrica estática flexible que forma la base del marco de proveedor de servicios.

Un cuarto componente opcional de un marco de proveedor de servicios es una interfaz de
proveedor de servicios, que describe un objeto de fábrica que produce instancias de la interfaz
de servicio. En ausencia de una interfaz de proveedor de servicios, las implementaciones deben
instanciarse reflexivamente (Elemento 65). En el caso de JDBC, `Connection` desempeña el
papel de la interfaz de servicio, `DriverManager.registerDriver` es la API de registro de
proveedores, `DriverManager.getConnection` es la API de acceso al servicio, y `Driver` es
la interfaz de proveedor de servicios.

Hay muchas variantes del patrón de marco de proveedor de servicios. Por ejemplo, la API de
acceso al servicio puede devolver una interfaz de servicio más rica a los clientes que la
proporcionada por los proveedores. Este es el patrón Bridge [Gamma95]. Los marcos de
inyección de dependencias (Elemento 5) pueden verse como proveedores de servicios
poderosos. Desde Java 6, la plataforma incluye un marco de proveedor de servicios de propósito
general, `java.util.ServiceLoader`, por lo que generalmente no deberías escribir tu propio
(Elemento 59). JDBC no utiliza `ServiceLoader`, ya que el primero es anterior al segundo.

La principal limitación de proporcionar solo métodos de fábrica estáticos es que las clases sin
constructores públicos o protegidos no pueden ser subclasificadas. Por ejemplo, es imposible
subclasificar cualquiera de las clases de implementación de conveniencia en el Framework de
Colecciones. Se podría argumentar que esto es una bendición disfrazada porque alienta a los
programadores a usar la composición en lugar de la herencia (Elemento 18) y es necesario
para tipos inmutables (Elemento 17).

