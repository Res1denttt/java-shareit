package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.InvalidOperationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    @Override
    public List<OwnerItemDto> findAllForUser(long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        return items.stream()
                .map(this::mapItemWithLastAndNextBookings)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto findById(long userId, long itemId) {
        Item item = itemRepository.findByIdWithRelations(itemId);
        return item.getOwner().getId() == userId ? mapItemWithLastAndNextBookings(item) : ItemMapper.mapToItemDto(item);
    }

    private OwnerItemDto mapItemWithLastAndNextBookings(Item item) {

        LocalDateTime now = LocalDateTime.now();

        Booking lastBooking = item.getBookings().stream()
                .filter(b -> b.getEnd().isBefore(now))
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);

        Booking nextBooking = item.getBookings().stream()
                .filter(b -> b.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);

        return ItemMapper.mapToOwnerItemDto(
                item,
                lastBooking != null ? BookingMapper.mapToBookingDto(lastBooking) : null,
                nextBooking != null ? BookingMapper.mapToBookingDto(nextBooking) : null,
                CommentMapper.mapToDto(item.getComments()));
    }


    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        User user = getUser(userId);
        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос товвара с id " + itemDto.getRequestId() + " не найден"));
        }
        Item item = ItemMapper.mapToItem(user, itemDto, request);
        itemRepository.save(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto update(long userId, ItemDto itemDto) {
        if (!userRepository.existsById(userId))
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        Item oldItem = getItem(itemDto.getId());
        if (oldItem.getOwner().getId() != userId)
            throw new InvalidOperationException("Можно редактировать только свои товары");
        Item updatedItem = ItemMapper.updateFields(oldItem, itemDto);
        itemRepository.save(updatedItem);
        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public void delete(long userId, long id) {
        Item item = getItem(id);
        if (item.getOwner().getId() != userId) throw new InvalidOperationException("Можно удалять только свои товары");
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
        User user = getUser(userId);
        Item item = getItem(itemId);
        List<Booking> bookings = bookingRepository.findBookingsByItemAndBooker(item, user, LocalDateTime.now());
        if (bookings.isEmpty())
            throw new InvalidOperationException("Можно оставлять отзыв только на товары, на которые было сделано бронирование");
        comment.setAuthorName(user.getName());
        comment.setItem(item);
        Comment saved = commentRepository.save(comment);
        return new CommentDto(saved.getId(), saved.getText(), saved.getAuthorName(), saved.getCreated(), saved.getItem().getId());
    }

    private Item getItem(long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item с id = " + id + " не найден"));
    }

    private User getUser(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }
}
