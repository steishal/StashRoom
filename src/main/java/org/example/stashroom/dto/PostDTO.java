package org.example.stashroom.dto;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
public class PostDTO {
    private Long id;
    private String content;
    private UserDTO author;
    private CategoryDTO category;
    private LocalDateTime createDate;
    private List<String> images;
    private int likeCount;
    private boolean likedByCurrentUser;
    private int commentsCount;
}
