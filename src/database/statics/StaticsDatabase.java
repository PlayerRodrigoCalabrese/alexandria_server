package database.statics;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.LoggerFactory;
import database.Database;
import database.statics.data.*;
import kernel.Config;
import kernel.Main;

public class StaticsDatabase {
    //connection
    private HikariDataSource dataSource;
    private Logger logger = (Logger) LoggerFactory.getLogger(StaticsDatabase.class);
    //data
    private AccountData accountData;
    private AreaData areaData;
    private BanIpData banIpData;
    private CommandData commandData;
    private EventData eventData;
    private GroupData groupData;
    private GuildData guildData;
    private HouseData houseData;
    private MountData mountData;
    private MountParkData mountParkData;
    private ObjectData objectData;
    private ObvejivanData obvejivanData;
    private PetData petData;
    private PlayerData playerData;
    private PubData pubData;
    private QuestPlayerData questPlayerData;
    private ServerData serverData;
    private SubAreaData subAreaData;
    private TrunkData trunkData;
    private WorldEntityData worldEntityData;

    private void initializeData() {
        this.accountData = new AccountData(this.dataSource);
        this.commandData = new CommandData(this.dataSource);
        this.eventData = new EventData(this.dataSource);
        this.playerData = new PlayerData(this.dataSource);
        this.serverData = new ServerData(this.dataSource);
        this.banIpData = new BanIpData(this.dataSource);
        this.areaData = new AreaData(this.dataSource);
        this.subAreaData = new SubAreaData(this.dataSource);
        this.guildData = new GuildData(this.dataSource);
        this.groupData = new GroupData(this.dataSource);
        this.houseData = new HouseData(this.dataSource);
        this.trunkData = new TrunkData(this.dataSource);
        this.mountData = new MountData(this.dataSource);
        this.mountParkData = new MountParkData(this.dataSource);
        this.objectData = new ObjectData(this.dataSource);
        this.obvejivanData = new ObvejivanData(this.dataSource);
        this.pubData = new PubData(this.dataSource);
        this.petData = new PetData(this.dataSource);
        this.questPlayerData = new QuestPlayerData(this.dataSource);
        this.worldEntityData = new WorldEntityData(this.dataSource);
    }

    public boolean initializeConnection() {
        try {
            logger.setLevel(Level.ALL);
            logger.trace("Reading database config");

            HikariConfig config = new HikariConfig();
            config.setDataSourceClassName("org.mariadb.jdbc.MySQLDataSource");
            config.addDataSourceProperty("serverName", Config.INSTANCE.getLoginHostDB());
            config.addDataSourceProperty("port", Config.INSTANCE.getLoginPortDB());
            config.addDataSourceProperty("databaseName", Config.INSTANCE.getLoginNameDB());
            config.addDataSourceProperty("user", Config.INSTANCE.getLoginUserDB());
            config.addDataSourceProperty("password", Config.INSTANCE.getLoginPassDB());
            config.setAutoCommit(true); // AutoCommit, c'est cool
            config.setMaximumPoolSize(20);
            config.setMinimumIdle(1);
            this.dataSource = new HikariDataSource(config);

            if (!Database.tryConnection(this.dataSource)) {
                logger.error("Please verify your username and password and database connection");
                Main.INSTANCE.stop("statics try connection failed");
                return false;
            }
            logger.info("Database connection established");
            initializeData();
            logger.info("Database data loaded");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    public AccountData getAccountData() {
        return accountData;
    }

    public CommandData getCommandData() {
        return commandData;
    }

    public EventData getEventData() {
        return eventData;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public ServerData getServerData() {
        return serverData;
    }

    public BanIpData getBanIpData() {
        return banIpData;
    }

    public AreaData getAreaData() {
        return areaData;
    }

    public SubAreaData getSubAreaData() {
        return subAreaData;
    }

    public GuildData getGuildData() {
        return guildData;
    }

    public GroupData getGroupData() {
        return groupData;
    }

    public HouseData getHouseData() {
        return houseData;
    }

    public TrunkData getTrunkData() {
        return trunkData;
    }

    public MountData getMountData() {
        return mountData;
    }

    public MountParkData getMountParkData() {
        return mountParkData;
    }

    public ObjectData getObjectData() {
        return objectData;
    }

    public ObvejivanData getObvejivanData() {
        return obvejivanData;
    }

    public PubData getPubData() {
        return pubData;
    }

    public PetData getPetData() {
        return petData;
    }

    public QuestPlayerData getQuestPlayerData() {
        return questPlayerData;
    }

    public WorldEntityData getWorldEntityData() {
        return worldEntityData;
    }
}
