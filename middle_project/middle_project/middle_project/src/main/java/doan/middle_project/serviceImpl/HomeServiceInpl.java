package doan.middle_project.serviceImpl;

import doan.middle_project.exception.StatusCode;
import doan.middle_project.exception.NotFoundException;
import doan.middle_project.dto.Requests.ProfileEditRequest;
import doan.middle_project.dto.Requests.ProfileRequest;
import doan.middle_project.dto.Responds.MessageResponse;
import doan.middle_project.repositories.AccountRepository;
import doan.middle_project.service.HomeService;
import doan.middle_project.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class HomeServiceInpl implements HomeService {

    @Autowired
    AccountRepository accountRepository;

    @Override
    public ProfileRequest getProfile(Integer profileId) throws NotFoundException {
        Account account = accountRepository.findById(profileId).orElseThrow(() -> new NotFoundException(StatusCode.Not_Found,"Không tìm thấy người dùng!!!"));
        ProfileRequest request = new ProfileRequest();
        request.setProfileId(account.getAccountId());
        request.setName(account.getName());
        request.setUserName(account.getUserName());
        request.setAddress(account.getAddress());
        request.setDob(account.getDob());
        request.setEmail(account.getEmail());
        request.setGender(account.getGender());
        request.setHigh(account.getHigh());
        request.setPhone(account.getPhone());
        request.setWeight(account.getWeight());
        request.setStatus(account.getStatus());
        request.setRole(account.getRole());
        request.setAvatarImage(account.getAvatarImage());
        request.setUpdateDate(account.getUpdateDate());
        request.setCreateDate(account.getCreateDate());
        return request;
    }

    @Override
    public ResponseEntity<?> updateProfile(Integer profileId, ProfileEditRequest profileRequest) throws NotFoundException {
        Account account = accountRepository.findById(profileId).orElseThrow(() -> new NotFoundException(StatusCode.Not_Found,"Không tìm thấy người dùng!!!"));
        account.setName(profileRequest.getName());
        account.setAddress(profileRequest.getAddress());
        account.setDob(profileRequest.getDob());
        account.setEmail(profileRequest.getEmail());
        account.setGender(profileRequest.getGender());
        account.setHigh(profileRequest.getHigh());
        account.setPhone(profileRequest.getPhone());
        account.setWeight(profileRequest.getWeight());
        account.setUpdateDate(LocalDate.now());
        account.setAvatarImage(profileRequest.getAvatarImage());
        accountRepository.save(account);
        return ResponseEntity.ok(new MessageResponse(StatusCode.Success,"Cập nhật profile thành công"));
    }

    @Override
    public ResponseEntity<?> updateImage(Integer profileId, String image) {
        Account a = accountRepository.findById(profileId).orElseThrow(()->new NotFoundException(StatusCode.Not_Found,"Tài khoản đã bị khóa hoặc không tìm thấy"));
        a.setAvatarImage(image);
        accountRepository.save(a);
        return ResponseEntity.ok(new MessageResponse(StatusCode.Success,"Cập nhật ảnh thành công"));
    }
}
