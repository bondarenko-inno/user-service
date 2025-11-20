package org.ebndrnk.userservice.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ebndrnk.userservice.exception.user.DuplicateEmailException;
import org.ebndrnk.userservice.exception.user.UserNotFoundException;
import org.ebndrnk.userservice.mapper.UserMapper;
import org.ebndrnk.userservice.model.dto.user.UserInfoForOrder;
import org.ebndrnk.userservice.model.dto.user.UserRequest;
import org.ebndrnk.userservice.model.dto.user.UserResponse;
import org.ebndrnk.userservice.model.entity.user.User;
import org.ebndrnk.userservice.repository.card.CardInfoRepository;
import org.ebndrnk.userservice.repository.user.UserRepository;
import org.ebndrnk.userservice.service.card.CardInfoCacheService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserCacheService userCacheService;
    private final UserMapper userMapper;
    private final CardInfoCacheService cardInfoCacheService;
    private final CardInfoRepository cardInfoRepository;

    @Override
    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
        log.info("Creating user with email: {}", userRequest.email());

        if (userRepository.findByEmail(userRequest.email()).isPresent()) {
            log.warn("Duplicate email attempted: {}", userRequest.email());
            throw new DuplicateEmailException("Email already exists: " + userRequest.email());
        }

        User saved = userRepository.save(userMapper.requestToEntity(userRequest));

        userCacheService.save(userMapper.entityToCacheDto(saved));

        UserResponse response = userMapper.entityToResponse(saved);
        log.info("User created with id: {}", response.id());
        return response;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::entityToResponse).collect(Collectors.toList());
    }


    @Override
    public UserResponse getUserById(Long id) {
        log.info("Fetching user by id: {}", id);

        return userCacheService.findById(id)
                .map(userMapper::cacheDtoToResponse)
                .or(() -> userRepository.findById(id).map(user -> {
                    userCacheService.save(userMapper.entityToCacheDto(user));
                    return userMapper.entityToResponse(user);
                }))
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new UserNotFoundException("User with id " + id + " not found");
                });
    }

    @Override
    public List<UserResponse> getUsersById(List<Long> ids) {
        log.info("Fetching users by ids: {}", ids);

        if (ids == null || ids.isEmpty()) {
            log.warn("Empty or null list of ids provided to getCardsByIds");
            throw new IllegalArgumentException("List of IDs must not be empty");
        }

        List<User> users = userRepository.findAllById(ids);
        if (users.isEmpty()) {
            log.error("No users found for given ids: {}", ids);
            throw new UserNotFoundException("No users found for given ids: " + ids);
        }

        users.forEach(user -> userCacheService.save(userMapper.entityToCacheDto(user)));

        return users.stream().map(userMapper::entityToResponse).toList();
    }

    @Override
    public UserInfoForOrder getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));

        boolean hasCards = userRepository.hasCardsByEmail(email);

        UserInfoForOrder userInfoForOrder = userMapper.entityToUserInfo(user);
        userInfoForOrder.setIsCardAvailable(hasCards);
        return userInfoForOrder;
    }


    @Override
    public boolean isExistByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        log.info("Updating user with id: {}", id);

        User existing = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + id));

        if (!existing.getEmail().equals(userRequest.email())) {
            userRepository.findByEmail(userRequest.email())
                    .ifPresent(foundUser -> {
                        throw new DuplicateEmailException("Email already exists: " + userRequest.email());
                    });
        }

        userMapper.update(existing, userRequest);
        User saved = userRepository.save(existing);

        userCacheService.save(userMapper.entityToCacheDto(saved));

        UserResponse response = userMapper.entityToResponse(saved);
        log.info("User updated with id: {}", response.id());
        return response;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);

        if (!userRepository.existsById(id)) {
            log.error("User not found for deletion with id: {}", id);
            throw new UserNotFoundException("User not found for id: " + id);
        }

        userCacheService.deleteById(id);
        cardInfoRepository.findByUserId(id).forEach(cardInfo -> cardInfoCacheService.deleteById(cardInfo.getId()));


        userRepository.deleteById(id);

        log.info("User deleted with id: {}", id);
    }

    @Override
    public User getEntityById(Long id) {
        log.info("Fetching user entity by id: {}", id);

        return userRepository.findById(id).orElseThrow(() -> {
            log.error("User entity not found with id: {}", id);
            return new UserNotFoundException("User not found with id: " + id);
        });
    }
}
