package org.javaEffective.charapter3.item10;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CaseInsensitiveStringTest {
    @Test
    public void equals_shouldReturnTrue_whenComparingEqualCaseInsensitiveStrings() {
        CaseInsensitiveString cis1 = new CaseInsensitiveString("Polish");
        CaseInsensitiveString cis2 = new CaseInsensitiveString("Polish");
        assertEquals(cis1, cis2);
    }

    @Test
    public void ComparingEqualCaseInsensitiveStringsSymmetricViolation() {
        CaseInsensitiveString cis = new CaseInsensitiveString("Polish");
        CaseInsensitiveString cis2 = new CaseInsensitiveString("polish");
        String s = "Polish";
        assertFalse(cis.equals(s));
        assertTrue(cis.equals(cis2));
    }





}