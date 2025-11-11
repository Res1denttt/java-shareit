package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static Item mapToItem(long userId, ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(userId);
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

    public static Item updateFields(Item oldItem, ItemDto itemDto) {
        Item updatedItem = new Item();
        updatedItem.setName(itemDto.hasName() ? itemDto.getName() : oldItem.getName());
        updatedItem.setDescription(itemDto.hasDescription() ? itemDto.getDescription() : oldItem.getDescription());
        updatedItem.setAvailable(itemDto.hasIsAvailable() ? itemDto.getAvailable() : oldItem.isAvailable());
        updatedItem.setId(itemDto.getId());
        updatedItem.setOwnerId(oldItem.getOwnerId());
        return updatedItem;
    }
}
