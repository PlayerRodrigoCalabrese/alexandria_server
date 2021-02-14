package event;

/**
 * Created by Locos on 02/10/2016.
 */
public class EventReward {

    private final byte type;
    private final String args;

    public EventReward(byte type, String args) {
        this.type = type;
        this.args = args;
    }




    public static EventReward[] parse(String rewards) {
        return new EventReward[] {};
    }
}
