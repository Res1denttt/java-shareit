package ru.practicum.dto.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.dto.item.ItemDto;

import java.time.Instant;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
public class ItemRequestDto {
    private long id;
    private String description;
    private Instant created;
    private List<ItemDto> items;
}
