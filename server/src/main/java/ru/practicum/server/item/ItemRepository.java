package ru.practicum.server.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.server.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(long userId);

    @Query("""
             select item
             from Item item
             where item.available = true
               and (
                     lower(item.name) like lower(concat('%', :text, '%'))
                     or lower(item.description) like lower(concat('%', :text, '%'))
                   )
            """)
    List<Item> search(@Param("text") String text);

    @Query("""
            select i
            from Item i
            left join fetch i.bookings
            where i.id = :id
            """)
    Item findByIdWithRelations(long id);

}
