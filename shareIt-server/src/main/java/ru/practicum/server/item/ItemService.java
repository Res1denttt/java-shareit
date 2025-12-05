package ru.practicum.server.item;

import ru.practicum.dto.item.CommentDto;
import ru.practicum.dto.item.ItemDto;
import ru.practicum.dto.item.NewCommentDto;
import ru.practicum.dto.item.OwnerItemDto;
import ru.practicum.server.item.model.Comment;

import java.util.List;

public interface ItemService {

    List<OwnerItemDto> findAllForUser(long userId);

    ItemDto findById(long userId, long itemId);

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, ItemDto itemDto);

    void delete(long userId, long id);

    List<ItemDto> search(String text);

    CommentDto createComment(long userId, long itemId, NewCommentDto dto);
}
