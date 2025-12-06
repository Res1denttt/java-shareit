package ru.practicum.server.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.user.UserDto;
import ru.practicum.server.exceptions.ConditionsNotMetException;
import ru.practicum.server.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void testFindAll() {
        Mockito
                .when(repository.findAll())
                .thenReturn(List.of(new User("oleg", "oleg@mail.ru")));

        List<UserDto> userDtos = service.findAll();

        assertThat(userDtos, hasSize(1));
        assertThat(userDtos.get(0).getEmail(), is("oleg@mail.ru"));
        assertThat(userDtos.get(0).getName(), is("oleg"));
        Mockito.verify(repository).findAll();
    }

    @Test
    void testFindById() {
        Mockito
                .when(repository.findById(2L))
                .thenReturn(Optional.of(new User("oleg", "oleg@mail.ru")));

        UserDto userDto = service.findById(2);

        assertThat(userDto.getEmail(), is("oleg@mail.ru"));
        assertThat(userDto.getName(), is("oleg"));
        Mockito.verify(repository).findById(2L);
    }

    @Test
    void findById_shouldThrow_NotFound() {
        Mockito
                .when(repository.findById(5L))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> service.findById(5L)
        );

        assertThat(ex.getMessage(), containsString("Пользователь с id 5 не найден"));
        Mockito.verify(repository).findById(5L);
    }

    @Test
    void create_shouldReturnUserDto() {
        UserDto dto = new UserDto();
        dto.setEmail("oleg@mail.ru");
        dto.setName("oleg");

        Mockito
                .when(repository.save(Mockito.any(User.class)))
                .thenReturn(new User(dto.getName(), dto.getEmail()));

        UserDto result = service.create(dto);

        assertThat(result.getEmail(), is("oleg@mail.ru"));
        assertThat(result.getName(), is("oleg"));
        Mockito.verify(repository).save(Mockito.any(User.class));
    }

    @Test
    void create_shouldThrowException_InvalidEmail() {
        UserDto dto = new UserDto();
        dto.setEmail("  ");
        dto.setName("oleg");

        ConditionsNotMetException ex = assertThrows(
                ConditionsNotMetException.class,
                () -> service.create(dto)
        );

        assertThat(ex.getMessage(), containsString("Email должен быть указан"));
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void update_shouldReturnUserDto() {
        Mockito
                .when(repository.findById(1L))
                .thenReturn(Optional.of(new User("oleg", "oleg@mail.ru")));

        Mockito
                .when(repository.save(Mockito.any(User.class)))
                .thenReturn(new User("alex", "alex@mail.ru"));

        UserDto dto = service.update(1L, new UserDto());

        assertThat(dto.getName(), is("alex"));
        assertThat(dto.getEmail(), is("alex@mail.ru"));
        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).save(Mockito.any(User.class));
    }

    @Test
    void update_shouldThrow_NotFound() {
        Mockito
                .when(repository.findById(10L))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> service.update(10L, new UserDto())
        );

        assertThat(ex.getMessage(), containsString("Пользователь с id 10 не найден"));
        Mockito.verify(repository).findById(10L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void shouldDelete() {
        Mockito
                .when(repository.findById(1L))
                .thenReturn(Optional.of(new User()));

        service.delete(1L);

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).delete(Mockito.any(User.class));
    }

    @Test
    void delete_shouldThrow_NotFound() {
        Mockito
                .when(repository.findById(7L))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> service.delete(7L)
        );

        assertThat(ex.getMessage(), containsString("Пользователь с id 7 не найден"));
        Mockito.verify(repository).findById(7L);
        Mockito.verify(repository, Mockito.never()).delete(Mockito.any(User.class));
    }
}
