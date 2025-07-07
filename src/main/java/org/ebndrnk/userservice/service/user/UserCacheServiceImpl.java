package org.ebndrnk.userservice.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ebndrnk.userservice.model.dto.user.UserCacheDto;
import org.ebndrnk.userservice.repository.user.UserRedisRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCacheServiceImpl implements UserCacheService {

    private final UserRedisRepository userRedisRepository;

    @Override
    public Optional<UserCacheDto> findById(Long id) {
        try {
            return userRedisRepository.findById(id);
        } catch (Exception e) {
            log.error("Failed to fetch user from Redis", e);
            return Optional.empty();
        }
    }

    @Override
    public void save(UserCacheDto userCacheDto) {
        try {
            userRedisRepository.save(userCacheDto);
        } catch (Exception e) {
            log.error("Failed to save user to Redis", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            userRedisRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Failed to delete user from Redis", e);
        }
    }
}
