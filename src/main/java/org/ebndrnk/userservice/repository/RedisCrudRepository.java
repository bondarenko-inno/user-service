package org.ebndrnk.userservice.repository;

import java.util.Optional;

public interface RedisCrudRepository<T> {

    void save(T entity);

    Optional<T> findById(Long id);

    public void deleteById(Long id);
}
