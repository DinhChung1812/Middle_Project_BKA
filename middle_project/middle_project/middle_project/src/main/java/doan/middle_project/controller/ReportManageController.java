package doan.middle_project.controller;

import doan.middle_project.common.logging.LogUtils;
import doan.middle_project.common.vo.BlogCommentAccountVo;
import doan.middle_project.service.BlogCommentService;
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
public class ReportManageController {

    @Value("${pageSize}")
    private Integer pageSize;

    @Autowired
    private BlogCommentService blogCommentService;

    @GetMapping("/admin/getListBlogCommentReport")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getListBlogCommentReport(Model model, @RequestParam(required = false) String searchData ,
                                                      @RequestParam(required = false) Integer pageIndex) {
        LogUtils.getLog().info("START getListBlogCommentReport");
        if (pageIndex == null) {
            pageIndex = 1;
        }
        Page<BlogCommentAccountVo> blogCommentAccountVos = blogCommentService.findReportBlogComment(searchData,pageIndex-1, pageSize);
        model.addAttribute("dishCommentAccountVoList", blogCommentAccountVos.toList());
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("numOfPages", blogCommentAccountVos.getTotalPages());
        LogUtils.getLog().info("END getListBlogCommentReport");
        return ResponseEntity.ok(model);
    }

    @PostMapping ("/admin/approveBlogComment")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> approveBlogComment(@RequestParam Integer blogCommentId ) {
        return blogCommentService.approveComment(blogCommentId);
    }

}
