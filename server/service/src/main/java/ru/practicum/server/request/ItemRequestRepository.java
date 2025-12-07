package ru.practicum.server.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.dto.request.ShortItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByUserIdOrderByCreatedDesc(long userId);

    List<ShortItemRequest> findAllByUserIdNotOrderByCreatedDesc(long userId);

}
