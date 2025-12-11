package ru.practicum.server.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.booking.BookingDto;
import ru.practicum.dto.booking.ResponseBookingDto;
import ru.practicum.dto.item.ItemDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.server.item.ItemService;
import ru.practicum.server.user.UserService;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;

    @Test
    void approve_shouldSetStatusApprovedAndPersist() {
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
        itemDto.setDescription("Ударная");
        itemDto.setAvailable(true);
        itemDto.setRequestId(null);
        ItemDto item = itemService.create(owner.getId(), itemDto);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));

        ResponseBookingDto created = bookingService.create(booker.getId(), bookingDto);

        ResponseBookingDto approved =
                bookingService.approve(owner.getId(), created.getId(), true);

        assertThat(approved.getStatus(), equalTo(BookingStatus.APPROVED.toString()));

        Booking dbBooking = bookingRepository.findById(created.getId()).orElseThrow();
        assertThat(dbBooking.getStatus(), equalTo(BookingStatus.APPROVED));
        assertThat(dbBooking.getItem().getId(), equalTo(item.getId()));
        assertThat(dbBooking.getBooker().getId(), equalTo(booker.getId()));
    }
}

