package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> findAllForUser(long userId);

    ItemDto findById(long id);

    ItemDto save(long userId, ItemDto itemDto);

    ItemDto update(long userId, ItemDto itemDto);

    void delete(long userId, long id);

    List<ItemDto> search(String text);
}
