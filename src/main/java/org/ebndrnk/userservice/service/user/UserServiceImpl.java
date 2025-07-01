package org.ebndrnk.userservice.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ebndrnk.userservice.exception.dto.user.DuplicateEmailException;
import org.ebndrnk.userservice.exception.dto.user.UserNotFoundException;
import org.ebndrnk.userservice.mapper.UserMapper;
import org.ebndrnk.userservice.model.dto.user.UserRequest;
import org.ebndrnk.userservice.model.dto.user.UserResponse;
import org.ebndrnk.userservice.model.entity.user.User;
import org.ebndrnk.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        log.info("Creating user with email: {}", userRequest.email());

        if (userRepository.findByEmail(userRequest.email()).isPresent()) {
            log.warn("Duplicate email attempted: {}", userRequest.email());
            throw new DuplicateEmailException(userRequest.email());
        }

        UserResponse response = userMapper.toDto(userRepository.save(userMapper.toEntity(userRequest)));
        log.info("User created with id: {}", response.id());
        return response;
    }

    @Override
    public UserResponse getUserById(Long id) {
        log.info("Fetching user by id: {}", id);

        User user = userRepository.findById(id).orElseThrow(() -> {
            log.error("User not found with id: {}", id);
            return new UserNotFoundException(String.format("User with id '%s' not found", id));
        });

        return userMapper.toDto(user);
    }

    @Override
    public List<UserResponse> getUsersById(List<Long> ids) {
        log.info("Fetching users by ids: {}", ids);

        List<User> users = userRepository.findAllById(ids);
        if (users.isEmpty()) {
            log.error("No users found for given ids: {}", ids);
            throw new UserNotFoundException("No users found for given ids: " + ids);
        }

        return users.stream().map(userMapper::toDto).toList();
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);

        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElse(null);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        log.info("Updating user with id: {}", id);

        User existing = userRepository.findById(id).orElseThrow(() -> {
            log.error("User not found for update with id: {}", id);
            return new UserNotFoundException("User not found for id: " + id);
        });

        userMapper.update(existing, userRequest);

        UserResponse response = userMapper.toDto(userRepository.save(existing));
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
