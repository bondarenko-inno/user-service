package org.ebndrnk.userservice.unit.service.card;

import org.ebndrnk.userservice.exception.card.CardInfoNotFoundException;
import org.ebndrnk.userservice.exception.card.DuplicateCardNumberException;
import org.ebndrnk.userservice.exception.card.ExpiredCardException;
import org.ebndrnk.userservice.mapper.CardInfoMapper;
import org.ebndrnk.userservice.model.dto.card.CardInfoCacheDto;
import org.ebndrnk.userservice.model.dto.card.CardInfoRequest;
import org.ebndrnk.userservice.model.dto.card.CardInfoResponse;
import org.ebndrnk.userservice.model.entity.card.CardInfo;
import org.ebndrnk.userservice.model.entity.user.User;
import org.ebndrnk.userservice.repository.card.CardInfoRepository;
import org.ebndrnk.userservice.service.card.CardInfoCacheServiceImpl;
import org.ebndrnk.userservice.service.card.CardInfoServiceImpl;
import org.ebndrnk.userservice.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CardInfoServiceImpl}.
 */
class CardInfoServiceImplTest {

    private CardInfoRepository cardInfoRepository;
    private UserServiceImpl userService;
    private CardInfoMapper cardInfoMapper;
    private CardInfoCacheServiceImpl cardInfoCacheService;

    private CardInfoServiceImpl cardInfoService;

    @BeforeEach
    void setUp() {
        cardInfoRepository = mock(CardInfoRepository.class);
        userService = mock(UserServiceImpl.class);
        cardInfoMapper = mock(CardInfoMapper.class);
        cardInfoCacheService = mock(CardInfoCacheServiceImpl.class);

        cardInfoService = new CardInfoServiceImpl(
                cardInfoRepository,
                userService,
                cardInfoMapper,
                cardInfoCacheService
        );
    }

    /**
     * Tests successful creation of a card.
     */
    @Test
    void createCard_success() {
        CardInfoRequest request = new CardInfoRequest(
                "1234567812345678",
                LocalDateTime.now().plusYears(1),
                "John Doe",
                1L
        );

        var user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");
        user.setSurname("Doe");
        user.setBirthDate(LocalDateTime.now());

        CardInfo entity = new CardInfo();
        entity.setId(10L);
        entity.setNumber(request.number());
        entity.setHolder(request.holder());
        entity.setExpirationDate(request.expirationDate());
        entity.setUser(user);

        CardInfoResponse response = new CardInfoResponse(
                10L,
                "1234567812345678",
                "John Doe",
                request.expirationDate(),
                1L
        );

        when(cardInfoRepository.findByNumber(request.number())).thenReturn(Optional.empty());
        when(cardInfoMapper.toEntity(request)).thenReturn(entity);
        when(cardInfoRepository.save(entity)).thenReturn(entity);
        when(cardInfoMapper.toCacheDto(entity)).thenReturn(new CardInfoCacheDto(
                entity.getId(),
                entity.getNumber(),
                entity.getHolder(),
                entity.getExpirationDate(),
                entity.getUser().getId()
        ));
        when(cardInfoMapper.toDto(entity)).thenReturn(response);

        CardInfoResponse result = cardInfoService.createCard(request);

        assertThat(result).isEqualTo(response);
        verify(cardInfoRepository).save(entity);
        verify(cardInfoCacheService).save(any(CardInfoCacheDto.class));
    }

    /**
     * Tests that creating a card with a duplicate number throws {@link DuplicateCardNumberException}.
     */
    @Test
    void createCard_duplicateNumber_throwsException() {
        CardInfoRequest request = new CardInfoRequest(
                "1234567812345678",
                LocalDateTime.now().plusYears(1),
                "John Doe",
                1L
        );

        when(cardInfoRepository.findByNumber(request.number()))
                .thenReturn(Optional.of(new CardInfo()));

        assertThatThrownBy(() -> cardInfoService.createCard(request))
                .isInstanceOf(DuplicateCardNumberException.class);
    }

    /**
     * Tests that creating a card with an expired date throws {@link ExpiredCardException}.
     */
    @Test
    void createCard_expiredDate_throwsException() {
        CardInfoRequest request = new CardInfoRequest(
                "1234567812345678",
                LocalDateTime.now().minusDays(1),
                "John Doe",
                1L
        );

        assertThatThrownBy(() -> cardInfoService.createCard(request))
                .isInstanceOf(ExpiredCardException.class);
    }

