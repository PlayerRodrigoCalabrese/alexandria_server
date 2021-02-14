package event.type;

import area.map.GameCase;
import client.Player;
import entity.npc.Npc;
import event.EventManager;
import event.EventReward;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Locos on 22/10/2016.
 */
public class EventFindMe extends Event {

    private final static List<FindMeRow> findMeRows = new ArrayList<>();

    /***************************/

    private Npc animator;

    public EventFindMe(byte id, byte maxPlayers, String name, String description, EventReward[] first) {
        super(id, maxPlayers, name, description, first);
    }

    @Override
    public void prepare() {
        this.animator = this.map.addNpc(16000, (short) 221, 1);

    }

    @Override
    public void perform() {

    }

    @Override
    public void execute() {

    }

    @Override
    public void close() {

    }

    @Override
    public boolean onReceivePacket(EventManager manager, Player player, String packet) throws Exception {
        return false;
    }

    @Override
    public GameCase getEmptyCellForPlayer(Player player) {
        return null;
    }

    @Override
    public void kickPlayer(Player player) {

    }

    public static class FindMeRow {
        private final short map;
        private final short cell;
        private final String[] indices;
        private byte actual = 0;

        public FindMeRow(short map, short cell, String[] indices) {
            this.map = map;
            this.cell = cell;
            this.indices = indices;
            EventFindMe.findMeRows.add(this);
        }

        public short getMap() {
            return map;
        }

        public short getCell() {
            return cell;
        }

        public String getNextIndice() {
            if(this.actual > this.indices.length - 1) return null;
            String indice = this.indices[this.actual];
            this.actual++;
            return indice;
        }
    }
}
