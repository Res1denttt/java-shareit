package ru.practicum.server.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.booking.BookingDto;
import ru.practicum.dto.booking.ResponseBookingDto;
import ru.practicum.dto.item.CommentDto;
import ru.practicum.dto.item.ItemDto;
import ru.practicum.dto.item.NewCommentDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.server.booking.BookingService;
import ru.practicum.server.user.UserService;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class ItemServiceImplIntegrationTest {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @Test
    void createComment_shouldCreateComment_whenBookingExists() {
        UserDto ownerDto = new UserDto();
        ownerDto.setName("owner");
        ownerDto.setEmail("owner@mail.ru");
        UserDto owner = userService.create(ownerDto);

        UserDto bookerDto = new UserDto();
        bookerDto.setName("booker");
        bookerDto.setEmail("booker@mail.ru");
        UserDto booker = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("ударная");
        itemDto.setAvailable(true);
        ItemDto item = itemService.create(owner.getId(), itemDto);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(1));
        ResponseBookingDto created = bookingService.create(booker.getId(), bookingDto);
        bookingService.approve(owner.getId(), created.getId(), true);

        NewCommentDto newComment = new NewCommentDto();
        newComment.setText("отличная дрель");

        CommentDto comment = itemService.createComment(booker.getId(), item.getId(), newComment);

        assertThat(comment.getId(), notNullValue());
        assertThat(comment.getText(), is("отличная дрель"));
        assertThat(comment.getAuthorName(), is(booker.getName()));
        assertThat(comment.getItemId(), is(item.getId()));
    }

    @Test
    void createComment_shouldThrow_whenNoBookings() {
        UserDto ownerDto = new UserDto();
        ownerDto.setName("owner2");
        ownerDto.setEmail("owner2@mail.ru");
        UserDto owner = userService.create(ownerDto);

        UserDto userDto = new UserDto();
        userDto.setName("user");
        userDto.setEmail("user@mail.ru");
        UserDto user = userService.create(userDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Молоток");
        itemDto.setDescription("тяжёлый");
        itemDto.setAvailable(true);
        ItemDto item = itemService.create(owner.getId(), itemDto);

        NewCommentDto newComment = new NewCommentDto();
        newComment.setText("комментарий без бронирования");

        var ex = assertThrows(
                ru.practicum.server.exceptions.InvalidOperationException.class,
                () -> itemService.createComment(user.getId(), item.getId(), newComment)
        );

        assertThat(ex.getMessage(), containsString("Можно оставлять отзыв только на товары"));
    }
}
