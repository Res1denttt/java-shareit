package ru.practicum.server.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.dto.item.ItemDto;
import ru.practicum.dto.request.ItemRequestDto;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    private ItemDto buildItemDto() {
        ItemDto item = new ItemDto();
        item.setId(3L);
        item.setName("Дрель");
        item.setDescription("ударная");
        item.setAvailable(true);
        return item;
    }

    @Test
    void testSerializeItemRequestDto() throws Exception {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужна дрель для ремонта")
                .created(Instant.parse("2025-12-06T14:30:00Z"))
                .items(List.of(buildItemDto()))
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"description\":\"Нужна дрель для ремонта\"");
        assertThat(json).contains("\"created\":\"2025-12-06T14:30:00Z\"");
        assertThat(json).contains("\"items\":[{\"id\":3");
    }

    @Test
    void testDeserializeItemRequestDto() throws Exception {
        String json = """
                {
                  "id": 1,
                  "description": "Нужна дрель для ремонта",
                  "created": "2025-12-06T14:30:00Z",
                  "items": [
                    {
                      "id": 3,
                      "name": "Дрель",
                      "description": "ударная",
                      "available": true
                    }
                  ]
                }
                """;

        ItemRequestDto dto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Нужна дрель для ремонта");
        assertThat(dto.getCreated()).isEqualTo(Instant.parse("2025-12-06T14:30:00Z"));
        assertThat(dto.getItems().get(0).getId()).isEqualTo(3L);
        assertThat(dto.getItems().get(0).getName()).isEqualTo("Дрель");
    }
}
