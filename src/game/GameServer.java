package game;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import client.Account;
import client.Player;
import exchange.ExchangeClient;
import game.world.World;
import kernel.Config;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class GameServer {

    public static short MAX_PLAYERS = 700;
    public static GameServer INSTANCE = new GameServer();

    private final static @NotNull ArrayList<Account> waitingClients = new ArrayList<>();
    private final static @NotNull Logger log = LoggerFactory.getLogger(GameServer.class);
    private final @NotNull IoAcceptor acceptor;

    static {

    }

    private GameServer(){
        acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF8"), LineDelimiter.NUL, new LineDelimiter("\n\0"))));
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60 * 10 /*10 Minutes*/);
        acceptor.setHandler(new GameHandler());
    }

    public boolean start() {
        if (acceptor.isActive()) {
            log.warn("Error already start but try to launch again");
            return false;
        }

        try {
            acceptor.bind(new InetSocketAddress(Config.INSTANCE.getGamePort()));
            log.info("Game server started on address : {}:{}", Config.INSTANCE.getIp(), Config.INSTANCE.getGamePort());
            return true;
        } catch (IOException e) {
            log.error("Error while starting game server", e);
            return false;
        }
    }

    public void stop() {
        if (!acceptor.isActive()) {
            acceptor.getManagedSessions().values().stream()
                    .filter(session -> session.isConnected() || !session.isClosing())
                    .forEach(session -> session.close(true));
            acceptor.dispose();
            acceptor.unbind();
        }

        log.error("The game server was stopped.");
    }

    public static List<GameClient> getClients() {
        return INSTANCE.acceptor.getManagedSessions().values().stream()
                .filter(session -> session.getAttachment() != null)
                .map(session -> (GameClient) session.getAttachment())
                .collect(Collectors.toList());
    }

    public static int getPlayersNumberByIp() {
        return (int) getClients().stream().filter(client -> client != null && client.getAccount() != null)
                .map(client -> client.getAccount().getCurrentIp())
                .distinct().count();
    }

    public void setState(int state) {
        ExchangeClient.INSTANCE.send("SS" + state);
    }

    public static Account getAndDeleteWaitingAccount(int id){
        Iterator<Account> it = waitingClients.listIterator();
        while(it.hasNext()){
            Account account = it.next();
            if(account.getId() == id){
                it.remove();
                return account;
            }
        }
        return null;
    }

    public static void addWaitingAccount(Account account) {
        if(!waitingClients.contains(account)) waitingClients.add(account);
    }

    public static void a() {
        log.warn("Unexpected behaviour detected");
    }

    public void kickAll(boolean kickGm) {
        for (Player player : World.world.getOnlinePlayers()) {
            if (player != null && player.getGameClient() != null) {
                if (player.getGroupe() != null && !player.getGroupe().isPlayer() && kickGm)
                    continue;
                player.send("M04");
                player.getGameClient().kick();
            }
        }
    }
}
