package exchange;

import client.Account;
import command.CommandPlayer;
import database.Database;
import exchange.transfer.DataType;
import game.GameServer;
import game.world.World;
import kernel.Config;
import kernel.Main;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExchangePacketHandler {

    public static void parser(String recv) {
        for(String packet : recv.split("#")) {
            if(packet.isEmpty()) continue;
            try {
                switch (packet.charAt(0)) {
                    case 'F': //Free places
                        switch (packet.charAt(1)) {
                            case '?': //Required
                                int i = GameServer.MAX_PLAYERS - World.world.getOnlinePlayers().size();
                                ExchangeClient.INSTANCE.send("F" + i);
                                break;
                        }
                        break;

                    case 'S': //Server
                        switch (packet.charAt(1)) {
                            case 'H': //Host
                                switch (packet.charAt(2)) {
                                    case 'K': //Ok
                                        ExchangeClient.logger.info("The login server has validated the connection.");
                                        GameServer.INSTANCE.setState(1);
                                        break;
                                }
                                break;

                            case 'K': //Key
                                switch (packet.charAt(2)) {
                                    case '?': //Required
                                        int i = 50000 - GameServer.getClients().size();
                                        ExchangeClient.INSTANCE.send("SK" + Config.INSTANCE.getSERVER_ID() + ";" + Config.INSTANCE.getSERVER_KEY() + ";" + i);
                                        break;

                                    case 'K': //Ok
                                        ExchangeClient.logger.info("The login server has accepted the connection.");
                                        ExchangeClient.INSTANCE.send("SH" + Config.INSTANCE.getIp() + ";" + Config.INSTANCE.getGamePort());
                                        break;

                                    case 'R': //Refused
                                        ExchangeClient.logger.info("The login server has refused the connection.");
                                        Main.INSTANCE.stop("Connection refused by the login");
                                        break;
                                }
                                break;
                        }
                        break;

                    case 'W': //Waiting
                        switch (packet.charAt(1)) {
                            case 'A': //Add
                                int id = Integer.parseInt(packet.substring(2));
                                Account account = World.world.getAccount(id);

                                if (account == null) {
                                    Database.getStatics().getAccountData().load(id);
                                    account = World.world.getAccount(id);
                                }

                                if (account != null) {
                                    if (account.getCurrentPlayer() != null)
                                        account.getGameClient().kick();
                                    account.setSubscribe();
                                    GameServer.addWaitingAccount(account);
                                }
                                break;
                            case 'K': //Kick
                                id = Integer.parseInt(packet.substring(2));
                                Database.getStatics().getPlayerData().updateAllLogged(id, 0);
                                Database.getStatics().getAccountData().setLogged(id, 0);
                                account = World.world.getAccount(id);

                                if (account != null)
                                    if (account.getGameClient() != null)
                                        account.getGameClient().kick();
                                break;
                        }
                        break;

                    case 'D': // Data
                        switch (packet.charAt(1)) {
                            case 'I': // Id
                                packet = packet.substring(2);

                                for (String data : packet.split("DI")) {
                                    String[] split = data.split(";");
                                    long count = Long.parseLong(split[0].substring(1));
                                    DataType<?> queue = World.world.getDataQueue().getQueue().get(count);

                                    switch (Byte.parseByte(String.valueOf(data.charAt(0)))) {
                                        case 1: // Mount
                                        case 2: // Object
                                        case 3: // QuestPlayer
                                        case 4: // Guild
                                        case 5: // Pet
                                            if (split.length > 1)
                                                ((DataType<Integer>) queue).setValue(Integer.parseInt(split[1]));
                                            break;
                                    }
                                }
                                break;

                            case 'M': // Message
                                String[] split = packet.substring(2).split("\\|");
                                if(split.length > 1) {
                                    String prefix = "<font color='#C35617'>[" + (new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis()))) + "] (" + CommandPlayer.canal + ") (" + split[1] + ") <b>" + split[0] + "</b>";
                                    final String message = "Im116;" + prefix + "~" + split[2] + "</font>";

                                    World.world.getOnlinePlayers().stream().filter(p -> p != null && !p.noall).forEach(p -> p.send(message.replace("%20", " ")));
                                }
                                break;
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
