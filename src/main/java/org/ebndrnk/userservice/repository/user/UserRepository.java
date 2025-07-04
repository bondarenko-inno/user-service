package org.ebndrnk.userservice.repository.user;

import org.ebndrnk.userservice.model.entity.card.CardInfo;
import org.ebndrnk.userservice.model.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.id IN :ids")
    List<User> findAllByIds(@Param("ids") List<Long> ids);

}




