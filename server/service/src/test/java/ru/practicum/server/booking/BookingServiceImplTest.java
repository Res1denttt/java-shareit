package ru.practicum.server.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.dto.booking.BookingDto;
import ru.practicum.dto.booking.ResponseBookingDto;
import ru.practicum.server.booking.strategy.BookingFetchStateStrategy;
import ru.practicum.server.booking.strategy.BookingFetchStateStrategyFactory;
import ru.practicum.server.exceptions.ConditionsNotMetException;
import ru.practicum.server.exceptions.InvalidOperationException;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.server.item.ItemRepository;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingFetchStateStrategyFactory strategyFactory;

    @InjectMocks
    private BookingServiceImpl service;


    @Test
    void create_shouldReturnResponseBookingDto() {
        long userId = 1L;
        long itemId = 2L;

        User user = new User("oleg", "oleg@mail.ru");
        user.setId(userId);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(true);

        BookingDto dto = new BookingDto();
        dto.setItemId(itemId);
        dto.setStart(LocalDateTime.now().plusHours(1));
        dto.setEnd(LocalDateTime.now().plusHours(2));

        Booking booking = BookingMapper.mapToBooking(dto, item, user);
        booking.setId(10L);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking);

        ResponseBookingDto result = service.create(userId, dto);

        assertThat(result.getId(), is(10L));
        assertThat(result.getItem().getId(), is(itemId));
        assertThat(result.getBooker().getId(), is(userId));
        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(itemRepository).findById(itemId);
        Mockito.verify(bookingRepository).save(Mockito.any(Booking.class));
    }

    @Test
    void create_shouldThrow_NotFound_whenUserMissing() {
        long userId = 1L;
        BookingDto dto = new BookingDto();
        dto.setItemId(2L);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> service.create(userId, dto)
        );

        assertThat(ex.getMessage(), containsString("Пользователь с id = 1 не найден"));
        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(itemRepository, Mockito.never()).findById(Mockito.anyLong());
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void create_shouldThrow_NotFound_whenItemMissing() {
        long userId = 1L;
        long itemId = 2L;

        User user = new User("oleg", "oleg@mail.ru");
        user.setId(userId);

        BookingDto dto = new BookingDto();
        dto.setItemId(itemId);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> service.create(userId, dto)
        );

        assertThat(ex.getMessage(), containsString("Товар с id = 2 не найден"));
        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(itemRepository).findById(itemId);
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void create_shouldThrow_ConditionsNotMet_whenItemNotAvailable() {
        long userId = 1L;
        long itemId = 2L;

        User user = new User("oleg", "oleg@mail.ru");
        user.setId(userId);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(false);

        BookingDto dto = new BookingDto();
        dto.setItemId(itemId);
        dto.setStart(LocalDateTime.now().plusHours(1));
        dto.setEnd(LocalDateTime.now().plusHours(2));

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ConditionsNotMetException ex = assertThrows(
                ConditionsNotMetException.class,
                () -> service.create(userId, dto)
        );

        assertThat(ex.getMessage(), containsString("не доступен для бронирования"));
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());
    }


    @Test
    void findById_shouldReturn_forBooker() {
        long userId = 1L;
        long ownerId = 2L;
        long bookingId = 10L;

        User booker = new User("booker", "b@mail.ru");
        booker.setId(userId);

        User owner = new User("owner", "o@mail.ru");
        owner.setId(ownerId);

        Item item = new Item();
        item.setId(3L);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        ResponseBookingDto dto = service.findById(userId, bookingId);

        assertThat(dto.getId(), is(bookingId));
        Mockito.verify(bookingRepository).findById(bookingId);
    }

    @Test
    void findById_shouldThrow_InvalidOperation_whenUserNotBookerOrOwner() {
        long userId = 99L;
        long bookingId = 10L;

        User booker = new User("booker", "b@mail.ru");
        booker.setId(1L);

        User owner = new User("owner", "o@mail.ru");
        owner.setId(2L);

        Item item = new Item();
        item.setId(3L);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setItem(item);

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        InvalidOperationException ex = assertThrows(
                InvalidOperationException.class,
                () -> service.findById(userId, bookingId)
        );

        assertThat(ex.getMessage(), containsString("Просмотр бронирования доступен только"));
    }

    @Test
    void approve_shouldSetApproved_whenOwnerAndAvailable() {
        long userId = 2L;
        long bookingId = 10L;

        User booker = new User("booker", "b@mail.ru");
        booker.setId(1L);

        User owner = new User("owner", "o@mail.ru");
        owner.setId(userId);

        Item item = new Item();
        item.setId(3L);
        item.setOwner(owner);
        item.setAvailable(true);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseBookingDto dto = service.approve(userId, bookingId, true);

        assertThat(dto.getStatus(), is("APPROVED"));
        Mockito.verify(bookingRepository).save(Mockito.any(Booking.class));
    }

    @Test
    void approve_shouldThrow_InvalidOperation_whenNotOwner() {
        long userId = 99L;
        long bookingId = 10L;

        User owner = new User("owner", "o@mail.ru");
        owner.setId(2L);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(new Item());
        booking.getItem().setOwner(owner);

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        InvalidOperationException ex = assertThrows(
                InvalidOperationException.class,
                () -> service.approve(userId, bookingId, true)
        );

        assertThat(ex.getMessage(), containsString("только своих товаров"));
        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void approve_shouldSetRejectedAndThrow_whenItemNoLongerAvailable() {
        long userId = 2L;
        long bookingId = 10L;

        User owner = new User("owner", "o@mail.ru");
        owner.setId(userId);

        Item item = new Item();
        item.setId(3L);
        item.setOwner(owner);
        item.setAvailable(false);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ConditionsNotMetException ex = assertThrows(
                ConditionsNotMetException.class,
                () -> service.approve(userId, bookingId, true)
        );

        assertThat(ex.getMessage(), containsString("более не доступен"));
        assertThat(booking.getStatus(), is(BookingStatus.REJECTED));
        Mockito.verify(bookingRepository).save(booking);
    }


    @Test
    void findByState_shouldUseStrategy() {
        long userId = 1L;
        BookingState state = BookingState.CURRENT;

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);

        User booker = new User("oleg", "oleg@mail.ru");
        booker.setId(5L);

        Item item = new Item();
        item.setId(3L);
        item.setOwner(booker);
        item.setAvailable(true);

        Booking booking = new Booking();
        booking.setId(10L);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);

        BookingFetchStateStrategy strategy = Mockito.mock(BookingFetchStateStrategy.class);

        Mockito.when(strategyFactory.findStrategy(state)).thenReturn(strategy);
        Mockito.when(strategy.getBookings(Mockito.eq(userId), Mockito.any(Sort.class)))
                .thenReturn(List.of(booking));

        List<ResponseBookingDto> result = service.findByState(userId, state);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), is(10L));
        assertThat(result.get(0).getBooker().getId(), is(5L));
        assertThat(result.get(0).getItem().getId(), is(3L));

        Mockito.verify(userRepository).existsById(userId);
        Mockito.verify(strategyFactory).findStrategy(state);
        Mockito.verify(strategy).getBookings(Mockito.eq(userId), Mockito.any(Sort.class));
    }


    @Test
    void findByState_shouldThrow_NotFound_whenUserMissing() {
        long userId = 1L;

        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> service.findByState(userId, BookingState.ALL)
        );

        assertThat(ex.getMessage(), containsString("Пользователь с id = 1 не найден"));
        Mockito.verify(strategyFactory, Mockito.never()).findStrategy(Mockito.any());
    }


    @Test
    void findForOwnerByState_ALL() {
        long userId = 2L;

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);

        User booker = new User("oleg", "oleg@mail.ru");
        booker.setId(5L);

        Item item = new Item();
        item.setId(3L);
        item.setOwner(booker);
        item.setAvailable(true);

        Booking booking = new Booking();
        booking.setId(10L);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);

        Mockito.when(bookingRepository.findAllByItem_Owner_Id(Mockito.eq(userId), Mockito.any(Sort.class)))
                .thenReturn(List.of(booking));

        List<ResponseBookingDto> result = service.findForOwnerByState(userId, BookingState.ALL);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), is(10L));
        Mockito.verify(bookingRepository).findAllByItem_Owner_Id(Mockito.eq(userId), Mockito.any(Sort.class));
    }

    @Test
    void findForOwnerByState_shouldThrow_NotFound_whenUserMissing() {
        long userId = 2L;

        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> service.findForOwnerByState(userId, BookingState.ALL)
        );

        assertThat(ex.getMessage(), containsString("Пользователь с id = 2 не найден"));
        Mockito.verify(bookingRepository, Mockito.never()).findAllByItem_Owner_Id(Mockito.anyLong(), Mockito.any());
    }
}
