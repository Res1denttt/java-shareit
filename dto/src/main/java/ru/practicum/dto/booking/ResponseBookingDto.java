package ru.practicum.dto.booking;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.dto.item.ItemDto;
import ru.practicum.dto.user.UserDto;

import java.time.LocalDateTime;

@Builder
@Getter
public class ResponseBookingDto {

    private long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private String status;

    private ItemDto item;

    private UserDto booker;

}
