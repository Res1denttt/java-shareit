package ru.practicum.gateway.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.user.UserDto;
import ru.practicum.gateway.configuration.BaseClient;


@Service
public class UserClient extends BaseClient {
    private static final String URL = "/users";

    @Value("${shareit-server.url}")
    private String host;

    public UserClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> findAll() {
        return get(host + URL);
    }

    public ResponseEntity<Object> findById(long id) {
        return get(host + URL + "/" + id);
    }

    public ResponseEntity<Object> create(UserDto userDto) {
        return post(host + URL, userDto);
    }

    public ResponseEntity<Object> update(long id, UserDto userDto) {
        return patch(host + URL + "/" + id, userDto);
    }

    public void delete(long id) {
        delete(host + URL + "/" + id);
    }
}