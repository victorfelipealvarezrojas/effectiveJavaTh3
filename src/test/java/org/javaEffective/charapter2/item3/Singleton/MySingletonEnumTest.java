package org.javaEffective.charapter2.item3.Singleton;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MySingletonEnumTest {
    @Test
    public void testSingletonBehavior() {
        // Acceder a la instancia única del enum singleton
        MySingletonEnum singleton = MySingletonEnum.INSTANCE;
        MySingletonEnum singleton2 = MySingletonEnum.INSTANCE;
        // Utilizar el método del singleton
        singleton.myMethod();
        singleton.leaveTheBuilding();
        // Comprobar que la instancia es la misma
        assertSame(singleton, MySingletonEnum.INSTANCE);
        assertSame(singleton, singleton2);
    }
}