package doan.middle_project.common.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "old Password cannot blank")
    @NotNull(message = "old Password cannot null")
    private String oldPassword;

    @NotBlank(message = "new Password cannot blank")
    @NotNull(message = "new Password cannot null")
    private String newPassword;
}
