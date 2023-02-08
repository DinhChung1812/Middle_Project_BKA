package doan.middle_project.common.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BlogCommentAccountVo {
    private Integer blogCommentID;
    private String content;
    private Integer flag;
    private Integer totalLike;
    private Integer totalDisLike;
    private String createDate;
    private String updateDate;
    private String accountUserName;
    private String avatarImage;
    private Integer checkLike;
    private Integer checkDislike;
    private Integer checkEdit;

    public BlogCommentAccountVo(Integer blogCommentID, String content, Integer flag, Integer totalLike, Integer totalDisLike, String createDate, String updateDate, String accountUserName, String avatarImage) {
        this.blogCommentID = blogCommentID;
        this.content = content;
        this.flag = flag;
        this.totalLike = totalLike;
        this.totalDisLike = totalDisLike;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.accountUserName = accountUserName;
        this.avatarImage = avatarImage;
    }
}
