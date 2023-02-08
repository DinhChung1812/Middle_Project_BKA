package doan.middle_project.exception;

import lombok.Data;

@Data
public class StatusCode {
    public static final int Not_Found = 80801;
    public static final int INTERNAL_SERVER_ERROR = Not_Found + 1;

    public static final int Success = Not_Found + 2;

    public static final int Duplicate = Not_Found + 3;

    public static final int Lack_Of_Information = Not_Found + 4;

}
