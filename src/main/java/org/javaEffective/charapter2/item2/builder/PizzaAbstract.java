package org.javaEffective.charapter2.item2.builder;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public abstract class PizzaAbstract {
    public enum Topping { HAM, MUSHROOM, ONION, PEPPER, SAUSAGE }

    final Set<Topping> toppings;

    abstract static class BuilderAbstract<T extends BuilderAbstract<T>> {
        EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);

        public T addTopping(Topping topping) {
            toppings.add(Objects.requireNonNull(topping));
            return self();
        }

        abstract PizzaAbstract build();

        // Subclasses must override this method to return "this"
        protected abstract T self();
    }

    PizzaAbstract(BuilderAbstract<?> builder) {
        toppings = builder.toppings.clone(); // See Item 50
    }
}
