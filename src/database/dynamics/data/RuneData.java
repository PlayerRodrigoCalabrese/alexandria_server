package database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import database.dynamics.AbstractDAO;
import job.maging.Rune;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RuneData extends AbstractDAO<Rune> {
    public RuneData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
        Result result = null;
        try {
            result = getData("SELECT * FROM runes");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                new Rune(RS.getShort("id"), RS.getFloat("weight"), RS.getByte("bonus"));
            }
        } catch (SQLException e) {
            super.sendError("RuneData load", e);
        } finally {
            close(result);
        }
    }

    @Override
    public boolean update(Rune obj) {
        return false;
    }
}
