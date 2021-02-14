package kernel

object Config {

    val startTime = System.currentTimeMillis()
    var HALLOWEEN = ConfigReader.data[ConfigReader.mode.halloween]
    var NOEL = ConfigReader.data[ConfigReader.mode.christmas]
    var HEROIC = ConfigReader.data[ConfigReader.mode.heroic]
    var TEAM_MATCH = ConfigReader.data[ConfigReader.options.teamMatch]
    var DEATH_MATCH = ConfigReader.data[ConfigReader.options.deathMatch]
    var AUTO_EVENT = ConfigReader.data[ConfigReader.options.event.active]
    var AUTO_REBOOT = ConfigReader.data[ConfigReader.options.autoReboot]
    var ALL_ZAAP = ConfigReader.data[ConfigReader.options.allZaap]
    var ALL_EMOTE = ConfigReader.data[ConfigReader.options.allEmote]

    var isSaving = false
    var isRunning = false

    var ENCRYPT_PACKET = ConfigReader.data[ConfigReader.options.encryptPacket]
    var TIME_PER_EVENT: Short = ConfigReader.data[ConfigReader.options.event.timePerEvent].toShort()

    var NAME: String = "StarLoco"
    var url: String = ""
    var startMessage = "Bienvenue sur le serveur $NAME !"
    var colorMessage = "B9121B"

    var START_MAP = ConfigReader.data[ConfigReader.options.start.map]
    var START_CELL = ConfigReader.data[ConfigReader.options.start.cell]
    var RATE_KAMAS = ConfigReader.data[ConfigReader.rate.kamas]
    var RATE_DROP = ConfigReader.data[ConfigReader.rate.farm]
    var RATE_HONOR = ConfigReader.data[ConfigReader.rate.honor]
    var RATE_JOB = ConfigReader.data[ConfigReader.rate.job]
    var RATE_XP = ConfigReader.data[ConfigReader.rate.xp]

    var exchangePort: Int = ConfigReader.data[ConfigReader.exchange.port]
    var gamePort: Int = ConfigReader.data[ConfigReader.server.port]
    var exchangeIp: String = ConfigReader.data[ConfigReader.exchange.host]
    var loginHostDB: String = ConfigReader.data[ConfigReader.database.login.host]
    var loginPortDB: Int = ConfigReader.data[ConfigReader.database.login.port]
    var loginNameDB: String = ConfigReader.data[ConfigReader.database.login.name]
    var loginUserDB: String = ConfigReader.data[ConfigReader.database.login.user]
    var loginPassDB: String = ConfigReader.data[ConfigReader.database.login.pass]
    var hostDB: String? = ConfigReader.data[ConfigReader.database.game.host]
    var portDB: Int = ConfigReader.data[ConfigReader.database.game.port]
    var nameDB: String? = ConfigReader.data[ConfigReader.database.game.name]
    var userDB: String? = ConfigReader.data[ConfigReader.database.game.user]
    var passDB: String? = ConfigReader.data[ConfigReader.database.game.pass]
    var ip: String? = ConfigReader.data[ConfigReader.server.host]

    var SERVER_ID: Int = ConfigReader.data[ConfigReader.server.id]
    var SERVER_KEY: String = ConfigReader.data[ConfigReader.server.key]
    var subscription = ConfigReader.data[ConfigReader.options.subscription]

    var startKamas: Long = ConfigReader.data[ConfigReader.options.start.kamas]
    var startLevel: Int = ConfigReader.data[ConfigReader.options.start.level]



}