package ru.practicum.server.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.request.ItemRequestDto;
import ru.practicum.dto.request.NewRequestDto;
import ru.practicum.dto.request.ShortItemRequest;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository requestRepository;

    @InjectMocks
    private ItemRequestServiceImpl service;

    @Test
    void create_shouldReturnItemRequestDto() {
        long userId = 1L;

        User user = new User("oleg", "oleg@mail.ru");
        user.setId(userId);

        NewRequestDto dto = new NewRequestDto();
        dto.setDescription("нужна дрель");

        ItemRequest saved = new ItemRequest();
        saved.setId(10L);
        saved.setDescription("нужна дрель");
        saved.setUser(user);
        saved.setCreated(Instant.now());

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(requestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(saved);

        ItemRequestDto result = service.create(userId, dto);

        assertThat(result.getId(), is(10L));
        assertThat(result.getDescription(), is("нужна дрель"));
        assertThat(result.getCreated(), notNullValue());
        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(requestRepository).save(Mockito.any(ItemRequest.class));
    }

    @Test
    void create_shouldThrow_NotFound_whenUserMissing() {
        long userId = 1L;
        NewRequestDto dto = new NewRequestDto();
        dto.setDescription("нужна дрель");

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> service.create(userId, dto)
        );

        assertThat(ex.getMessage(), containsString("Пользователь с id 1 не найден"));
        Mockito.verify(requestRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findUserRequests_shouldReturnMappedDtos() {
        long userId = 1L;

        User user = new User("oleg", "oleg@mail.ru");
        user.setId(userId);

        ItemRequest r1 = new ItemRequest();
        r1.setId(10L);
        r1.setDescription("нужна дрель");
        r1.setUser(user);
        r1.setCreated(Instant.now());

        ItemRequest r2 = new ItemRequest();
        r2.setId(11L);
        r2.setDescription("нужен молоток");
        r2.setUser(user);
        r2.setCreated(Instant.now().minusSeconds(3600));

        Mockito.when(requestRepository.findAllByUserIdOrderByCreatedDesc(userId))
                .thenReturn(List.of(r1, r2));

        List<ItemRequestDto> result = service.findUserRequests(userId);

        assertThat(result, hasSize(2));
        assertThat(result.get(0).getId(), is(10L));
        assertThat(result.get(1).getId(), is(11L));
        Mockito.verify(requestRepository).findAllByUserIdOrderByCreatedDesc(userId);
    }

    @Test
    void findAllRequests_shouldReturnShortRequests() {
        long userId = 1L;

        ShortItemRequest r1 = Mockito.mock(ShortItemRequest.class);
        ShortItemRequest r2 = Mockito.mock(ShortItemRequest.class);

        Mockito.when(r1.getDescription()).thenReturn("нужна дрель");
        Mockito.when(r2.getDescription()).thenReturn("нужен молоток");

        Mockito.when(requestRepository.findAllByUserIdNotOrderByCreatedDesc(userId))
                .thenReturn(List.of(r1, r2));

        List<ShortItemRequest> result = service.findAllRequests(userId);

        assertThat(result, hasSize(2));
        assertThat(result.get(0).getDescription(), is("нужна дрель"));
        assertThat(result.get(1).getDescription(), is("нужен молоток"));
        Mockito.verify(requestRepository).findAllByUserIdNotOrderByCreatedDesc(userId);
    }

    @Test
    void findById_shouldReturnItemRequestDto() {
        long requestId = 10L;

        User user = new User("oleg", "oleg@mail.ru");
        user.setId(1L);

        ItemRequest request = new ItemRequest();
        request.setId(requestId);
        request.setDescription("нужна дрель");
        request.setUser(user);
        request.setCreated(Instant.now());

        Mockito.when(requestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        ItemRequestDto result = service.findById(requestId);

        assertThat(result.getId(), is(requestId));
        assertThat(result.getDescription(), is("нужна дрель"));
        assertThat(result.getCreated(), notNullValue());
        Mockito.verify(requestRepository).findById(requestId);
    }

    @Test
    void findById_shouldThrow_NotFound_whenMissing() {
        long requestId = 10L;

        Mockito.when(requestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> service.findById(requestId)
        );

        assertThat(ex.getMessage(), containsString("Запрос товвара с id 10 не найден"));
        Mockito.verify(requestRepository).findById(requestId);
    }
}
