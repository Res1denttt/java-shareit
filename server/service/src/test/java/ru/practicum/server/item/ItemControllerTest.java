package ru.practicum.server.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.item.CommentDto;
import ru.practicum.dto.item.ItemDto;
import ru.practicum.dto.item.NewCommentDto;
import ru.practicum.dto.item.OwnerItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService service;

    @Autowired
    private MockMvc mvc;

    private ItemDto buildItemDto() {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Дрель");
        dto.setDescription("ударная");
        dto.setAvailable(true);
        dto.setLastBooking(null);
        dto.setNextBooking(null);
        dto.setComments(List.of());
        dto.setRequestId(null);
        return dto;
    }

    private OwnerItemDto buildOwnerItemDto() {
        OwnerItemDto dto = new OwnerItemDto();
        dto.setId(1L);
        dto.setName("Дрель");
        dto.setDescription("ударная");
        dto.setAvailable(true);
        dto.setLastBooking(null);
        dto.setNextBooking(null);
        dto.setComments(List.of());
        return dto;
    }

    @Test
    void findAll_shouldReturnOwnerItemsForUser() throws Exception {
        OwnerItemDto ownerItem = buildOwnerItemDto();

        when(service.findAllForUser(anyLong()))
                .thenReturn(List.of(ownerItem));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(ownerItem.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(ownerItem.getName())))
                .andExpect(jsonPath("$[0].description", is(ownerItem.getDescription())));
    }

    @Test
    void findById_shouldReturnItemDto() throws Exception {
        ItemDto itemDto = buildItemDto();

        when(service.findById(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void create_shouldReturnCreatedItem() throws Exception {
        ItemDto itemDto = buildItemDto();

        when(service.create(anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void update_shouldReturnUpdatedItem() throws Exception {
        ItemDto itemDto = buildItemDto();

        when(service.update(anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    void delete_shouldCallService() throws Exception {
        mvc.perform(delete("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).delete(1L, 1L);
    }

    @Test
    void search_shouldReturnListWhenTextNotBlank() throws Exception {
        ItemDto itemDto = buildItemDto();

        when(service.search(anyString()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .param("text", "дрель")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())));
    }

    @Test
    void search_shouldReturnEmptyListWhenTextBlank() throws Exception {
        mvc.perform(get("/items/search")
                        .param("text", " ")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void createComment_shouldReturnCommentDto() throws Exception {
        NewCommentDto newCommentDto = new NewCommentDto();
        newCommentDto.setText("отличная дрель");

        CommentDto commentDto = new CommentDto(
                1L,
                "отличная дрель",
                "user",
                LocalDateTime.now(),
                1L
        );

        when(service.createComment(anyLong(), anyLong(), any(NewCommentDto.class)))
                .thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(newCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.itemId", is(commentDto.getItemId()), Long.class));
    }
}
