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

