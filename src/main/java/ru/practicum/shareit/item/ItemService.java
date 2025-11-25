package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {

    List<OwnerItemDto> findAllForUser(long userId);

    ItemDto findById(long userId, long ItemId);

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, ItemDto itemDto);

    void delete(long userId, long id);

    List<ItemDto> search(String text);

    CommentDto createComment(long userId, long itemId, Comment comment);
}
