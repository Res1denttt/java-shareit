package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.InvalidOperation;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<OwnerItemDto> findAllForUser(long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);

        List<Booking> bookings = bookingRepository.findBookingsForItems(items, LocalDateTime.now());
        Map<Long, List<Booking>> bookingsByItem = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        List<CommentDto> allComments = commentRepository.findAllByItemIn(items).stream()
                .map(CommentMapper::mapToDto) // маппим в DTO сразу
                .toList();
        Map<Long, List<CommentDto>> commentsByItem = allComments.stream()
                .collect(Collectors.groupingBy(CommentDto::getItemId));

        return items.stream()
                .map(item -> mapItemWithBookingsAndComments(item, bookingsByItem, commentsByItem))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto findById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item с id = " + itemId + " не найден"));

        List<CommentDto> comments = commentRepository.findAllByItem(item).stream()
                .map(CommentMapper::mapToDto)
                .toList();

        if (item.getOwner().getId() == userId) {
            List<Booking> bookings = bookingRepository.findBookingsForItems(List.of(item), LocalDateTime.now());
            Map<Long, List<Booking>> bookingsByItem = bookings.stream()
                    .collect(Collectors.groupingBy(b -> b.getItem().getId()));
            return mapItemWithBookingsAndComments(item, bookingsByItem, Map.of(item.getId(), comments));
        }
        return ItemMapper.mapToItemDto(item, comments); // DTO без ленивых сущностей
    }

    private OwnerItemDto mapItemWithBookingsAndComments(Item item,
                                                        Map<Long, List<Booking>> bookingsByItem,
                                                        Map<Long, List<CommentDto>> commentsByItem) {
        List<Booking> itemBookings = bookingsByItem.getOrDefault(item.getId(), Collections.emptyList());
        List<CommentDto> itemComments = commentsByItem.getOrDefault(item.getId(), Collections.emptyList());

        LocalDateTime now = LocalDateTime.now();

        Booking lastBooking = itemBookings.stream()
                .filter(b -> b.getEnd().isBefore(now))
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);

        Booking nextBooking = itemBookings.stream()
                .filter(b -> b.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);

        // Передаём DTO вместо сущностей
        return ItemMapper.mapToOwnerItemDto(
                item,
                lastBooking != null ? BookingMapper.mapToBookingDto(lastBooking) : null,
                nextBooking != null ? BookingMapper.mapToBookingDto(nextBooking) : null,
                itemComments
        );
    }


    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Item item = ItemMapper.mapToItem(user, itemDto);
        itemRepository.save(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto update(long userId, ItemDto itemDto) {
        if (!userRepository.existsById(userId))
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        Item oldItem = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new NotFoundException("Item с id = " + itemDto.getId() + " не найден"));
        if (oldItem.getOwner().getId() != userId) throw new InvalidOperation("Можно редактировать только свои товары");
        Item updatedItem = ItemMapper.updateFields(oldItem, itemDto);
        itemRepository.save(updatedItem);
        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public void delete(long userId, long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item с id = " + id + " не найден"));
        if (item.getOwner().getId() != userId) throw new InvalidOperation("Можно удалять только свои товары");
        itemRepository.delete(item);
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemRepository.search(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public CommentDto createComment(long userId, long itemId, Comment comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item с id = " + itemId + " не найден"));
        List<Booking> bookings = bookingRepository.findBookingsByItemAndBooker(item, user, LocalDateTime.now());
        if (bookings.isEmpty())
            throw new InvalidOperation("Можно оставлять отзыв только на товары, на которые было сделано бронирование");
        comment.setAuthorName(user.getName());
        comment.setItem(item);
        Comment saved = commentRepository.save(comment);
        return new CommentDto(saved.getId(), saved.getText(), saved.getAuthorName(), saved.getCreated(), saved.getItem().getId());
    }
}
