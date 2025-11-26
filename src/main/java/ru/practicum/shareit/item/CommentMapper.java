package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    public static CommentDto mapToDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getAuthorName(), comment.getCreated(), comment.getItem().getId());
    }

    public static List<CommentDto> mapToDto(Iterable<Comment> comments) {
        if (comments == null) return List.of();
        List<CommentDto> dtos = new ArrayList<>();
        for (Comment comment : comments) {
            dtos.add(mapToDto(comment));
        }
        return dtos;
    }
}
