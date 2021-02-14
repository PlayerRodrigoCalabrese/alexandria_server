package util.lang;

import client.Player;
import util.lang.type.English;
import util.lang.type.French;
import util.lang.type.Spanish;


/**
 * Created by Locos on 06/12/2015.
 */
public interface Lang {

    byte FRENCH = 1, ENGLISH = 2, SPANISH = 3;

    String get(int id);
    void initialize();

    static String get(Player player, int index) {
        return get(player.getGameClient().getLanguage(), index);
    }

    static String get(byte language, int index) {
        switch(language) {
            case FRENCH:
                return French.getInstance().get(index);
            case ENGLISH:
                return English.getInstance().get(index);
            case SPANISH:
                return Spanish.getInstance().get(index);
            default:
                return "Unknown lang data, please contact administrator.";
        }
    }
}
