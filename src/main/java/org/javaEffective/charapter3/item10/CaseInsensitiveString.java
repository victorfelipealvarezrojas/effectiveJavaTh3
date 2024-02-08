package org.javaEffective.charapter3.item10;

import java.util.Objects;

public final class CaseInsensitiveString {
    private final String value;

    public CaseInsensitiveString(String s) {
        this.value = Objects.requireNonNull(s);
    }

    // Broken - violates symmetry!
    //@Override
    public boolean equalsNotCorrect(Object o) {
        return o instanceof CaseInsensitiveString &&
                ((CaseInsensitiveString) o).value.equalsIgnoreCase(value);
    }
    // Remainder omitted
    @Override
    public boolean equals(Object o) {
        return o instanceof CaseInsensitiveString &&
                ((CaseInsensitiveString) o).value.equalsIgnoreCase(this.value);
    }
}
