package ru.practicum.server.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.booking.BookingDto;
import ru.practicum.dto.item.CommentDto;
import ru.practicum.dto.item.ItemDto;
import ru.practicum.dto.item.OwnerItemDto;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.request.ItemRequest;
import ru.practicum.server.user.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {

    public static Item mapToItem(User user, ItemDto itemDto, ItemRequest request) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        item.setRequest(request);
        return item;
    }

    public static ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());
        itemDto.setComments(CommentMapper.mapToDto(item.getComments()));
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        return itemDto;
    }

    public static Item updateFields(Item oldItem, ItemDto itemDto) {
        Item updatedItem = new Item();
        updatedItem.setName(itemDto.hasName() ? itemDto.getName() : oldItem.getName());
        updatedItem.setDescription(itemDto.hasDescription() ? itemDto.getDescription() : oldItem.getDescription());
        updatedItem.setAvailable(itemDto.hasIsAvailable() ? itemDto.getAvailable() : oldItem.isAvailable());
        updatedItem.setId(itemDto.getId());
        updatedItem.setOwner(oldItem.getOwner());
        return updatedItem;
    }

    public static OwnerItemDto mapToOwnerItemDto(Item item, BookingDto last, BookingDto next, List<CommentDto> comments) {
        OwnerItemDto itemDto = new OwnerItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());
        itemDto.setLastBooking(last);
        itemDto.setNextBooking(next);
        itemDto.setComments(comments);
        return itemDto;
    }
}
