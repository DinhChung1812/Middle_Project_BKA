package doan.middle_project.controller;

import doan.middle_project.common.logging.LogUtils;
import doan.middle_project.common.vo.AccountManageVo;
import doan.middle_project.common.vo.MessageVo;
import doan.middle_project.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountManageController {

    @Value("${pageSize}")
    private Integer pageSize;
    @Autowired
    private AccountService accountService;

    @GetMapping("/admin/listAccount")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllAccountActive(Model model, @RequestParam(required = false) String searchData,
                                                 @RequestParam(required = false) Integer pageIndex) {
        LogUtils.getLog().info("START getAllAccountActive");
        if (pageIndex == null) {
            pageIndex = 1;
        }
        Page<AccountManageVo> listAccountActive = accountService.findAll(searchData,pageIndex-1, pageSize );
        if(listAccountActive.toList() == null || listAccountActive.toList().size() == 0){
            return ResponseEntity.ok(new MessageVo("Không tìm thấy tài khoản " + searchData, "info"));
        }
        model.addAttribute("listAccountActive", listAccountActive.toList());
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("numOfPages", listAccountActive.getTotalPages());
        LogUtils.getLog().info("END getAllAccountActive");
        return ResponseEntity.ok(model);
    }
    @PostMapping ("/admin/changeRole")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> changeRole( @RequestParam Integer accountId,
                                         @RequestParam String role) {
        return accountService.changeRole(accountId,role);
    }

    @PostMapping ("/admin/deleteAccount")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteAccount( @RequestParam Integer accountId) {
        return accountService.deleteAccount(accountId);
    }


}
