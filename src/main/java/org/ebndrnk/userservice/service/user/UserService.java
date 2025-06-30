package org.ebndrnk.userservice.service.user;

import org.ebndrnk.userservice.model.dto.user.UserRequest;
import org.ebndrnk.userservice.model.dto.user.UserResponse;
import org.ebndrnk.userservice.model.entity.user.User;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest userRequest);

    UserResponse getUserById(Long id);

    List<UserResponse> getUsersById(List<Long> ids);

    UserResponse getUserByEmail(String email);

    UserResponse updateUser(Long id, UserRequest userRequest);

    void deleteUser(Long id);

    User getEntityById(Long id);
}
