package org.javaEffective.charapter2;

import jdk.jfr.Description;
import org.javaEffective.charapter2.item2.builder.Calzone;
import org.javaEffective.charapter2.item2.builder.NutritionFacts;
import org.javaEffective.charapter2.item2.builder.NyPizza;
import org.junit.jupiter.api.Test;

import static org.javaEffective.charapter2.item2.builder.NyPizza.Size.*;
import static org.javaEffective.charapter2.item2.builder.PizzaAbstract.Topping.*;
import static org.junit.jupiter.api.Assertions.*;

class Charapter2Test {

    @Test
    @Description("Test the Builder pattern")
    void NutritionFactsBuilderTest() {
        // Crea un objeto utilizando el patrón Builder
        NutritionFacts cocaCola = new NutritionFacts.Builder(240, 8)
                .calories(100)
                .sodium(35)
                .carbohydrate(27)
                .build();

        assertEquals(240, cocaCola.getServingSize());
        assertEquals(8, cocaCola.getServings());
        assertEquals(100, cocaCola.getCalories());

        // Modifica los valores utilizando el patrón Builder con una nueva instancia
        NutritionFacts modifiedFacts = new NutritionFacts.Builder(cocaCola.getServingSize(), cocaCola.getServings())
                .calories(200)
                .fat(8)
                .sodium(15)
                .carbohydrate(25)
                .build();

        assertEquals(cocaCola.getServingSize(), modifiedFacts.getServingSize());
        assertEquals(cocaCola.getServings(), modifiedFacts.getServings());
        assertNotEquals(cocaCola.getCalories(), modifiedFacts.getCalories());
    }

    @Test
    @Description("Test the Builder Abstract")
    void PizzaTest() {
        NyPizza pizza = new NyPizza
                .Builder(SMALL)
                .addTopping(SAUSAGE)
                .addTopping(ONION)
                .addTopping(MUSHROOM)
                .build();

        Calzone calzone = new Calzone
                .BuilderInnerClass()
                .addTopping(HAM)
                .sauceInside()
                .build();
    }
}