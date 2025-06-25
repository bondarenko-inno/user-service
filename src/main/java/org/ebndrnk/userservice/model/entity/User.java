package org.ebndrnk.userservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_user")
@Getter
@Setter
public class User extends BasicEntity {

    @Comment("Name of user, will be used in column card_info.holder")
    @Column(name = "name", length = 50)
    @NotNull
    @Size(min = 2, max = 50)
    private String name;

    @Comment("Name of user, will be used in column card_info.holder")
    @Column(name = "surname", length = 50)
    @NotNull
    @Size(min = 2, max = 50)
    private String surname;

    @Comment("Format ISO 8601: YYYY-MM-DD hh:mm:ss.000000")
    @Column(name = "birth_date")
    @NotNull
    private LocalDateTime birthDate;

    @Comment("The email address of the user. Must be a valid email and between 4 and 50 characters.")
    @Column(name = "email", length = 50)
    @NotNull
    @Size(min = 4, max = 50)
    @Email
    private String email;

}
