package org.javaEffective.charapter2.item3.Singleton;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class ElvisTest {
    @Test
    public void testSingletonInstance() {
        Elvis instancia1 = Elvis.INSTANCE;
        Elvis instancia2 = Elvis.INSTANCE;
        assertSame(instancia1, instancia2);
    }

    @Test
    public void testReflectionCreateInstance() throws ClassNotFoundException   ,
                                                      NoSuchMethodException    ,
                                                      InvocationTargetException,
                                                      InstantiationException   ,
                                                      IllegalAccessException {

        Class<?> elvisClass = Class.forName(Elvis.class.getName());
        // private constructor
        Constructor<?> constructor = elvisClass.getDeclaredConstructor();
        // Hacer que el constructor sea accesible
        constructor.setAccessible(true);

        // Crear una nueva instancia utilizando reflexión
        Elvis nuevaInstancia = (Elvis) constructor.newInstance();
        Elvis nuevaInstancia2 = (Elvis) constructor.newInstance();

        // Verificar que la nueva instancia no es la misma que la instancia singleton
        assertNotSame(Elvis.INSTANCE, nuevaInstancia);
        assertNotSame(nuevaInstancia, nuevaInstancia2);
    }
}