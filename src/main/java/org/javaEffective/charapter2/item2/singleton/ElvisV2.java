package org.javaEffective.charapter2.item2.singleton;

// Singleton con fábrica estática
public class ElvisV2 {
    private static final ElvisV2 INSTANCE = new ElvisV2();
    private ElvisV2() {
        // ...
    }
    public static ElvisV2 getInstance() {
        return INSTANCE;
    }
    public void leaveTheBuilding() {
        // ...
    }
}