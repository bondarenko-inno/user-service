package org.ebndrnk.userservice.service.user;

import org.ebndrnk.userservice.model.dto.user.UserCacheDto;

import java.util.Optional;

/**
 * Service interface for managing user cache data.
 * <p>
 * Provides methods to find, save, and delete cached user information.
 */
public interface UserCacheService {

    /**
     * Finds cached user information by user ID.
     *
     * @param id the unique identifier of the user
     * @return an Optional containing the cached user DTO if found, or empty otherwise
     */
    Optional<UserCacheDto> findById(Long id);

    /**
     * Saves or updates user cache information.
     *
     * @param userCacheDto the DTO containing user cache data to save
     */
    void save(UserCacheDto userCacheDto);

    /**
     * Deletes cached user information by user ID.
     *
     * @param id the unique identifier of the user cache to delete
     */
    void deleteById(Long id);
}
