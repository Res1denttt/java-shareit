package ru.practicum.server.booking.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.server.booking.Booking;
import ru.practicum.server.booking.BookingRepository;
import ru.practicum.server.booking.BookingState;
import ru.practicum.server.booking.BookingStatus;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WaitingBookingStrategy implements BookingFetchStateStrategy {

    private final BookingRepository repository;

    @Override
    public List<Booking> getBookings(long userId, Sort sort) {
        return repository.findAllByBooker_IdAndStatus(userId, BookingStatus.WAITING, sort);
    }

    @Override
    public BookingState getStrategyState() {
        return BookingState.WAITING;
    }
}
