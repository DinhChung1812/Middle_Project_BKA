package doan.middle_project.serviceImpl;

import doan.middle_project.exception.StatusCode;
import doan.middle_project.exception.NotFoundException;
import doan.middle_project.common.vo.AccountManageVo;
import doan.middle_project.common.vo.MessageVo;
import doan.middle_project.repositories.AccountRepository;
import doan.middle_project.service.AccountService;
import doan.middle_project.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;


@Service
@Transactional
public class AccountServiceImpl implements AccountService {


    @Autowired
    private AccountRepository accountRepository;

    @Override
    public void updateResetPassword(String newPassword, String email) throws AccountNotFoundException{

        Account account = accountRepository.findAccountByEmail(email);
        if (account != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(newPassword);
            account.setPassword(encodedPassword);
            accountRepository.save(account);
        } else {
            throw new AccountNotFoundException("Could not find any account with the email " + email);
        }
    }

    @Override
    public void changePassword(String userName, String newPass) throws AccountNotFoundException {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            Account account = accountRepository.findAccountByUserName(userName);
            if(account != null){
                String encodedPassword = passwordEncoder.encode(newPass);
                account.setPassword(encodedPassword);
                accountRepository.save(account);
            }else {
                throw new AccountNotFoundException("Could not find any account with the username " + userName);
            }
    }

    @Override
    public Page<AccountManageVo> findAll(String searchData,Integer pageIndex, Integer pageSize) {
        if (searchData == null) {
            searchData = "";
        }
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return accountRepository.findAll("%" + searchData.trim() + "%",pageable);
    }


    @Override
    public void updateResetPasswordToken(String token, String email) throws AccountNotFoundException{

        Account account = accountRepository.findAccountByEmail(email);
        if (account != null) {
            account.setResetPasswordToken(token);
            accountRepository.save(account);
        } else {
            throw new AccountNotFoundException("Could not find any customer with the email " + email);
        }
    }

    @Override
    public ResponseEntity<?> changeRole(Integer accountId, String role) {
        Account account = accountRepository.findById(accountId).orElseThrow(() ->
                new NotFoundException(StatusCode.Not_Found, "account" + accountId + " Not exist or account was blocked "));;
       if(role.trim().equalsIgnoreCase("admin")){
            account.setRole("ROLE_ADMIN");
            accountRepository.save(account);
           return ResponseEntity.ok(new MessageVo("cập nhật role thành công", "success"));
        } else if (role.trim().equalsIgnoreCase("mod")) {
           account.setRole("ROLE_MOD");
           accountRepository.save(account);
           return ResponseEntity.ok(new MessageVo("cập nhật role thành công", "success"));
       }else if(role.trim().equalsIgnoreCase("user")){
           account.setRole("ROLE_USER");
           accountRepository.save(account);
           return ResponseEntity.ok(new MessageVo("cập nhật role thành công", "success"));
       }else {
           return ResponseEntity.ok(new MessageVo("có gì đó sai sai", "error"));
       }

    }

    @Override
    public ResponseEntity<?> deleteAccount(Integer accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() ->
                new NotFoundException(StatusCode.Not_Found, "account" + accountId + " Not exist or account was blocked "));;
         account.setStatus(3);
         accountRepository.save(account);
        return ResponseEntity.ok(new MessageVo("xóa tài khoản thành công", "success"));
    }

    @Override
    public Account getByResetPasswordToken(String token) {
        return accountRepository.findByResetPasswordToken(token);
    }

    @Override
    public void updatePassword(Account account, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        account.setPassword(encodedPassword);
        account.setResetPasswordToken(null);
        accountRepository.save(account);
    }


}
