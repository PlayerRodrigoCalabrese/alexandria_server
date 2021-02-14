package database.statics.data;

import com.zaxxer.hikari.HikariDataSource;
import database.statics.AbstractDAO;
import object.GameObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObvejivanData extends AbstractDAO<GameObject> {

    public ObvejivanData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {}

    @Override
    public boolean update(GameObject obj) {
        return false;
    }

    public void add(GameObject obvijevan, GameObject object) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("INSERT INTO `world.entity.obvijevans`(`id`, `template`) VALUES(?, ?);");
            p.setInt(1, object.getGuid());
            p.setInt(2, obvijevan.getTemplate().getId());
            execute(p);
        } catch (Exception e) {
            super.sendError("ObvejivanData add", e);
        } finally {
            close(p);
        }
    }

    public int getAndDelete(GameObject object, boolean delete) {
        Result result = null;
        int template = -1;
        try {
            result = getData("SELECT * FROM `world.entity.obvijevans` WHERE `id` = '" + object.getGuid() + "';");
            ResultSet resultSet = result.resultSet;

            if (resultSet.next()) {
                template = resultSet.getInt("template");
                if (delete) {
                    PreparedStatement ps = getPreparedStatement("DELETE FROM `world.entity.obvijevans` WHERE id = '" + object.getGuid() + "';");
                    execute(ps);
                }
            }
        } catch (SQLException e) {
            super.sendError("ObvejivanData getAndDelete", e);
        } finally {
            close(result);
        }
        return template;
    }
}
