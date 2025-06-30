package org.ebndrnk.userservice.controller.card;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ebndrnk.userservice.model.dto.card.CardInfoRequest;
import org.ebndrnk.userservice.model.dto.card.CardInfoResponse;
import org.ebndrnk.userservice.service.card.CardInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;



import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardInfoController {

    private final CardInfoService cardInfoService;

    @PostMapping
    public ResponseEntity<CardInfoResponse> createCard(@Valid @RequestBody CardInfoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cardInfoService.createCard(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardInfoResponse> getCardById(@PathVariable Long id) {
        return ResponseEntity.ok(cardInfoService.getCardById(id));
    }

    @GetMapping
    public ResponseEntity<List<CardInfoResponse>> getCardsByIds(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(cardInfoService.getCardsByIds(ids));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardInfoResponse> updateCard(
            @PathVariable Long id,
            @Valid @RequestBody CardInfoRequest request
    ) {
        return ResponseEntity.ok(cardInfoService.updateCard(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardInfoService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}
