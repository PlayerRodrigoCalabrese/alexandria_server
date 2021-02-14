package event;

import client.Player;
import common.Formulas;
import common.SocketManager;
import database.Database;
import event.type.Event;
import game.scheduler.Updatable;
import game.world.World;
import kernel.Config;
import util.TimerWaiter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Locos on 02/10/2016.
 */
public class EventManager extends Updatable {

    public final static int TOKEN = 50007, NPC = 16000;
    private final static EventManager singleton = new EventManager(Database.getStatics().getEventData().load());

    public static EventManager getInstance() {
        return singleton;
    }

    public enum State {
        WAITING, INITIALIZE, PROCESSED, STARTED, FINISHED
    }

    /** EvenetManager **/

    private final Event[] events;
    private State state = State.WAITING;
    private Event current, lastest;
    private short count = 0;
    private List<Player> participants = new ArrayList<>();

    private EventManager(Event[] events) {
        super(60000);
        this.events = events;
    }

    public State getState() {
        return state;
    }

    public Event getCurrentEvent() {
        return current;
    }

    public List<Player> getParticipants() {
        return participants;
    }

    public void startNewEvent() {
        Event event = this.events[Formulas.random.nextInt(this.events.length)];

        if(event != null) {
            if(this.events.length > 1 && this.lastest != null && event.getEventId() == this.lastest.getEventId()) {
                this.startNewEvent();
                return;
            }

            event.prepare();
            this.lastTime = System.currentTimeMillis();
            this.current = event;
            this.state = State.PROCESSED;
            World.world.sendMessageToAll("(<b>Infos</b>) : L'événement '<b>" + event.getEventName() + "</b>' vient de démarrer, <b>.event</b> pour vous inscrire.");
        } else {
            this.startNewEvent();
        }
    }

    private synchronized void startCurrentEvent() {
        if(this.state == State.STARTED)
            return;
        this.state = State.STARTED;

        if(!this.hasEnoughPlayers()) {
            this.count = 0;
            this.lastTime = System.currentTimeMillis();
            this.state = State.PROCESSED;
        } else if(this.moveAllPlayersToEventMap(true)) {
            this.lastTime = System.currentTimeMillis();
            TimerWaiter.addNext(() -> this.current.perform(), 0, TimeUnit.SECONDS, TimerWaiter.DataType.CLIENT);
        }
    }

    public void finishCurrentEvent() {
        this.participants.stream().filter(player -> player != null).forEach(player -> {
            player.teleportOldMap();
            player.setBlockMovement(false);
        });

        this.current.interrupt();
        this.lastest = this.current;
        this.current = null;
        this.lastTime = System.currentTimeMillis();
        this.count = 0;
        this.state = State.WAITING;
    }

    public synchronized byte subscribe(final Player player) {
        if(this.current == null || this.state == State.WAITING) {
            return 0;
        } else {
            if(this.state == State.PROCESSED) {
                for (Player p : this.getParticipants()) {
                    if (player.getAccount() != null && p != null && p.getAccount() != null) {
                        if (player.getAccount().getCurrentIp().compareTo(p.getAccount().getCurrentIp()) == 0) {
                            SocketManager.GAME_SEND_MESSAGE(player, "Impossible de rejoindre ce combat, vous êtes déjà dans le combat avec une même IP !");
                            return 1;
                        }
                    }
                }
                if (this.participants.size() >= this.current.getMaxPlayers()) {
                    player.sendMessage("(<b>Infos</b>) : L'événement '<b>" + this.current.getEventName() + "</b>' est déjà au complet.");
                } else if (this.participants.contains(player)) {
                    this.participants.remove(player);
                    player.sendMessage("(<b>Infos</b>) : Vous venez de vous désinscrire de l'événement '<b>" + this.current.getEventName() + "</b>'.");
                } else if (this.hasSameIP(player)) {
                    player.sendMessage("(<b>Infos</b>) : Vous avez déjà un membre de votre réseaux internet en jeu sur l'événement.");
                } else if (player.getParty() != null && player.getParty().getMaster() != null) {
                    player.sendMessage("(<b>Infos</b>) : Vous ne pouvez pas rejoindre un événement en étant en mode maître.");
                } else {
                    this.participants.add(player);
                    player.sendMessage("(<b>Infos</b>) : Vous venez de vous inscrire à l'événement '<b>" + this.current.getEventName() + "</b>'.");

                    if (this.participants.size() >= this.current.getMaxPlayers()) {
                        this.startCurrentEvent();
                    } else {
                        this.participants.forEach(target -> target.sendMessage("(<b>Infos</b>) : En attente de " +
                                (this.current.getMaxPlayers() - this.participants.size()) + " joueur(s)."));
                    }
                }
            } else {
                player.sendMessage("(<b>Infos</b>) : L'événement '<b>" + this.current.getEventName() + "</b>' a déjà démarrer.");
            }
        }
        return 1;
    }

