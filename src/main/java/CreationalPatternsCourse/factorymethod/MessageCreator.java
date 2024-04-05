package CreationalPatternsCourse.factorymethod;


import CreationalPatternsCourse.factorymethod.message.Message;

/**
 * This is our abstract "creator". 
 * The abstract method createMessage() has to be implemented by
 * its subclasses.
 */
public abstract class MessageCreator {

    // El patrón Factory Method permite que una clase
    // posponga la instancia a las subclases.
    public Message getMessage(){
        Message mssg = createMessage();
        mssg.addDefaultHeaders();
        mssg.encrypt();
        return mssg;
    }

    // Factory Method
    public abstract Message createMessage();
	
}
