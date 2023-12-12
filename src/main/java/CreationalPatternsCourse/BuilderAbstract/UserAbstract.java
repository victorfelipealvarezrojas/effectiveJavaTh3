package CreationalPatternsCourse.BuilderAbstract;

import java.util.EnumSet;
import java.util.Set;

public abstract class UserAbstract {
    public enum Interest {
        MUSIC, PHOTOGRAPHY,
        PROGRAMING, PHILOSOPHY,
        POETRY, SPORTS, NUTRITION
    }

    private final String name;
    private final int age;
    private final String email;
    private final Set<Interest> interests;

    UserAbstract(BuilderAbstract<?> builder) {
        this.name = builder.name;
        this.age = builder.age;
        this.email = builder.email;
        this.interests = builder.interests.clone();
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public Set<Interest> getInterests() {
        return interests;
    }

    /**  @BuilderAbstract::<<
         clase genérica que utiliza un parámetro de tipo T.  La restricción   <T extends BuilderAbstract<T>>
         asegura que T sea siempre un tipo que extiende BuilderAbstract.  Esto simula un "self-type",
         permitiendo que las instancias de subclases de BuilderAbstract puedan usar métodos  que retornan su propio tipo específico.
     */
    abstract static class BuilderAbstract<T extends BuilderAbstract<T>> {
        private String name;
        private int age;
        private String email;
        EnumSet<Interest> interests = EnumSet.noneOf(Interest.class);

        public T name(String name) {
            this.name = name;
            return self();
        }

        public T age(int age) {
            this.age = age;
            return self();
        }

        public T email(String email) {
            this.email = email;
            return self();
        }

        public T addInterest(Interest interest) {
            interests.add(interest);
            return self();
        }

        abstract UserAbstract build();

        protected abstract T self();
    }
}