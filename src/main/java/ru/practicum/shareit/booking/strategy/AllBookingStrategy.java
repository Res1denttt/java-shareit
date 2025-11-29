package ru.practicum.shareit.booking.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AllBookingStrategy implements BookingFetchStateStrategy {

    private final BookingRepository repository;

    @Override
    public List<Booking> getBookings(long userId, Sort sort) {
        return repository.findAllByBookerId(userId, sort);
    }

    @Override
    public BookingState getStrategyState() {
        return BookingState.ALL;
    }
}
