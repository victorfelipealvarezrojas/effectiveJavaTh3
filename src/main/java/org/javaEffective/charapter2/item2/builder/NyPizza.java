package org.javaEffective.charapter2.item2.builder;

import java.util.Objects;

public class NyPizza extends PizzaAbstract {
    public enum Size {
        SMALL,
        MEDIUM,
        LARGE
    }

    private final Size size;

    public static class Builder extends PizzaAbstract.BuilderAbstract<Builder> {

        private final Size size;

        public Builder(Size size) {
            this.size = Objects.requireNonNull(size);
        }

        @Override
        public NyPizza build() {
            return new NyPizza(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    private NyPizza(Builder builder) {
        super(builder);
        size = builder.size;
    }
}
