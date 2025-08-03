package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Friendship {
    private int userId;
    private int friendId;
    private FriendshipStatus status;
    private LocalDateTime createdDate;

    public Friendship(int userId, int friendId, FriendshipStatus status) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
        this.createdDate = LocalDateTime.now();
    }
}
