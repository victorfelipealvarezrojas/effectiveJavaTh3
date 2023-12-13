> ### SINGLETON
>
Un singleton es simplemente una clase que se instancia exactamente una vez [Gamma95]. Los singletons suelen representar ya sea un objeto sin estado, como una función (Elemento 24), o un componente del sistema que es intrínsecamente único. Hacer de una clase un singleton puede dificultar la prueba de sus clientes porque es imposible sustituir una implementación simulada (mock) para un singleton a menos que implemente una interfaz que sirva como su tipo.

Hay dos formas comunes de implementar singletons. Ambas se basan en mantener el constructor privado y exportar un miembro público estático para proporcionar acceso a la única instancia. En un enfoque, el miembro es un campo final:

```java
// Singleton con campo público final
public class Elvis {
    public static final Elvis INSTANCE = new Elvis();

    private Elvis() { 
        // Constructor privado
    }

    public void leaveTheBuilding() { 
        // Método de ejemplo
    }
}
```

La falta de un constructor público o protegido garantiza un universo "monoelvístico": exactamente una instancia de Elvis existirá una vez que la clase Elvis se haya inicializado, ni más ni menos. Nada de lo que haga un cliente puede cambiar esto, con una advertencia: un cliente con privilegios puede invocar el constructor privado reflexivamente (Elemento 65) con la ayuda del método `AccessibleObject.setAccessible`. Si necesitas defenderte contra este ataque, modifica el constructor para que lance una excepción si se le pide crear una segunda instancia.

En el segundo enfoque para implementar singletons, el miembro público es un método de fábrica estático:

```java
// Singleton con fábrica estática
public class Elvis {
    private static final Elvis INSTANCE = new Elvis();

    private Elvis() { 
        // Constructor privado
    }

    public static Elvis getInstance() { 
        return INSTANCE; 
    }

    public void leaveTheBuilding() { 
        // Método de ejemplo
    }
}
```


Todas las llamadas a `Elvis.getInstance` devuelven la misma referencia de objeto, y nunca se creará otra instancia de `Elvis` (con la misma advertencia mencionada anteriormente).


La principal ventaja del enfoque del campo público es que la API deja claro que la clase es un singleton: el campo público estático es final, por lo que siempre contendrá la misma referencia de objeto. La segunda ventaja es que es más simple.

Una ventaja del enfoque de la fábrica estática es que te brinda la flexibilidad de cambiar de opinión sobre si la clase es un singleton sin cambiar su API. El método de fábrica devuelve la única instancia, pero podría modificarse para devolver, por ejemplo, una instancia separada para cada hilo que lo invoque. Una segunda ventaja es que puedes escribir una fábrica de singleton genérica si tu aplicación lo requiere (Elemento 30). Una ventaja final de usar una fábrica estática es que una referencia de método se puede utilizar como proveedor, por ejemplo, `Elvis::instance` es un `Supplier<Elvis>`. A menos que una de estas ventajas sea relevante, el enfoque del campo público es preferible.

Para hacer que una clase singleton que utilice cualquiera de estos enfoques sea serializable (Capítulo 12), no es suficiente simplemente agregar `implements Serializable` a su declaración. Para mantener la garantía de singleton, declara todos los campos de instancia como transitorios y proporciona un método `readResolve` (Elemento 89). De lo contrario, cada vez que se deserializa una instancia serializada, se creará una nueva instancia, lo que, en el caso de nuestro ejemplo, podría llevar a avistamientos falsos de Elvis. Para evitar que esto ocurra, agrega este método `readResolve` a la clase `Elvis`:

```java
// Método readResolve para preservar la propiedad de singleton
private Object readResolve() {
    // Devolver el único Elvis real y dejar que el recolector de basura
    // se encargue del imitador de Elvis.
    return INSTANCE;
}
```

Una tercera forma de implementar un singleton es declarar un enum de un solo elemento:

```java
public enum ElvisV3 {
    INSTANCE;
    public void leaveTheBuilding() {
        // Método de ejemplo
    }
}
```
Este enfoque es similar al del campo público, pero más conciso, proporciona la maquinaria de serialización de forma gratuita y garantiza de manera sólida que no haya instanciaciones múltiples, incluso frente a sofisticados ataques de serialización o reflexión. Aunque este enfoque puede parecer un tanto antinatural, un tipo de enum de un solo elemento suele ser la mejor manera de implementar un singleton. Ten en cuenta que no puedes utilizar este enfoque si tu singleton debe extender una superclase que no sea `Enum` (aunque puedes declarar un enum para implementar interfaces).


otro ejemplo ejemplo, MySingletonEnum actúa como un singleton porque tiene solo una instancia, que es INSTANCE. Al acceder a MySingletonEnum.INSTANCE, obtienes la única instancia disponible. Además, implementa la interfaz MyInterface y proporciona una implementación concreta de myMethod. La clave aquí es que la instancia única es garantizada por la naturaleza de los enums en Java.

```java
// Interfaz que define el comportamiento común
interface MyInterface {
    void myMethod();
}

// Enum singleton que implementa la interfaz
public enum MySingletonEnum implements MyInterface {
    INSTANCE;

    @Override
    public void myMethod() {
        System.out.println("Implementación de myMethod en el enum singleton");
    }
}

// Clase de ejemplo que utiliza el enum singleton
public class Main {
    public static void main(String[] args) {
        // Acceder a la instancia única del enum singleton
        MySingletonEnum singleton = MySingletonEnum.INSTANCE;

        // Utilizar el método del singleton
        singleton.myMethod();
    }
}

```