package CreationalPatternsCourse.factorymethod.animal;

public class Client {
    public static void main(String[] args) {
        System.out.println("***Factory Method Pattern Demo.***");
        AnimalFactory factory;
        Animal animal;

        factory = new TigerFactory();
        animal = factory.createAnimal();
        animal.displayBehavior();

        // Create a dog and display its behavior
        // using DogFactory.
        factory = new DogFactory();
        animal = factory.createAnimal();
        animal.displayBehavior();
    }
}
