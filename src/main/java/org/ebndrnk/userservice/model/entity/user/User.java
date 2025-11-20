package org.ebndrnk.userservice.model.entity.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.ebndrnk.common.common.entity.BasicEntity;
import org.ebndrnk.userservice.model.entity.card.CardInfo;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
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

    @Comment("The email address of the user. Must be a valid email and between 4 and 50 characters and unique.")
    @Column(name = "email", length = 50, unique = true)
    @NotNull
    @Size(min = 4, max = 50)
    @Email
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CardInfo> cards = new ArrayList<>();

}
