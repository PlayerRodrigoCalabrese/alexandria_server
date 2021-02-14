package event.type;

import area.map.GameCase;
import client.Player;
import common.Formulas;
import common.PathFinding;
import common.SocketManager;
import entity.npc.Npc;
import event.EventManager;
import event.EventReward;
import game.world.World;
import object.GameObject;
import object.ObjectTemplate;
import util.TimerWaiter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Locos on 02/10/2016.
 */
public class EventSmiley extends Event {

    private final List<Byte> emotes = new ArrayList<>();
    private final List<World.Couple<Player, List<Byte>>> answers = new ArrayList<>();
    private byte state = 0, count = 0;
    private final short[] cells = {239, 253, 225, 267, 211, 281, 197, 295, 183, 309, 169};
    private Npc animator;

    public EventSmiley(byte id, byte maxPlayers, String name, String description, EventReward[] first) {
        super(id, maxPlayers, name, description, first);
        this.map = World.world.getMap((short) 9862);
    }

    @Override
    public void prepare() {
        this.answers.clear();
        this.emotes.clear();
        this.state = this.count = 0;
        this.animator = this.map.addNpc(EventManager.NPC, (short) 221, 1);

        if (!this.map.getPlayers().isEmpty()) {
            SocketManager.GAME_SEND_ADD_NPC_TO_MAP(this.map, this.animator);
        }

        TimerWaiter.addNext(() -> {
            boolean ok = true;
            while (EventManager.getInstance().getState() == EventManager.State.INITIALIZE || EventManager.getInstance().getState() == EventManager.State.PROCESSED) {
                moveAnimatorToCellId(ok ? 137 : 221);
                wait(2500);
                ok = !ok;
            }
        }, 0, TimeUnit.SECONDS, TimerWaiter.DataType.CLIENT);
    }

    @Override
    public void perform() {
        this.moveAnimatorToCellId(179);
        wait(1500);
        this.animator.setOrientation((byte) 1);
        SocketManager.GAME_SEND_eD_PACKET_TO_MAP(this.map, this.animator.getId(), 1);
        wait(1000);
        SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(this.map, "", this.animator.getId(), "Event", "Bonjour à tous et bienvenue à l'évent Smiley !");
        wait(3000);
        SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(this.map, "", this.animator.getId(), "Event", "Avant de commencer, laissez moi vous expliquer les règles du jeu.");
        wait(4000);
        SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(this.map, "", this.animator.getId(), "Event", "L'objectif est de reproduire les smileys que j'utiliserai.");
        wait(5000);
        SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(this.map, "", this.animator.getId(), "Event", "Par exemple, si j'utilise ce smiley :");
        wait(1500);
        SocketManager.GAME_SEND_EMOTICONE_TO_MAP(this.map, this.animator.getId(), 10);
        wait(2000);
        SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(this.map, "", this.animator.getId(), "Event", "Vous devrez utiliser ce smiley aussi.");
        wait(3500);
        SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(this.map, "", this.animator.getId(), "Event", "Attention, si vous vous trompez de smiley, vous serez éliminé(e).");
        wait(4000);
        SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(this.map, "", this.animator.getId(), "Event", "N'oubliez pas d'attendre que le chronomètre démarre avant d'utiliser un smiley.");
        wait(5500);
        SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(this.map, "", this.animator.getId(), "Event", "Vous êtes prêt(e)s ? C'est parti !");
        this.execute();
    }

