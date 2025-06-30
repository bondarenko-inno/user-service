package org.ebndrnk.userservice.repository;

import org.ebndrnk.userservice.model.entity.card.CardInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {

    List<CardInfo> findByIdIn(List<Long> ids);

    List<CardInfo> findByUserId(Long userId);

    Optional<CardInfo> findByNumber(String cardNumber);

}
