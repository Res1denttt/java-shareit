package ru.practicum.server.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.dto.booking.ResponseBookingDto;
import ru.practicum.dto.item.ItemDto;
import ru.practicum.dto.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ResponseBookingDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    private ItemDto buildItemDto() {
        ItemDto dto = new ItemDto();
        dto.setId(3L);
        dto.setName("Дрель");
        dto.setDescription("ударная");
        dto.setAvailable(true);
        dto.setLastBooking(null);
        dto.setNextBooking(null);
        dto.setComments(List.of());
        dto.setRequestId(null);
        return dto;
    }

    private UserDto buildUserDto() {
        UserDto user = new UserDto();
        user.setId(2L);
        user.setEmail("booker@mail.ru");
        user.setName("booker");
        return user;
    }

    @Test
    void testSerializeResponseBookingDto() throws Exception {
        ResponseBookingDto dto = ResponseBookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 12, 7, 10, 0))
                .end(LocalDateTime.of(2025, 12, 7, 12, 0))
                .status("APPROVED")
                .item(buildItemDto())
                .booker(buildUserDto())
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"status\":\"APPROVED\"");
        assertThat(json).contains("\"item\":{\"id\":3");
        assertThat(json).contains("\"booker\":{\"id\":2");
    }

    @Test
    void testDeserializeResponseBookingDto() throws Exception {
        String json = """
            {
              "id": 1,
              "start": "2025-12-07T10:00:00",
              "end": "2025-12-07T12:00:00",
              "status": "APPROVED",
              "item": {
                "id": 3,
                "name": "Дрель",
                "description": "ударная",
                "available": true,
                "lastBooking": null,
                "nextBooking": null,
                "comments": [],
                "requestId": null
              },
              "booker": {
                "id": 2,
                "email": "booker@mail.ru",
                "name": "booker"
              }
            }
            """;

        ResponseBookingDto dto =
                objectMapper.readValue(json, ResponseBookingDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStatus()).isEqualTo("APPROVED");
        assertThat(dto.getItem().getId()).isEqualTo(3L);
        assertThat(dto.getItem().getName()).isEqualTo("Дрель");
        assertThat(dto.getBooker().getId()).isEqualTo(2L);
        assertThat(dto.getBooker().getEmail()).isEqualTo("booker@mail.ru");
    }
}
