package database;

public interface DAO<T> {
    void load(Object obj);

    boolean update(T obj);
}