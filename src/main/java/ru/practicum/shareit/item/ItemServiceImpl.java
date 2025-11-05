package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.InvalidOperation;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserService userService;

    @Override
    public List<ItemDto> findAllForUser(long userId) {
        return repository.findAllForUser(userId).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public ItemDto findById(long id) {
        Item item = repository.findById(id).orElseThrow(() -> new NotFoundException("Item с id = " + id + " не найден"));
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto save(long userId, ItemDto itemDto) {
        User user = userService.findById(userId);
        if (user == null) throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        Item item = ItemMapper.mapToItem(userId, itemDto);
        repository.save(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto update(long userId, ItemDto itemDto) {
        User user = userService.findById(userId);
        if (user == null) throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        Item oldItem = repository.findById(itemDto.getId()).orElseThrow(() -> new NotFoundException("Item с id = " +
                itemDto.getId() + " не найден"));
        if (oldItem.getOwnerId() != userId) throw new InvalidOperation("Можно редактировать только свои товары");
        Item updatedItem = ItemMapper.updateFields(oldItem, itemDto);
        repository.update(updatedItem);
        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public void delete(long userId, long id) {
        Item item = repository.findById(id).orElseThrow(() -> new NotFoundException("Item с id = " + id + " не найден"));
        if (item.getOwnerId() != userId) throw new InvalidOperation("Можно удалять только свои товары");
        repository.delete(item);
    }

    @Override
    public List<ItemDto> search(String text) {
        return repository.findAll().stream()
                .filter(Item::isAvailable)
                .filter(i -> i.getName().toLowerCase().contains(text.toLowerCase()) ||
                        i.getDescription().toLowerCase().contains(text.toLowerCase()))
                .map(ItemMapper::mapToItemDto)
                .toList();
    }
}
