
package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.strategy.BookingFetchStateStrategyFactory;
import ru.practicum.shareit.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exceptions.InvalidOperation;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingFetchStateStrategyFactory strategyFactory;

    @Transactional
    @Override
    public ResponseBookingDto create(long userId, BookingDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Товар с id = " + dto.getItemId() + " не найден"));
        checkConditions(dto, item);
        Booking booking = BookingMapper.mapToBooking(dto, item, user);
        Booking saved = bookingRepository.save(booking);
        return BookingMapper.mapToResponseBookingDto(saved);
    }

    private boolean checkConditions(BookingDto dto, Item item) {
        LocalDateTime start = dto.getStart();
        LocalDateTime end = dto.getEnd();
        if (!item.isAvailable())
            throw new ConditionsNotMetException("Товар с id = " + item.getId() + "не доступен для бронирования");
        if (start.isBefore(LocalDateTime.now().minusSeconds(3)))
            throw new ConditionsNotMetException("Неверно указано начало бронирования");
        if (!end.isAfter(start))
            throw new ConditionsNotMetException("Неверно указано окончание бронирования");
        return true;
    }

    @Override
    public ResponseBookingDto findById(long userId, long bookingId) {
        Booking booking = getBooking(bookingId);
        Item item = booking.getItem();
        if (userId != booking.getBooker().getId() && userId != item.getOwner().getId())
            throw new InvalidOperation("Просмотр бронирования доступен только для инициатора бронирования и " +
                    "владельца товара");
        return BookingMapper.mapToResponseBookingDto(booking);
    }

    @Transactional
    @Override
    public ResponseBookingDto approve(long userId, long bookingId, boolean approved) {
        Booking booking = getBooking(bookingId);
        Item item = booking.getItem();
        if (userId != item.getOwner().getId())
            throw new InvalidOperation("Можно подтверждать бронирование только своих товаров");
        if (!item.isAvailable()) {
            booking.setStatus(BookingStatus.REJECTED);
            bookingRepository.save(booking);
            throw new ConditionsNotMetException("Товар с id = " + item.getId() + " более не доступен для бронирования.");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepository.save(booking);
        return BookingMapper.mapToResponseBookingDto(booking);
    }

    @Override
    public List<ResponseBookingDto> findByState(long userId, BookingState state) {
        if (!userRepository.existsById(userId))
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        Sort sort = Sort.by("start").ascending();
        List<Booking> bookings = strategyFactory.findStrategy(state).getBookings(userId, sort);
        return BookingMapper.mapToResponseBookingDto(bookings);
    }

    @Override
    public List<ResponseBookingDto> findForOwnerByState(long userId, BookingState state) {
        if (!userRepository.existsById(userId))
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        Sort sort = Sort.by("start").ascending();
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByItem_Owner_Id(userId, sort);
            case CURRENT -> bookingRepository.findCurrentByOwnerId(userId);
            case PAST -> bookingRepository.findAllByItem_Owner_IdAndEndIsBefore(userId, OffsetDateTime.now(), sort);
            case FUTURE -> bookingRepository.findAllByItem_Owner_IdAndStartIsAfter(userId, OffsetDateTime.now(), sort);
            case WAITING -> bookingRepository.findAllByItemOwner_IdAndStatus(userId, BookingStatus.WAITING, sort);
            case REJECTED -> bookingRepository.findAllByItemOwner_IdAndStatus(userId, BookingStatus.REJECTED, sort);
        };
        return BookingMapper.mapToResponseBookingDto(bookings);
    }

    private Booking getBooking(long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + id + " не найдено"));
    }
}