    private boolean hasSameIP(Player player) {
        if(player != null && player.getAccount() != null) {
            final String ip = player.getAccount().getCurrentIp();

            if(ip.equals("127.0.0.1"))
                return false;
            for (Player target : this.participants) {
                if (target != null && target.getAccount() != null) {
                    return ip.equals(target.getAccount().getCurrentIp());
                }

            }
        }
        return false;
    }

    private boolean hasEnoughPlayers() {
        if(this.current == null)
            return false;
        short percent = (short) ((100 * this.participants.size()) / this.current.getMaxPlayers());
        return percent >= 30;
    }

    @Override
    public void update() {
        if(Config.INSTANCE.getAUTO_EVENT() && this.verify()) {
            if (this.state == State.WAITING) {
                short result = (short) (Config.INSTANCE.getTIME_PER_EVENT() - (++count));
                if (result == 0) {
                    this.count = 0;
                    this.lastTime = System.currentTimeMillis();
                    this.state = State.INITIALIZE;
                    TimerWaiter.addNext(this::startNewEvent, 0, TimeUnit.SECONDS, TimerWaiter.DataType.CLIENT);
                } else if (result == 60 || result == 30 || result == 15 || result == 5) {
                    World.world.sendMessageToAll("(<b>Infos</b>) : Un <b>événement</b> va démarrer dans " + result + " minutes.");
                }
            } else if (this.state == State.PROCESSED) {
                short result = (short) ((this.hasEnoughPlayers() ? 5 : 10) - (++count));
                this.moveAllPlayersToEventMap(false);

                if (result <= 0) {
                    this.startCurrentEvent();
                } else if(result == 1 && this.hasEnoughPlayers()) {
                    for(Player player : this.participants) {
                        player.sendMessage("(<b>Infos</b>) : L'événement va commencer dans 1 minute.");
                    }
                }
            }
        }
    }

    @Override
    public Object get() {
        return lastTime;
    }

    private boolean moveAllPlayersToEventMap(boolean teleport) {
        boolean ok = true;
        final StringBuilder afk = teleport ? new StringBuilder("") : null;

        for(final Player player : this.participants) {
            if(player.getFight() != null || !player.isOnline() || player.isGhost() || player.getDoAction()) {
                ok = false;
                this.participants.remove(player);
                player.sendMessage("La prochaine fois tâchez d'être disponible !");
                player.sendMessage("(<b>Infos</b>) : Vous venez d'être expulsé du jeu pour indisponibilité.");

                if(teleport) {
                    afk.append(afk.length() == 0 ? ("<b>" + player.getName() + "</b>") : (", <b>" + player.getName() + "</b>"));
                }
            }
        }

        if(!ok || !teleport) {
            if(teleport) {
                this.participants.forEach(player -> player.sendMessage("(<b>Infos</b> : Merci à " + afk.toString() + " expulsé pour inactivité."));
                World.world.getOnlinePlayers().stream().filter(target -> !afk.toString().contains(target.getName()))
                        .forEach(target -> target.sendMessage("(<b>Infos</b> : Il vous reste 30 secondes pour vous inscrire à l'événement '<b>" + this.current.getEventName() + "</b>' (<b>.event</b>)."));
            }
            return false;
        }

        for(final Player player : this.participants) {
            if(player.getFight() == null && player.isOnline() && !player.isGhost() && !player.getDoAction()) {
                player.setOldPosition();
                player.setBlockMovement(true);
                player.teleport(this.current.getMap().getId(), this.current.getEmptyCellForPlayer(player).getId());
                SocketManager.GAME_SEND_eD_PACKET_TO_MAP(this.current.getMap(), player.getId(), 4);
            } else {
                ok = false;
                this.participants.remove(player);
                player.sendMessage("La prochaine fois tâchez d'être disponible !");
                player.sendMessage("(<b>Infos</b>) : Vous venez d'être expulsé du jeu pour indisponibilité.");
            }
        }

        return ok;
    }

    public static boolean isInEvent(Player player) {
        if(Config.INSTANCE.getAUTO_EVENT() && EventManager.getInstance().getState() == State.STARTED)
            for(Player target : EventManager.getInstance().getParticipants())
                if(target.getId() == player.getId())
                    return true;
        return false;
    }
}
