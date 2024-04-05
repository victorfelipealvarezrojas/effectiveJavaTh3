package CreationalPatternsCourse.factorymethod.animal;

public class DogFactory extends AnimalFactory{
    @Override
    protected Animal createAnimal() {
        return new Dog();
    }
}
