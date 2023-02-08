package doan.middle_project.common.vo;

import lombok.Data;

@Data
public class SaveBlogCommentRequest {

    private Integer blogId;
    private Integer blogCommentId;
    private String content;
}
