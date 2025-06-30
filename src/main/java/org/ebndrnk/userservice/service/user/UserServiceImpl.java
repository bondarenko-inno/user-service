package org.ebndrnk.userservice.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        if (userRepository.findByEmail(userRequest.email()).isPresent()) {
            throw new DuplicateEmailException(userRequest.email());
        }

        return userMapper.toDto(userRepository.save(userMapper.toEntity(userRequest)));
    }


    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException(String.format("User with id '%s' not found", id)));
        return userMapper.toDto(user);
    }

    @Override
    public List<UserResponse> getUsersById(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        if (users.isEmpty()) {
            throw new UserNotFoundException("No users found for given ids: " + ids);
        }
        return users.stream().map(userMapper::toDto).toList();
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElse(null);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + id));
        userMapper.update(existing, userRequest);
        return userMapper.toDto(userRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found for id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public User getEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }
}
