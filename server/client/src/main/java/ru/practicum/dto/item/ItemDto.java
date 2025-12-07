package ru.practicum.dto.item;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.util.StringUtils;
import ru.practicum.dto.booking.BookingDto;

import java.util.List;

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
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
    private Long requestId;

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
