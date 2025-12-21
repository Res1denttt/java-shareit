package ru.practicum.server.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.user.UserDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User updateUserFields(User user, UserDto userDto) {
        User updated = new User();
        updated.setId(user.getId());
        updated.setName(userDto.hasName() ? userDto.getName() : user.getName());
        updated.setEmail(userDto.hasEmail() ? userDto.getEmail() : user.getEmail());
        return updated;
    }

    public static User mapToUser(UserDto dto) {
        return new User(dto.getName(), dto.getEmail());
    }

    public static UserDto mapToUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}
