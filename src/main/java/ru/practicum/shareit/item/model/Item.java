package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private boolean available;
    private long ownerId;
}
