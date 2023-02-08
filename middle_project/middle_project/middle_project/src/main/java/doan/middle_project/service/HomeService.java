package doan.middle_project.service;

import doan.middle_project.exception.BadRequestException;
import doan.middle_project.dto.Requests.ProfileEditRequest;
import doan.middle_project.dto.Requests.ProfileRequest;
import org.springframework.http.ResponseEntity;

public interface HomeService {
    ProfileRequest getProfile(Integer username);

    ResponseEntity<?> updateProfile(Integer profileId, ProfileEditRequest profileRequest) throws BadRequestException;

    ResponseEntity<?> updateImage(Integer profileId, String image);
}
