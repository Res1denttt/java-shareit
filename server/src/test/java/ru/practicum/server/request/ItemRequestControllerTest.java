package ru.practicum.server.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.request.ItemRequestDto;
import ru.practicum.dto.request.NewRequestDto;
import ru.practicum.dto.request.ShortItemRequest;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    @Test
    void create_shouldReturnItemRequestDto() throws Exception {
        NewRequestDto newRequest = new NewRequestDto();
        newRequest.setDescription("нужна дрель");

        ItemRequestDto response = ItemRequestDto.builder()
                .id(1L)
                .description("нужна дрель")
                .created(Instant.now())
                .items(List.of())
                .build();

        when(itemRequestService.create(anyLong(), any(NewRequestDto.class)))
                .thenReturn(response);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(newRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(response.getDescription())));
    }

    @Test
    void findUserItemRequests_shouldReturnListOfUserRequests() throws Exception {
        ItemRequestDto r1 = ItemRequestDto.builder()
                .id(1L)
                .description("нужна дрель")
                .created(Instant.now())
                .items(List.of())
                .build();

        ItemRequestDto r2 = ItemRequestDto.builder()
                .id(2L)
                .description("нужен молоток")
                .created(Instant.now())
                .items(List.of())
                .build();

        when(itemRequestService.findUserRequests(anyLong()))
                .thenReturn(List.of(r1, r2));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(r1.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(r1.getDescription())))
                .andExpect(jsonPath("$[1].id", is(r2.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(r2.getDescription())));
    }

    @Test
    void findAllItemRequests_shouldReturnListOfShortRequests() throws Exception {
        ShortItemRequest s1 = new ShortItemRequest() {
            @Override
            public String getDescription() {
                return "нужна дрель";
            }
        };
        ShortItemRequest s2 = new ShortItemRequest() {
            @Override
            public String getDescription() {
                return "нужен молоток";
            }
        };

        when(itemRequestService.findAllRequests(anyLong()))
                .thenReturn(List.of(s1, s2));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].description", is("нужна дрель")))
                .andExpect(jsonPath("$[1].description", is("нужен молоток")));
    }

    @Test
    void findById_shouldReturnItemRequestDto() throws Exception {
        ItemRequestDto response = ItemRequestDto.builder()
                .id(5L)
                .description("нужен шуруповерт")
                .created(Instant.now())
                .items(List.of())
                .build();

        when(itemRequestService.findById(anyLong()))
                .thenReturn(response);

        mvc.perform(get("/requests/{requestId}", 5L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(response.getDescription())));
    }
}
