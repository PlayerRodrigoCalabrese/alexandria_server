package database;

import com.zaxxer.hikari.HikariDataSource;
import database.dynamics.DynamicsDatabase;
import database.statics.StaticsDatabase;

import java.sql.Connection;

public class Database {
    private final static DynamicsDatabase dynamics = new DynamicsDatabase();
    private final static StaticsDatabase statics = new StaticsDatabase();

    public static boolean launchDatabase() {
        return !(!statics.initializeConnection() || !dynamics.initializeConnection());
    }

    public static DynamicsDatabase getDynamics() {
        return dynamics;
    }

    public static StaticsDatabase getStatics() {
        return statics;
    }

    public static boolean tryConnection(HikariDataSource dataSource) {
        try {
            Connection connection = dataSource.getConnection();
            connection.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
