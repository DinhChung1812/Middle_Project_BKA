package doan.middle_project.common.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlogVo {
    private Integer blogID;

    private String title;

    private String content;

    private Integer totalLike;

    private Integer totalDisLike;

    private Integer numberComment;

    private Integer accountId;

    private String userName;

    private String avatarImage;

    private Integer status;

    private LocalDate createDate;

    private LocalDate updateDate;

    private Integer checkLike;

    private Integer checkDislike;

    private Integer checkEdit;

    public BlogVo(Integer blogID, String title, String content, Integer totalLike, Integer totalDisLike, Integer numberComment, Integer accountId,
                  String userName, String avatarImage, Integer status, LocalDate createDate,  LocalDate updateDate) {
        this.blogID = blogID;
        this.title = title;
        this.content = content;
        this.totalLike = totalLike;
        this.totalDisLike = totalDisLike;
        this.numberComment = numberComment;
        this.accountId = accountId;
        this.userName = userName;
        this.avatarImage = avatarImage;
        this.status = status;
        this.createDate = createDate;
        this.updateDate = updateDate;

    }
}
