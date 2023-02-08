package doan.middle_project.common.vo;

import lombok.Data;

@Data
public class SaveBlogRequest {
    private Integer blogId;
    private String title;
    private String content;
}
