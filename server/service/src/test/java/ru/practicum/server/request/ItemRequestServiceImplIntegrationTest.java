package ru.practicum.server.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.request.ItemRequestDto;
import ru.practicum.dto.request.NewRequestDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.server.user.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class ItemRequestServiceImplIntegrationTest {

    private final ItemRequestService itemRequestService;
    private final UserService userService;

    @Test
    void findUserRequests_shouldReturnRequestsForUserOrderedByCreatedDesc() {
        UserDto userDto = new UserDto();
        userDto.setName("requester");
        userDto.setEmail("requester@mail.ru");
        UserDto user = userService.create(userDto);

        NewRequestDto r1 = new NewRequestDto();
        r1.setDescription("нужна дрель");
        ItemRequestDto saved1 = itemRequestService.create(user.getId(), r1);

        NewRequestDto r2 = new NewRequestDto();
        r2.setDescription("нужен молоток");
        ItemRequestDto saved2 = itemRequestService.create(user.getId(), r2);

        List<ItemRequestDto> result = itemRequestService.findUserRequests(user.getId());

        assertThat(result, hasSize(2));
        assertThat(result.get(0).getId(), is(saved2.getId()));
        assertThat(result.get(0).getDescription(), is("нужен молоток"));
        assertThat(result.get(1).getId(), is(saved1.getId()));
        assertThat(result.get(1).getDescription(), is("нужна дрель"));
    }
}
