package org.javaEffective.charapter2.item3.Singleton;

public class ElvisV2 {
    private static final ElvisV2 INSTANCE = new ElvisV2();

    private ElvisV2() {
        // Constructor privado
    }

    public static ElvisV2 getInstance() {
        return INSTANCE;
    }

    public void leaveTheBuilding() {
        // Método de ejemplo
    }
}