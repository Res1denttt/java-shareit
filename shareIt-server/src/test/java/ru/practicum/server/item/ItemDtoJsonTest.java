package ru.practicum.server.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.dto.booking.BookingDto;
import ru.practicum.dto.item.CommentDto;
import ru.practicum.dto.item.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    private BookingDto buildBookingDto() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.of(2025, 12, 6, 10, 0));
        dto.setEnd(LocalDateTime.of(2025, 12, 6, 12, 0));
        dto.setItemId(3L);
        return dto;
    }

    private CommentDto buildCommentDto() {
        return new CommentDto(
                1L,
                "Отличная дрель!",
                "Иван Иванов",
                LocalDateTime.of(2025, 12, 6, 14, 30),
                3L
        );
    }

    @Test
    void testSerializeItemDto() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(3L);
        dto.setName("Дрель");
        dto.setDescription("ударная");
        dto.setAvailable(true);
        dto.setLastBooking(buildBookingDto());
        dto.setNextBooking(null);
        dto.setComments(List.of(buildCommentDto()));
        dto.setRequestId(null);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":3");
        assertThat(json).contains("\"name\":\"Дрель\"");
        assertThat(json).contains("\"description\":\"ударная\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"lastBooking\":{\"id\":1");
        assertThat(json).contains("\"itemId\":3");
        assertThat(json).contains("\"comments\":[{\"id\":1");
    }

    @Test
    void testDeserializeItemDto() throws Exception {
        String json = """
            {
              "id": 3,
              "name": "Дрель",
              "description": "ударная",
              "available": true,
              "lastBooking": {
                "id": 1,
                "start": "2025-12-06T10:00:00",
                "end": "2025-12-06T12:00:00",
                "itemId": 3
              },
              "nextBooking": null,
              "comments": [
                {
                  "id": 1,
                  "text": "Отличная дрель!",
                  "authorName": "Иван Иванов",
                  "created": "2025-12-06T14:30:00",
                  "itemId": 3
                }
              ],
              "requestId": null
            }
            """;

        ItemDto dto = objectMapper.readValue(json, ItemDto.class);

        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.getName()).isEqualTo("Дрель");
        assertThat(dto.getDescription()).isEqualTo("ударная");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getLastBooking().getId()).isEqualTo(1L);
        assertThat(dto.getLastBooking().getItemId()).isEqualTo(3L);
        assertThat(dto.getComments().get(0).getText()).isEqualTo("Отличная дрель!");
    }
}
