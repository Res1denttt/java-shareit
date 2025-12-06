package ru.practicum.server.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.dto.booking.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
 public class BookingDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerializeBookingDto() throws Exception {
        BookingDto dto = new BookingDto(
                1L,
                LocalDateTime.of(2025, 12, 7, 10, 0),
                LocalDateTime.of(2025, 12, 7, 12, 0),
                3L
        );

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json)
                .isEqualTo("{\"id\":1,\"start\":\"2025-12-07T10:00:00\",\"end\":\"2025-12-07T12:00:00\",\"itemId\":3}");
    }

    @Test
    void testDeserializeBookingDto() throws Exception {
        String json = "{\"id\":1,\"start\":\"2025-12-07T10:00:00\",\"end\":\"2025-12-07T12:00:00\",\"itemId\":3}";

        BookingDto dto = objectMapper.readValue(json, BookingDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2025, 12, 7, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2025, 12, 7, 12, 0));
        assertThat(dto.getItemId()).isEqualTo(3L);
    }

    @Test
    void testDeserializeNullFields() throws Exception {
        String json = "{\"id\":null,\"start\":null,\"end\":null,\"itemId\":0}";

        BookingDto dto = objectMapper.readValue(json, BookingDto.class);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getStart()).isNull();
        assertThat(dto.getEnd()).isNull();
        assertThat(dto.getItemId()).isZero();
    }
}