    @Override
    public void execute() {
        this.count = 0;

        List<Player> participants = new ArrayList<>(EventManager.getInstance().getParticipants());
        int nbPlayers = participants.size();

        for(Player player : participants)
            if(player != null && player.isOnline())
                this.answers.add(new World.Couple<>(player, new ArrayList<>()));

        //239 cell emote pnj, 179 cell non emote
        while(nbPlayers > 1) {
            this.count++;

            this.moveAnimatorToCellId(134);
            wait(2000);

            this.emotes.add((byte) (Formulas.random.nextInt(14) + 1));

            for(byte e : this.emotes) {
                SocketManager.GAME_SEND_EMOTICONE_TO_MAP(this.map, this.animator.getId(), e);
                wait(1500 - 100);
            }
            wait(1500);


            this.moveAnimatorToCellId(179);
            wait(1500);

            SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(this.map, "", this.animator.getId(), "Event", "Faites vos jeux !");
            wait(750);

            this.initializeTurn((short) (3000 + 1000 * this.count));
            wait(1500 + 650 * this.count);
            SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(this.map, "", this.animator.getId(), "Event", "Rien ne va plus !");
            wait(1500 + 650 * this.count);
            SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(this.map, "", this.animator.getId(), "Event", "Les jeux sont faits !");
            this.state = 0;

            for(World.Couple<Player, List<Byte>> pair : new ArrayList<>(this.answers)) {
                if(pair.second.size() == this.count) {
                    byte c = 0;
                    boolean kick = false;
                    for(Byte b1 : pair.second) {
                        Byte b2 = this.emotes.get(c);
                        if(b2 == null) {
                            kick = true;
                            break;
                        } else if(b1.byteValue() != b2.byteValue()) {
                            kick = true;
                            break;
                        }
                        c++;
                    }
                    if(kick) {
                        this.kickPlayer(pair.first);
                        nbPlayers--;
                    } else {
                        pair.second.clear();
                        pair.first.sendMessage("(<b>Infos</b>) : Bien joué camarade !");
                    }
                } else {
                    this.kickPlayer(pair.first);
                    nbPlayers--;
                }
            }

            wait(1000);
            if(EventManager.getInstance().getParticipants().size() > 1) {
                SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(this.map, "", this.animator.getId(), "Event", "C'est parti pour le " + (this.count + 1) + "éme tours !");
            }
            wait(2000);
        }

        this.close();
    }

    @Override
    public void close() {
        if(!EventManager.getInstance().getParticipants().isEmpty()) {
            Player winner = EventManager.getInstance().getParticipants().get(0);

            SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(this.map, "", this.animator.getId(), "Event", "Félicitations à " + winner.getName() + " pour ça victoire !");
            winner.sendMessage("(<b>Infos</b>) : Vous venez de remporter 1 jeton !");
            ObjectTemplate template = World.world.getObjTemplate(EventManager.TOKEN);

            if(template != null) {
                GameObject object = template.createNewItem(1, false);

                if (object != null && winner.addObjet(object, true)) {
                    World.world.addGameObject(object, true);
                }
            }
        } else {
            SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(this.map, "", this.animator.getId(), "Event", "Personne n'a gagnez, une autre fois.. peut être !");
        }

        wait(2000);
        this.moveAnimatorToCellId(344);
        wait(2500);
        this.map.removeNpcOrMobGroup(this.animator.getId());
        this.map.send("GM|-" + this.animator.getId());
        this.map.send("GV");
        EventManager.getInstance().finishCurrentEvent();
    }

    public GameCase getEmptyCellForPlayer(Player player) {
        return map.getCase(this.cells[count++]);
    }

    @Override
    public void kickPlayer(Player player) {
        EventManager.getInstance().getParticipants().remove(player);
        Iterator<World.Couple<Player, List<Byte>>> iterator = this.answers.iterator();

        while(iterator.hasNext()) {
            World.Couple<Player, List<Byte>> pair = iterator.next();

            if(pair.first.getId() == player.getId()) {
                this.map.send("GA;208;" + player.getId() + ";" + player.getCurCell().getId() + ",2916,11,8,1");
                player.sendMessage("(<b>Infos</b>) : Vous avez perdu.. Peut-être une autre fois !");
                player.teleportOldMap();
                player.setBlockMovement(false);
                iterator.remove();
                break;
            }
        }
    }

    @Override
    public boolean onReceivePacket(EventManager manager, Player player, String packet) throws Exception {
        if(packet.startsWith("BS") && this.state == 1) {
            byte emote = Byte.parseByte(packet.substring(2));
            for(World.Couple<Player, List<Byte>> pair : this.answers) {
                if(pair.first.getId() == player.getId()) {
                    pair.second.add(emote);
                    if(pair.second.size() == this.count)
                        player.sendMessage("(<b>Infos</b>) : Le compte est bon !");
                    break;
                }
            }

        }
        return false;
    }

    private void initializeTurn(short time) {
        this.state = 1;
        for (Player player : EventManager.getInstance().getParticipants()) {
            player.send("GTS" + player.getId() + "|" + time);
        }
    }

    private void moveAnimatorToCellId(int cellId) {
        String path;

        try {
            path = PathFinding.getShortestStringPathBetween(this.map, this.animator.getCellId(), cellId, 20);
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }

        if(path != null) {
            this.animator.setCellId(cellId);
            SocketManager.GAME_SEND_GA_PACKET_TO_MAP(this.map, "0", 1, String.valueOf(this.animator.getId()), path);
        }
    }
}