package ru.practicum.gateway.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.item.CommentDto;
import ru.practicum.dto.item.ItemDto;
import ru.practicum.dto.item.NewCommentDto;
import ru.practicum.gateway.configuration.BaseClient;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String URL = "/items";

    @Value("${server.host}")
    private String host;

    public ItemClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> findAll(long userId) {
        return get(host + URL, userId);
    }

    public ResponseEntity<Object> findById(long userId, long itemId) {
        return get(host + URL + "/" + itemId, userId);
    }

    public ResponseEntity<Object> create(long userId, ItemDto dto) {
        return post(host + URL, userId, dto);
    }

    public ResponseEntity<Object> update(long userId, ItemDto itemDto, long itemId) {
        return patch(host + URL + "/" + itemId, userId, itemDto);
    }

    public void delete(long userId, long itemId) {
        delete(host + URL + "/" + itemId, userId);
    }

    public ResponseEntity<Object> search(String text) {
        return get(host + URL + "/search?text={text}", 1L, Map.of("text", text));
    }

    public ResponseEntity<Object> createComment(long userId, long itemId, NewCommentDto dto) {
        return post(host + URL + "/" + itemId + "/comment", userId, dto);
    }
}
