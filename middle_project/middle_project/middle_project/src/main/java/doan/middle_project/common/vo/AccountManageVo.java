package doan.middle_project.common.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountManageVo {
    private Integer accountId;
    private String userName;
    private String role;
    private String email;
    private String avatarImage;
    private String createDate;
}
