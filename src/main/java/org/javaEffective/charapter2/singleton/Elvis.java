package org.javaEffective.charapter2.singleton;

// Singleton con campo público final
public class Elvis {
    public static final Elvis INSTANCE = new Elvis();

    private Elvis() {
        // ...
    }

    public void leaveTheBuilding() {
        // ...
    }
}
