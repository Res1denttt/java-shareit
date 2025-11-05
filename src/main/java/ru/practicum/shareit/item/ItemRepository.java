package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    List<Item> findAllForUser(long userId);

    List<Item> findAll();

    Optional<Item> findById(long id);

    Item save(Item item);

    Item update(Item item);

    void delete(Item item);
}
