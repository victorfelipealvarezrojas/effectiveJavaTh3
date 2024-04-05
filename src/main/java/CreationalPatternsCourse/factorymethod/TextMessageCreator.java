package CreationalPatternsCourse.factorymethod;


import CreationalPatternsCourse.factorymethod.message.Message;
import CreationalPatternsCourse.factorymethod.message.TextMessage;

/**
 * Provides implementation for creating Text messages
 */
public class TextMessageCreator extends MessageCreator {


    @Override
    public Message createMessage() {
        return new TextMessage();
    }
}
