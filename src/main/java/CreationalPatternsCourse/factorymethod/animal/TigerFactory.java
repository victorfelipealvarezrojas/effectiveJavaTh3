package CreationalPatternsCourse.factorymethod.animal;

public class TigerFactory extends AnimalFactory{
    @Override
    protected Animal createAnimal() {
        return new Tiger();
    }
}
