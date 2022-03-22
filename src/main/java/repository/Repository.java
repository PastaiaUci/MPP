package repository;

import java.util.Collection;

public interface Repository<Tid, T> {
    int size();
    void save(T entity);
    void delete(Tid id);

    //void update(Tid id, T entity);
    T findOne(Tid id);
    Iterable<T> findAll();
}