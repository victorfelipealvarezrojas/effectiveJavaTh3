package org.javaEffective.charapter2.item6;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class RomanNumeralsTest {

    @Test
    public void testIsRomanNumeralPerformance() {
        long startTimeA = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            RomanNumerals.isRomanNumeral("XIV");
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTimeA) / 1000000;

        long startTimeB = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            isRomanNumeralNo.isRomanNumeral("XIV");
        }
        long endTimeB = System.nanoTime();
        long durationB = (endTimeB - startTimeB) / 1000000;

        assertTrue(duration < 400);
        assertTrue(durationB < 1500);
    }





}