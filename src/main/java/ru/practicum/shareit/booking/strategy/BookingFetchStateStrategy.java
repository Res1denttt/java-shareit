package ru.practicum.shareit.booking.strategy;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;

import java.util.List;

public interface BookingFetchStateStrategy {

    List<Booking> getBookings(long userId, Sort sort);

    BookingState getStrategyState();
}
