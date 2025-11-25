package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ItemRepositoryInMemory {

    private final Map<Long, Item> items = new HashMap<>();
    private static long currentId = 1;

    public List<Item> findAllForUser(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .toList();
    }

    public List<Item> findAll() {
        return items.values().stream().toList();
    }

    public Optional<Item> findById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    public Item save(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }

    public Item update(Item item) {
        return items.put(item.getId(), item);
    }

    public void delete(Item item) {
        items.remove(item.getId());
    }

    public List<Item> search(String text) {
        return items.values().stream()
                .filter(Item::isAvailable)
                .filter(i -> i.getName().toLowerCase().contains(text.toLowerCase()) ||
                        i.getDescription().toLowerCase().contains(text.toLowerCase()))
                .toList();
    }

    private static long generateId() {
        return currentId++;
    }
}
