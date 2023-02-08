package doan.middle_project.common.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FriendsVo {

    private Integer usernameID;

    private String fullname;

    private String avatar;

    public FriendsVo(Integer usernameID) {
        this.usernameID = usernameID;
    }
}
