package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(long userId, Sort sort);

    @Query("""
            select booking
            from Booking booking
            where booking.booker.id = ?1
                and booking.start <= current_timestamp
                and booking.end > current_timestamp
            order by booking.start
            """)
    List<Booking> findCurrentByBookerId(long userId);

    List<Booking> findAllByBooker_IdAndEndIsBefore(long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByBooker_IdAndStartIsAfter(long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByBooker_IdAndStatus(long userId, BookingStatus status, Sort sort);

    List<Booking> findAllByItem_Owner_Id(long ownerId, Sort sort);

    List<Booking> findAllByItemOwner_IdAndStatus(long ownerId, BookingStatus status, Sort sort);

    @Query("""
            select booking
            from Booking booking
            where booking.item.owner.id = ?1
                 and booking.start <= current_timestamp
                 and booking.end > current_timestamp
            order by booking.start
            """)
    List<Booking> findCurrentByOwnerId(long ownerId);

    List<Booking> findAllByItem_Owner_IdAndEndIsBefore(long ownerId, OffsetDateTime now, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStartIsAfter(long ownerId, OffsetDateTime now, Sort sort);

    @Query("""
            select booking
            from Booking booking
            where booking.item = :item
                and booking.booker = :booker
                and booking.status = 'APPROVED'
                and booking.start < :now
            """)
    List<Booking> findBookingsByItemAndBooker(Item item, User booker, LocalDateTime now);
}

