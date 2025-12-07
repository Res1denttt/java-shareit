package ru.practicum.server.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItem(Item item);

    List<Comment> findAllByItemIn(List<Item> items);
}