    /**
     * Tests successful retrieval of a card by ID from the cache.
     */
    @Test
    void getCardById_fromCache_success() {
        Long id = 10L;

        CardInfoCacheDto cacheDto = new CardInfoCacheDto(
                id,
                "1234567812345678",
                "John Doe",
                LocalDateTime.now().plusYears(1),
                1L
        );

        CardInfoResponse dto = new CardInfoResponse(
                id,
                "1234567812345678",
                "John Doe",
                cacheDto.expirationDate(),
                1L
        );

        when(cardInfoCacheService.findById(id))
                .thenReturn(Optional.of(cacheDto));

        when(cardInfoMapper.toDto(cacheDto))
                .thenReturn(dto);

        CardInfoResponse result = cardInfoService.getCardById(id);

        assertThat(result).isEqualTo(dto);
        verify(cardInfoRepository, never()).findById(id);
    }

    /**
     * Tests successful retrieval of a card by ID from the database when not found in cache.
     */
    @Test
    void getCardById_fromDatabase_success() {
        Long id = 10L;

        var user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");
        user.setSurname("Doe");
        user.setBirthDate(LocalDateTime.now());

        CardInfo entity = new CardInfo();
        entity.setId(id);
        entity.setNumber("1234567812345678");
        entity.setHolder("John Doe");
        entity.setExpirationDate(LocalDateTime.now().plusYears(1));
        entity.setUser(user);

        CardInfoCacheDto cacheDto = new CardInfoCacheDto(
                id,
                entity.getNumber(),
                entity.getHolder(),
                entity.getExpirationDate(),
                entity.getUser().getId()
        );

        CardInfoResponse dto = new CardInfoResponse(
                id,
                entity.getNumber(),
                entity.getHolder(),
                entity.getExpirationDate(),
                entity.getUser().getId()
        );

        when(cardInfoCacheService.findById(id))
                .thenReturn(Optional.empty());

        when(cardInfoRepository.findById(id))
                .thenReturn(Optional.of(entity));

        when(cardInfoMapper.toCacheDto(entity)).thenReturn(cacheDto);
        when(cardInfoMapper.toDto(entity)).thenReturn(dto);

        CardInfoResponse result = cardInfoService.getCardById(id);

        assertThat(result).isEqualTo(dto);
        verify(cardInfoCacheService).save(cacheDto);
    }

    /**
     * Tests that getting a card by a non-existing ID throws {@link CardInfoNotFoundException}.
     */
    @Test
    void getCardById_notFound_throwsException() {
        Long id = 99L;

        when(cardInfoCacheService.findById(id))
                .thenReturn(Optional.empty());
        when(cardInfoRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardInfoService.getCardById(id))
                .isInstanceOf(CardInfoNotFoundException.class);
    }

    /**
     * Tests successful card update.
     */
    @Test
    void updateCard_success() {
        Long id = 10L;
        CardInfoRequest request = new CardInfoRequest(
                "9999999911111111",
                LocalDateTime.now().plusYears(1),
                "Jane Doe",
                2L
        );

        var user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");
        user.setSurname("Doe");
        user.setBirthDate(LocalDateTime.now());

        CardInfo existing = new CardInfo();
        existing.setId(id);
        existing.setNumber("1234567812345678");
        existing.setHolder("John Doe");
        existing.setExpirationDate(LocalDateTime.now().plusYears(2));
        existing.setUser(user);

        User newUser = user;

        CardInfo updated = new CardInfo();
        updated.setId(id);
        updated.setNumber(request.number());
        updated.setHolder(request.holder());
        updated.setExpirationDate(request.expirationDate());
        updated.setUser(newUser);

        CardInfoCacheDto cacheDto = new CardInfoCacheDto(
                id,
                updated.getNumber(),
                updated.getHolder(),
                updated.getExpirationDate(),
                updated.getUser().getId()
        );

        CardInfoResponse response = new CardInfoResponse(
                id,
                request.number(),
                request.holder(),
                request.expirationDate(),
                2L
        );

        when(cardInfoRepository.findById(id)).thenReturn(Optional.of(existing));
        when(cardInfoRepository.findByNumber(request.number())).thenReturn(Optional.empty());
        doNothing().when(cardInfoMapper).update(existing, request);
        when(userService.getEntityById(2L)).thenReturn(newUser);
        when(cardInfoRepository.save(existing)).thenReturn(updated);
        when(cardInfoMapper.toCacheDto(updated)).thenReturn(cacheDto);
        when(cardInfoMapper.toDto(updated)).thenReturn(response);

        CardInfoResponse result = cardInfoService.updateCard(id, request);

        assertThat(result).isEqualTo(response);
        verify(cardInfoCacheService).save(cacheDto);
    }
}
