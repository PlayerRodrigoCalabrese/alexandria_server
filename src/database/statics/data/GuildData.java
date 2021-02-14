package database.statics.data;

import com.zaxxer.hikari.HikariDataSource;
import database.dynamics.AbstractDAO;
import exchange.transfer.DataType;
import game.world.World;
import guild.Guild;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildData extends AbstractDAO<Guild> {

    public GuildData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
        Result result = null;
        try {
            result = getData("SELECT * FROM `world.entity.guilds` WHERE `id` = " + obj + ";");
            ResultSet RS = result.resultSet;

            while (RS.next())
                World.world.addGuild(new Guild(RS.getInt("id"), RS.getString("name"), RS.getString("emblem"), RS.getInt("lvl"), RS.getLong("xp"), RS.getInt("capital"), RS.getInt("maxCollectors"), RS.getString("spells"), RS.getString("stats"), RS.getLong("date")), false);
        } catch (SQLException e) {
            super.sendError("GuildData load", e);
        } finally {
            close(result);
        }
    }

    @Override
    public boolean update(Guild guild) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `world.entity.guilds` SET `lvl` = ?, `xp` = ?, `capital` = ?, `maxCollectors` = ?, `spells` = ?, `stats` = ? WHERE id = ?;");
            p.setInt(1, guild.getLvl());
            p.setLong(2, guild.getXp());
            p.setInt(3, guild.getCapital());
            p.setInt(4, guild.getNbCollectors());
            p.setString(5, guild.compileSpell());
            p.setString(6, guild.compileStats());
            p.setInt(7, guild.getId());
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("GuildData update", e);
        } finally {
            close(p);
        }
        return false;
    }

    public void add(Guild guild) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("INSERT INTO `world.entity.guilds` VALUES (?,?,?,1,0,0,0,?,?,?);");
            p.setInt(1, guild.getId());
            p.setString(2, guild.getName());
            p.setString(3, guild.getEmblem());
            p.setString(4, "462;0|461;0|460;0|459;0|458;0|457;0|456;0|455;0|454;0|453;0|452;0|451;0|");
            p.setString(5, "176;100|158;1000|124;100|");
            p.setLong(6, guild.getDate());
            execute(p);
        } catch (SQLException e) {
            super.sendError("GuildData add", e);
        } finally {
            close(p);
        }
    }

    public void delete(int id) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM `world.entity.guilds` WHERE `id` = ?;");
            p.setInt(1, id);
            execute(p);
        } catch (SQLException e) {
            super.sendError("GuildData delete", e);
        } finally {
            close(p);
        }
    }

    public int getNextId() {
        //return Database.getStatics().getWorldEntityData().getNextGuildId();
        final DataType<Integer> queue = new DataType<>((byte) 4);
        return queue.getValue();
    }
}
