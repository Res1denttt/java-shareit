package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {

    public static Item mapToItem(User user, ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        return item;
    }

    public static ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());
        return itemDto;
    }

    public static List<ItemDto> mapToItemDto(Iterable<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(mapToItemDto(item));
        }
        return dtos;
    }

    public static ItemDto mapToItemDto(Item item, List<CommentDto> comments) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());
        itemDto.setComments(comments);
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

    public static List<OwnerItemDto> mapToOwnerItemDto(Iterable<Item> items, BookingDto last, BookingDto next, List<CommentDto> comments) {
        List<OwnerItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(mapToOwnerItemDto(item, last, next, comments));
        }
        return dtos;
    }
}
