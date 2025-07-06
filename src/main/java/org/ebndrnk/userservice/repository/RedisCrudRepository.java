package org.ebndrnk.userservice.repository;

import java.util.Optional;

/**
 * Generic interface defining basic CRUD operations for entities cached in Redis.
 * <p>
 * This interface abstracts the typical cache interactions such as saving an entity,
 * retrieving it by its identifier, and deleting it from the cache.
 * Implementations should handle serialization and deserialization as needed.
 *
 * @param <T> the type of entity to be managed in Redis cache
 */
public interface RedisCrudRepository<T> {

    /**
     * Saves or updates the given entity in Redis cache.
     *
     * @param entity the entity to save or update
     */
    void save(T entity);

    /**
     * Finds an entity by its identifier from Redis cache.
     *
     * @param id the identifier of the entity to find
     * @return an Optional containing the found entity, or empty if not found
     */
    Optional<T> findById(Long id);

    /**
     * Deletes the entity with the given identifier from Redis cache.
     *
     * @param id the identifier of the entity to delete
     */
    void deleteById(Long id);
}
