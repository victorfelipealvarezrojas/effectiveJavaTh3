package org.javaEffective.charapter2.item2.builder;

public class Calzone extends PizzaAbstract {
    private final boolean sauceInside;

    public static class BuilderInnerClass extends PizzaAbstract.BuilderAbstract<BuilderInnerClass> {
        private boolean sauceInside = false; // Default

        public BuilderInnerClass sauceInside() {
            sauceInside = true;
            return this;
        }

        @Override
        public Calzone build() {
            return new Calzone(this);
        }

        @Override
        protected BuilderInnerClass self() {
            return this;
        }
    }

    private Calzone(BuilderInnerClass builder) {
        super(builder);
        sauceInside = builder.sauceInside;
    }
}
