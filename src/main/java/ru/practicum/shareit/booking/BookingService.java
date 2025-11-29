package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;

import java.util.List;

public interface BookingService {

    ResponseBookingDto create(long userId, BookingDto dto);

    ResponseBookingDto findById(long userId, long bookingId);

    ResponseBookingDto approve(long userId, long itemId, boolean approved);

    List<ResponseBookingDto> findByState(long userId, BookingState state);

    List<ResponseBookingDto> findForOwnerByState(long userId, BookingState state);
}
