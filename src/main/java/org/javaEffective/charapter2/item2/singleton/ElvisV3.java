package org.javaEffective.charapter2.item2.singleton;

import java.io.Serial;
import java.io.Serializable;

// Singleton enum - el enfoque preferido
public class ElvisV3 implements Serializable {
    public static final ElvisV3 INSTANCE = new ElvisV3();

    private ElvisV3() {
        // ...
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }

    public void leaveTheBuilding() {
        // ...
    }

}
