package ru.practicum.gateway.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.request.NewRequestDto;
import ru.practicum.gateway.configuration.BaseClient;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String URL = "/requests";

    @Value("${shareit-server.url}")
    private String host;

    public ItemRequestClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> create(long userId, NewRequestDto dto) {
        return post(host + URL, userId, dto);
    }

    public ResponseEntity<Object> findUserItemRequests(long userId) {
        return get(host + URL, userId);
    }

    public ResponseEntity<Object> findAllItemRequests(long userId) {
        return get(host + URL + "/all", userId);
    }

    public ResponseEntity<Object> findById(long requestId) {
        return get(host + URL + "/" + requestId);
    }
}
