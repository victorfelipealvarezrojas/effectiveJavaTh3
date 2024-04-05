package CreationalPatternsCourse.factorymethod;


import CreationalPatternsCourse.factorymethod.message.JSONMessage;
import CreationalPatternsCourse.factorymethod.message.Message;

/**
 * Provides implementation for creating JSON messages
 */
public class JSONMessageCreator extends MessageCreator {

    @Override
    public Message createMessage() {
        return new JSONMessage();
    }
}
