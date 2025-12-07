package ru.practicum.server.booking;

import lombok.RequiredArgsConstructor;
import ru.practicum.dto.booking.BookingDto;
import ru.practicum.dto.booking.ResponseBookingDto;
import ru.practicum.server.item.ItemMapper;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserMapper;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class BookingMapper {

    public static Booking mapToBooking(BookingDto dto, Item item, User booker) {
        return new Booking(dto.getStart(), dto.getEnd(), item, booker);
    }

    public static ResponseBookingDto mapToResponseBookingDto(Booking booking) {
        return ResponseBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus().toString())
                .booker(UserMapper.mapToUserDto(booking.getBooker()))
                .item(ItemMapper.mapToItemDto(booking.getItem()))
                .build();
    }

    public static List<ResponseBookingDto> mapToResponseBookingDto(Iterable<Booking> bookings) {
        List<ResponseBookingDto> responseBookingDtos = new ArrayList<>();
        for (Booking booking : bookings) {
            responseBookingDtos.add(mapToResponseBookingDto(booking));
        }
        return responseBookingDtos;
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem().getId());
    }
}
