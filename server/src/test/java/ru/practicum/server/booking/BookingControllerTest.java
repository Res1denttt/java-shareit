package ru.practicum.server.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.booking.BookingDto;
import ru.practicum.dto.booking.ResponseBookingDto;
import ru.practicum.dto.item.ItemDto;
import ru.practicum.dto.user.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService service;

    @Autowired
    private MockMvc mvc;

    private ItemDto buildItemDto() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(3L);
        itemDto.setName("Дрель");
        itemDto.setDescription("ударная");
        itemDto.setAvailable(true);
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);
        itemDto.setComments(List.of());
        itemDto.setRequestId(null);
        return itemDto;
    }

    private final ResponseBookingDto responseDto = ResponseBookingDto.builder()
            .id(1L)
            .start(LocalDateTime.now().plusHours(1))
            .end(LocalDateTime.now().plusHours(2))
            .status("APPROVED")
            .booker(new UserDto(2L, "booker@mail.ru", "booker"))
            .item(buildItemDto())
            .build();

    @Test
    void create_shouldReturnBooking() throws Exception {
        BookingDto dto = new BookingDto(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                3L
        );

        when(service.create(anyLong(), any(BookingDto.class)))
                .thenReturn(responseDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(responseDto.getStatus())))
                .andExpect(jsonPath("$.booker.id", is(responseDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(responseDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(responseDto.getItem().getName())))
                .andExpect(jsonPath("$.item.available", is(responseDto.getItem().getAvailable())));
    }

    @Test
    void approve_shouldReturnUpdatedBooking() throws Exception {
        when(service.approve(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(responseDto);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(responseDto.getStatus())));
    }

    @Test
    void findById_shouldReturnBooking() throws Exception {
        when(service.findById(anyLong(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 2L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(responseDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(responseDto.getItem().getId()), Long.class));
    }

    @Test
    void findByState_shouldReturnList() throws Exception {
        when(service.findByState(anyLong(), any(BookingState.class)))
                .thenReturn(List.of(responseDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(responseDto.getStatus())))
                .andExpect(jsonPath("$[0].item.id", is(responseDto.getItem().getId()), Long.class));
    }

    @Test
    void findForOwnerByState_shouldReturnList() throws Exception {
        when(service.findForOwnerByState(anyLong(), any(BookingState.class)))
                .thenReturn(List.of(responseDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(responseDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(responseDto.getItem().getName())));
    }
}