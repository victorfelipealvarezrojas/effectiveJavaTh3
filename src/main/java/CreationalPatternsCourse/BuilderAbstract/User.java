package CreationalPatternsCourse.BuilderAbstract;


public class User extends UserAbstract {
    private final String rut;

    public String getRut() {
        return rut;
    }

    // La restricción <T extends BuilderAbstract<T>>  asegura que T sea siempre un tipo que extiende BuilderAbstract
    // Builder extends <-- BuilderAbstract (restriccion)
    // permitiendo que las instancias de subclases de BuilderAbstract puedan usar métodos que retornan su propio tipo específico.
    public static class Builder extends BuilderAbstract<Builder> {
        private String rut;

        // el metodo de constructor de esta sub clase retorna la sub clase correcta de esta implementacion
        public Builder rut(String rut) {
            this.rut = rut;
            return self();
        }

        @Override
        public User build() {
            return new User(this);
        }

        /**
         * Este método es el que permite que las subclases de BuilderAbstract osea this
         * puedan usar métodos que retornan su propio tipo específico.
         */
        @Override
        protected Builder self() {
            return this;
        }
        /**
         * Esta técnica, en la que un método de una subclase se declara para devolver un subtipo del tipo de
         * retorno declarado en la clase superior, se conoce como tipificación de retorno covariante.
         * Permite a los clientes utilizar estos constructores (builders) sin necesidad de realizar casting.
         */
    }

    private User(Builder builder) {
        super(builder);
        rut = builder.rut;
    }
}