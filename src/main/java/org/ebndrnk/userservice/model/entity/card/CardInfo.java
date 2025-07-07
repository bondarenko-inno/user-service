package org.ebndrnk.userservice.model.entity.card;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ebndrnk.userservice.model.entity.BasicEntity;
import org.ebndrnk.userservice.model.entity.user.User;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Table(name = "card_info")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardInfo extends BasicEntity {
    @ManyToOne
    @JoinColumn(name = "user_id",
            foreignKey = @ForeignKey(name = "fk_card_user"))
    @NotNull
    @Comment("Reference to card holder")
    @JsonBackReference
    private User user;

    @Column(name = "number", length = 16)
    @NotNull
    @Pattern(regexp = "^[0-9]{16}$", message = "Card number must be 16 digits")
    @Comment("16-digits card number")
    private String number;

    @Column(name = "holder")
    @NotNull
    @Comment("Name of card holder")
    private String holder;

    @Column(name = "expiration_date")
    @NotNull
    @Comment("Card expiration date")
    private LocalDateTime expirationDate;
}
