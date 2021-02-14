package database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import area.map.GameMap;
import area.map.entity.MountPark;
import database.dynamics.AbstractDAO;
import game.world.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MountParkData extends AbstractDAO<MountPark> {
    public MountParkData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(MountPark MP) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `mountpark_data` SET  `owner`=?, `guild`=?, `price` =?, `data` =?, `enclos` =?, `ObjetPlacer`=?, `durabilite`=? WHERE `mapid`=?");
            p.setInt(1, MP.getOwner());
            p.setInt(2, (MP.getGuild() != null) ? MP.getGuild().getId() : -1);
            p.setInt(3, MP.getPrice());
            p.setString(4, MP.parseEtableToString());
            p.setString(5, MP.parseRaisingToString());
            p.setString(6, MP.getStringObject());
            p.setString(7, MP.getStringObjDurab());
            p.setInt(8, MP.getMap().getId());
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("Mountpark_dataData update", e);
        } finally {
            close(p);
        }
        return false;
    }

    public int load() {
        Result result = null;
        int nbr = 0;
        try {
            result = getData("SELECT * from mountpark_data");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                GameMap map = World.world.getMap(RS.getShort("mapid"));
                if (map == null)
                    continue;

                MountPark MP = World.world.getMountPark().get(map.getId());
                if (MP == null)
                    continue;

                int owner = RS.getInt("owner");
                int guild = RS.getInt("guild");
                guild = World.world.getGuild(guild) != null ? guild : -1;
                int price = RS.getInt("price");
                String data = RS.getString("data");
                String enclos = RS.getString("enclos");
                String objetPlacer = RS.getString("ObjetPlacer");
                String durabilite = RS.getString("durabilite");

                enclos = enclos.equals(" ") ? "" : enclos;
                objetPlacer = objetPlacer.equals(" ") ? "" : objetPlacer;
                durabilite = durabilite.equals(" ") ? "" : durabilite;

                MP.setData(owner, guild, price, data, objetPlacer, durabilite, enclos);
                nbr++;
            }
        } catch (SQLException e) {
            super.sendError("Mountpark_dataData load", e);
        } finally {
            close(result);
        }
        return nbr;
    }

    public void exist(MountPark mountPark) {
        Result result = null;
        try {
            result = getData("SELECT * FROM `mountpark_data` WHERE `mapid` = '" + mountPark.getMap().getId() + "';");
            ResultSet RS = result.resultSet;
            if(!RS.next()) {
                this.insert(mountPark);
            }
        } catch (SQLException e) {
            super.sendError("Mountpark_dataData load", e);
        } finally {
            close(result);
        }
    }

    public void insert(MountPark mountPark) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("INSERT INTO `mountpark_data` (`mapid`, `owner`, `guild`, `price`, `data`, `enclos`, `ObjetPlacer`, `durabilite`) " +
                    "VALUES (?, ?, ?, ?, '', '', '', '')");
            p.setInt(1, mountPark.getMap().getId());
            p.setInt(2, 0);
            p.setInt(3, -1);
            p.setInt(4, mountPark.getPriceBase());
            execute(p);
        } catch (SQLException e) {
            super.sendError("Mountpark insert", e);
        } finally {
            close(p);
        }
    }

    public void reload(int i) {
        Result result = null;
        try {
            result = getData("SELECT * from mountpark_data");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                GameMap map = World.world.getMap(RS.getShort("mapid"));
                if (map == null)
                    continue;
                if (RS.getShort("mapid") != i)
                    continue;

                MountPark MP = World.world.getMountPark().get(map.getId());
                if (MP == null)
                    continue;
                int owner = RS.getInt("owner");
                int guild = RS.getInt("guild");
                int price = RS.getInt("price");
                String data = RS.getString("data");
                String enclos = RS.getString("enclos");
                String objetPlacer = RS.getString("ObjetPlacer");
                String durabilite = RS.getString("durabilite");
                MP.setData(owner, guild, price, data, objetPlacer, durabilite, enclos);
            }
        } catch (SQLException e) {
            super.sendError("Mountpark_dataData reload", e);
        } finally {
            close(result);
        }
    }
}
