package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class ResponseBookingDto {

    private long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;

    private ItemDto item;

    private UserDto booker;

    private List<Comment> comments;
}
