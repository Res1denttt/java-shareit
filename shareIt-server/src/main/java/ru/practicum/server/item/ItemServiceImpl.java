package ru.practicum.server.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.item.NewCommentDto;
import ru.practicum.server.booking.Booking;
import ru.practicum.server.booking.BookingMapper;
import ru.practicum.server.booking.BookingRepository;
import ru.practicum.server.exceptions.InvalidOperationException;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.dto.item.CommentDto;
import ru.practicum.dto.item.ItemDto;
import ru.practicum.dto.item.OwnerItemDto;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.request.ItemRequest;
import ru.practicum.server.request.ItemRequestRepository;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    @Transactional
    @Override
    public List<OwnerItemDto> findAllForUser(long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        return items.stream()
                .map(this::mapItemWithLastAndNextBookings)
                .toList();
    }

    @Override
    public ItemDto findById(long userId, long itemId) {
        Item item = itemRepository.findByIdWithRelations(itemId);
        return item.getOwner().getId() == userId ? mapItemWithLastAndNextBookings(item) : ItemMapper.mapToItemDto(item);
    }

    private OwnerItemDto mapItemWithLastAndNextBookings(Item item) {
        LocalDateTime now = LocalDateTime.now();

        Booking lastBooking = null;
        Booking nextBooking = null;

        if (item.getBookings() != null) {
            lastBooking = item.getBookings().stream()
                    .filter(b -> b.getEnd() != null && b.getEnd().isBefore(now))
                    .max(Comparator.comparing(Booking::getEnd))
                    .orElse(null);

            nextBooking = item.getBookings().stream()
                    .filter(b -> b.getStart() != null && b.getStart().isAfter(now))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);
        }

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
        item = itemRepository.save(item);
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
        updatedItem = itemRepository.save(updatedItem);
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
    public CommentDto createComment(long userId, long itemId, NewCommentDto dto) {
        User user = getUser(userId);
        Item item = getItem(itemId);
        List<Booking> bookings = bookingRepository.findBookingsByItemAndBooker(item, user, LocalDateTime.now());
        if (bookings.isEmpty())
            throw new InvalidOperationException("Можно оставлять отзыв только на товары, на которые было сделано бронирование");
        Comment comment = CommentMapper.mapToComment(dto);
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
