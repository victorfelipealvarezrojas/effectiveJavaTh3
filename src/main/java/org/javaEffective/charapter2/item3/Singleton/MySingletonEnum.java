package org.javaEffective.charapter2.item3.Singleton;

public enum MySingletonEnum implements MyInterface{
    INSTANCE;

    @Override
    public void myMethod() {
        System.out.println("Implementación de myMethod en el enum singleton");
    }

    public void leaveTheBuilding() {
        System.out.println("Implementación de leaveTheBuilding en el enum singleton");
    }
}
