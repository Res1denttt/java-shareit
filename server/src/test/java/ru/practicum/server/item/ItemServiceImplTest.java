package ru.practicum.server.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.item.CommentDto;
import ru.practicum.dto.item.ItemDto;
import ru.practicum.dto.item.NewCommentDto;
import ru.practicum.dto.item.OwnerItemDto;
import ru.practicum.server.booking.Booking;
import ru.practicum.server.booking.BookingRepository;
import ru.practicum.server.exceptions.InvalidOperationException;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.request.ItemRequest;
import ru.practicum.server.request.ItemRequestRepository;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImpl service;

    @Test
    void findAllForUser_shouldReturnItemsWithBookings() {
        long userId = 1L;

        User owner = new User("oleg", "oleg@mail.ru");
        owner.setId(userId);

        Item item = new Item();
        item.setId(10L);
        item.setOwner(owner);

        Booking past = new Booking();
        past.setId(100L);
        past.setItem(item);
        past.setStart(LocalDateTime.now().minusDays(2));
        past.setEnd(LocalDateTime.now().minusDays(1));

        Booking future = new Booking();
        future.setId(200L);
        future.setItem(item);
        future.setStart(LocalDateTime.now().plusDays(1));
        future.setEnd(LocalDateTime.now().plusDays(2));

        item.setBookings(List.of(past, future));
        item.setComments(List.of());

        Mockito.when(itemRepository.findAllByOwnerId(userId))
                .thenReturn(List.of(item));

        List<OwnerItemDto> result = service.findAllForUser(userId);

        assertThat(result, hasSize(1));
        OwnerItemDto dto = result.get(0);
        assertThat(dto.getId(), is(10L));
        assertThat(dto.getLastBooking(), notNullValue());
        assertThat(dto.getNextBooking(), notNullValue());
        assertThat(dto.getLastBooking().getId(), is(100L));
        assertThat(dto.getNextBooking().getId(), is(200L));
        Mockito.verify(itemRepository).findAllByOwnerId(userId);
    }

    @Test
    void findById_shouldReturnOwnerItemDto_whenOwner() {
        long userId = 1L;

        User owner = new User("oleg", "oleg@mail.ru");
        owner.setId(userId);

        Item item = new Item();
        item.setId(10L);
        item.setOwner(owner);
        item.setBookings(List.of());
        item.setComments(List.of());

        Mockito.when(itemRepository.findByIdWithRelations(10L))
                .thenReturn(item);

        ItemDto result = service.findById(userId, 10L);

        assertThat(result, instanceOf(OwnerItemDto.class));
        assertThat(result.getId(), is(10L));
        Mockito.verify(itemRepository).findByIdWithRelations(10L);
    }

    @Test
    void findById_shouldReturnItemDto_whenNotOwner() {
        long userId = 2L;

        User owner = new User("oleg", "oleg@mail.ru");
        owner.setId(1L);

        Item item = new Item();
        item.setId(10L);
        item.setOwner(owner);

        Mockito.when(itemRepository.findByIdWithRelations(10L))
                .thenReturn(item);

        ItemDto result = service.findById(userId, 10L);

        assertThat(result, not(instanceOf(OwnerItemDto.class)));
        assertThat(result.getId(), is(10L));
        Mockito.verify(itemRepository).findByIdWithRelations(10L);
    }

    @Test
    void create_shouldReturnItemDto_withoutRequest() {
        long userId = 1L;

        User owner = new User("oleg", "oleg@mail.ru");
        owner.setId(userId);

        ItemDto dto = new ItemDto();
        dto.setName("item");
        dto.setDescription("desc");
        dto.setAvailable(true);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(owner));

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocation -> {
                    Item i = invocation.getArgument(0);
                    i.setId(10L);
                    return i;
                });

        ItemDto result = service.create(userId, dto);

        assertThat(result.getId(), is(10L));
        assertThat(result.getName(), is("item"));
        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(itemRepository).save(Mockito.any(Item.class));
    }

    @Test
    void create_shouldUseRequest_whenRequestExists() {
        long userId = 1L;
        long requestId = 5L;

        User owner = new User("oleg", "oleg@mail.ru");
        owner.setId(userId);

        ItemRequest request = new ItemRequest();
        request.setId(requestId);

        ItemDto dto = new ItemDto();
        dto.setName("item");
        dto.setDescription("desc");
        dto.setAvailable(true);
        dto.setRequestId(requestId);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(owner));
        Mockito.when(requestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocation -> {
                    Item i = invocation.getArgument(0);
                    i.setId(10L);
                    return i;
                });

        ItemDto result = service.create(userId, dto);

        assertThat(result.getId(), is(10L));
        assertThat(result.getRequestId(), is(requestId));
        Mockito.verify(requestRepository).findById(requestId);
        Mockito.verify(itemRepository).save(Mockito.any(Item.class));
    }


    @Test
    void create_shouldThrow_NotFound_whenRequestMissing() {
        long userId = 1L;
        long requestId = 5L;

        User owner = new User("oleg", "oleg@mail.ru");
        owner.setId(userId);

        ItemDto dto = new ItemDto();
        dto.setName("item");
        dto.setDescription("desc");
        dto.setAvailable(true);
        dto.setRequestId(requestId);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(owner));
        Mockito.when(requestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> service.create(userId, dto)
        );

        assertThat(ex.getMessage(), containsString("Запрос товвара с id 5 не найден"));
        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void update_shouldReturnUpdatedItemDto() {
        long userId = 1L;

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);

        User owner = new User("oleg", "oleg@mail.ru");
        owner.setId(userId);

        Item oldItem = new Item();
        oldItem.setId(10L);
        oldItem.setName("old");
        oldItem.setOwner(owner);

        ItemDto dto = new ItemDto();
        dto.setId(10L);
        dto.setName("new name");

        Mockito.when(itemRepository.findById(10L))
                .thenReturn(Optional.of(oldItem));

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto result = service.update(userId, dto);

        assertThat(result.getId(), is(10L));
        assertThat(result.getName(), is("new name"));
        Mockito.verify(itemRepository).findById(10L);
        Mockito.verify(itemRepository).save(Mockito.any(Item.class));
    }

    @Test
    void update_shouldThrow_NotFound_whenUserMissing() {
        long userId = 1L;

        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        ItemDto dto = new ItemDto();
        dto.setId(10L);

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> service.update(userId, dto)
        );

        assertThat(ex.getMessage(), containsString("Пользователь с id = 1 не найден"));
        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void update_shouldThrow_InvalidOperation_whenNotOwner() {
        long userId = 2L;

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);

        User owner = new User("oleg", "oleg@mail.ru");
        owner.setId(1L);

        Item oldItem = new Item();
        oldItem.setId(10L);
        oldItem.setOwner(owner);

        ItemDto dto = new ItemDto();
        dto.setId(10L);

        Mockito.when(itemRepository.findById(10L))
                .thenReturn(Optional.of(oldItem));

        InvalidOperationException ex = assertThrows(
                InvalidOperationException.class,
                () -> service.update(userId, dto)
        );

        assertThat(ex.getMessage(), containsString("Можно редактировать только свои товары"));
        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void delete_shouldRemove_whenOwner() {
        long userId = 1L;

        User owner = new User("oleg", "oleg@mail.ru");
        owner.setId(userId);

        Item item = new Item();
        item.setId(10L);
        item.setOwner(owner);

        Mockito.when(itemRepository.findById(10L))
                .thenReturn(Optional.of(item));

        service.delete(userId, 10L);

        Mockito.verify(itemRepository).delete(item);
    }

    @Test
    void delete_shouldThrow_InvalidOperation_whenNotOwner() {
        long userId = 2L;

        User owner = new User("oleg", "oleg@mail.ru");
        owner.setId(1L);

        Item item = new Item();
        item.setId(10L);
        item.setOwner(owner);

        Mockito.when(itemRepository.findById(10L))
                .thenReturn(Optional.of(item));

        InvalidOperationException ex = assertThrows(
                InvalidOperationException.class,
                () -> service.delete(userId, 10L)
        );

        assertThat(ex.getMessage(), containsString("Можно удалять только свои товары"));
        Mockito.verify(itemRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    void search_shouldReturnMappedItems() {
        Item item = new Item();
        item.setId(10L);
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);

        Mockito.when(itemRepository.search("text"))
                .thenReturn(List.of(item));

        List<ItemDto> result = service.search("text");

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), is(10L));
        Mockito.verify(itemRepository).search("text");
    }

    @Test
    void createComment_shouldReturnCommentDto() {
        long userId = 1L;
        long itemId = 10L;

        User user = new User("oleg", "oleg@mail.ru");
        user.setId(userId);

        Item item = new Item();
        item.setId(itemId);

        Booking booking = new Booking();
        booking.setId(100L);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));

        NewCommentDto dto = new NewCommentDto();
        dto.setText("nice");

        Comment saved = new Comment();
        saved.setId(5L);
        saved.setText("nice");
        saved.setAuthorName(user.getName());
        saved.setItem(item);
        saved.setCreated(LocalDateTime.now());

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findBookingsByItemAndBooker(Mockito.eq(item), Mockito.eq(user), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        Mockito.when(commentRepository.save(Mockito.any(Comment.class)))
                .thenReturn(saved);

        CommentDto result = service.createComment(userId, itemId, dto);

        assertThat(result.getId(), is(5L));
        assertThat(result.getText(), is("nice"));
        assertThat(result.getAuthorName(), is("oleg"));
        assertThat(result.getItemId(), is(itemId));
        Mockito.verify(commentRepository).save(Mockito.any(Comment.class));
    }

    @Test
    void createComment_shouldThrow_InvalidOperation_whenNoBookings() {
        long userId = 1L;
        long itemId = 10L;

        User user = new User("oleg", "oleg@mail.ru");
        user.setId(userId);

        Item item = new Item();
        item.setId(itemId);

        NewCommentDto dto = new NewCommentDto();
        dto.setText("nice");

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findBookingsByItemAndBooker(Mockito.eq(item), Mockito.eq(user), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of());

        InvalidOperationException ex = assertThrows(
                InvalidOperationException.class,
                () -> service.createComment(userId, itemId, dto)
        );

        assertThat(ex.getMessage(), containsString("Можно оставлять отзыв только на товары"));
        Mockito.verify(commentRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void getItem_shouldThrow_NotFound() {
        Mockito.when(itemRepository.findById(10L))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> service.delete(1L, 10L)
        );

        assertThat(ex.getMessage(), containsString("Item с id = 10 не найден"));
    }

    @Test
    void getUser_shouldThrow_NotFound() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> service.create(1L, new ItemDto())
        );

        assertThat(ex.getMessage(), containsString("Пользователь с id = 1 не найден"));
    }
}
