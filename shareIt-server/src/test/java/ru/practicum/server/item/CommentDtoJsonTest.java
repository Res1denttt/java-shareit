package ru.practicum.server.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.dto.item.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerializeCommentDto() throws Exception {
        CommentDto dto = new CommentDto(
                1L,
                "Отличная дрель! Работает безупречно.",
                "Иван Иванов",
                LocalDateTime.of(2025, 12, 6, 14, 30),
                3L
        );

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"text\":\"Отличная дрель! Работает безупречно.\"");
        assertThat(json).contains("\"authorName\":\"Иван Иванов\"");
        assertThat(json).contains("\"created\":\"2025-12-06T14:30:00\"");
        assertThat(json).contains("\"itemId\":3");
    }

    @Test
    void testDeserializeCommentDto() throws Exception {
        String json = """
            {
              "id": 1,
              "text": "Отличная дрель! Работает безупречно.",
              "authorName": "Иван Иванов",
              "created": "2025-12-06T14:30:00",
              "itemId": 3
            }
            """;

        CommentDto dto = objectMapper.readValue(json, CommentDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Отличная дрель! Работает безупречно.");
        assertThat(dto.getAuthorName()).isEqualTo("Иван Иванов");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2025, 12, 6, 14, 30));
        assertThat(dto.getItemId()).isEqualTo(3L);
    }
}
