package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Boolean available;

    public boolean hasName() {
        return StringUtils.hasText(name);
    }

    public boolean hasDescription() {
        return StringUtils.hasText(description);
    }

    public boolean hasIsAvailable() {
        return available != null;
    }
}
