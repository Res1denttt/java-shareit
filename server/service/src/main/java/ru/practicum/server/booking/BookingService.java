package ru.practicum.server.booking;

import ru.practicum.dto.booking.BookingDto;
import ru.practicum.dto.booking.ResponseBookingDto;

import java.util.List;

public interface BookingService {

    ResponseBookingDto create(long userId, BookingDto dto);

    ResponseBookingDto findById(long userId, long bookingId);

    ResponseBookingDto approve(long userId, long itemId, boolean approved);

    List<ResponseBookingDto> findByState(long userId, BookingState state);

    List<ResponseBookingDto> findForOwnerByState(long userId, BookingState state);
}
