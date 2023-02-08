package doan.middle_project.dto.Requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileRequest {

    private int profileId;

    private String name;

    private String userName;

    private String email;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dob;

    private String gender;

    private String phone;

    private String address;

    private int status;

    private String role;

    private Double high;

    private Double weight;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate createDate;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate updateDate;

    private String avatarImage;

}
