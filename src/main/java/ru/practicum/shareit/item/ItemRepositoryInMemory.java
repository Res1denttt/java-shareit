package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemRepositoryInMemory implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private static long currentId = 1;

    @Override
    public List<Item> findAllForUser(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId() == userId)
                .toList();
    }

    @Override
    public List<Item> findAll() {
        return items.values().stream().toList();
    }

    @Override
    public Optional<Item> findById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item save(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        return items.put(item.getId(), item);
    }

    @Override
    public void delete(Item item) {
        items.remove(item.getId());
    }

    private static long generateId() {
        return currentId++;
    }
}
