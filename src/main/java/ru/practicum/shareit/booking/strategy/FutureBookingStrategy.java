package ru.practicum.shareit.booking.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FutureBookingStrategy implements BookingFetchStateStrategy {

    private final BookingRepository repository;

    @Override
    public List<Booking> getBookings(long userId, Sort sort) {
        return repository.findAllByBooker_IdAndStartIsAfter(userId, LocalDateTime.now(), sort);
    }

    @Override
    public BookingState getStrategyState() {
        return BookingState.FUTURE;
    }
}
