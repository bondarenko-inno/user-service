package org.ebndrnk.userservice.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Table
@Entity
@Getter
@Setter
public class CardInfo extends BasicEntity{
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    @Comment("Reference to card holder")
    private User user;

    @Column(name = "number", nullable = false, length = 16)
    @NotNull
    @Pattern(regexp = "\\d{16}", message = "Card number must be exactly 16 digits")
    @Comment("16-digits card number")
    private String number;

    @Column(name ="holder", nullable = false)
    @NotNull
    @Comment("Name of card holder")
    private String holder;

    @Column(name = "expiration_date")
    @NotNull
    @Comment("Card expiration date")
    private LocalDateTime expirationDate;
}
