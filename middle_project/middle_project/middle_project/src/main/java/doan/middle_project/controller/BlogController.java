package doan.middle_project.controller;

import doan.middle_project.common.logging.LogUtils;
import doan.middle_project.common.vo.*;
import doan.middle_project.repositories.*;
import doan.middle_project.service.BlogCommentService;
import doan.middle_project.service.BlogService;
import doan.middle_project.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

@Controller
public class BlogController {

    @Value("${pageSize}")
    private Integer pageSize;
    @Autowired
    private BlogService blogService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BlogCommentService blogCommentService;

    @Autowired
    private CheckLikeDislikeBlogRepository checkLikeDislikeBlogRepository;

    @Autowired
    private CheckLikeDislikeReportBCRepository checkLikeDislikeReportBCRepository;

    //danh sách blog, màn home của blog, search theo username ng tạo và title
    //Tuần 3: Truy vấn bài
    @GetMapping("/getListBlog")
    public ResponseEntity<?> getListBlog(Model model, @RequestParam(required = false) String searchData,
                                         @RequestParam(required = false) Integer pageIndex) {
        LogUtils.getLog().info("START getListBlog");
        if (pageIndex == null) {
            pageIndex = 1;
        }
        Page<BlogVo> listBlogActive = blogService.getListBlogActive(searchData,pageIndex-1, pageSize);
        model.addAttribute("listBlogActive", listBlogActive.toList());
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("numOfPages", listBlogActive.getTotalPages());
        LogUtils.getLog().info("END getListBlog");
        return ResponseEntity.ok(model);
    }

    //lấy ra blog chi tiết
    @GetMapping("/getBlogDetail")
    public ResponseEntity<?> getBlogDetail(@RequestParam(value = "blogId") Integer blogId, Authentication authentication) {
        LogUtils.getLog().info("START getBlogDetail");
        BlogVo blogDetail = blogService.getBlogDetail(blogId);
        if(blogDetail == null){
            return ResponseEntity.ok(new MessageVo("Bài viết có thể chua đc duyet nên khong tim tháy.", "success"));
        }
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Account account = accountRepository.findAccountByUserName(userDetails.getUsername());
            //kiểm tra xem người đăng nhập đã like hay dislike bài đó chưa: 0 chưa, 1 rồi
            CheckLikeDislikeReportVo checkLikeOrDislikes = checkLikeDislikeBlogRepository.getCheckLikeDislikeBlog(blogId, account.getAccountId());
            if (checkLikeOrDislikes != null) {
                blogDetail.setCheckLike(checkLikeOrDislikes.getCheckLike());
                blogDetail.setCheckDislike(checkLikeOrDislikes.getCheckDislike());
            }
            if (blogDetail.getUserName().equals(account.getUserName())) {// nếu người tạo comment là người đăng nhập
                blogDetail.setCheckEdit(1);// được quyền edit, delete
            } else {
                blogDetail.setCheckEdit(0);// ko dc quyền edit, delete
            }
        }
        LogUtils.getLog().info("END getBlogDetail");
        return ResponseEntity.ok(blogDetail);
    }

    // Tuần 3: create/update blog
    @PostMapping("/saveBlog")
    @PreAuthorize("hasRole('ROLE_ADMIN')or hasRole('ROLE_MOD')or hasRole('ROLE_USER')")
    public ResponseEntity<?> saveBlog(@Valid @RequestBody SaveBlogRequest saveBlogRequest,
                                      Authentication authentication) {
        if(saveBlogRequest.getTitle() == null || saveBlogRequest.getTitle().trim() == ""){
            return ResponseEntity.ok(new MessageVo("xin hãy điền tiêu đề cho bài viết", "error"));
        }
        if(saveBlogRequest.getContent() == null || saveBlogRequest.getContent().trim() == ""){
            return ResponseEntity.ok(new MessageVo("xin hãy điền nội dung cho bài viết", "error"));
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Account account = accountRepository.findAccountByUserName(userDetails.getUsername());
        if (saveBlogRequest.getBlogId() == null) {
            return blogService.createBlog(saveBlogRequest.getTitle(), saveBlogRequest.getContent(), account);
        } else {
            return blogService.updateBlog(saveBlogRequest.getBlogId(), saveBlogRequest.getTitle(), saveBlogRequest.getContent(), account);
        }

    }

    //xóa blog
    @PostMapping("/deleteBlog")
    @PreAuthorize("hasRole('ROLE_ADMIN')or hasRole('ROLE_MOD')or hasRole('ROLE_USER')")
    public ResponseEntity<?> deleteBlog(@RequestParam(value = "blogId") Integer blogId,
                                        Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Account account = accountRepository.findAccountByUserName(userDetails.getUsername()); //lấy ra thông tin ng đăng nhập
        return blogService.deleteBlog(blogId, account);
    }

    //danh sách blog chờ phê duyệt
    @GetMapping("/admin/listBlogPending")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getListBlogPending(Model model,@RequestParam(required = false) String searchData,
                                                @RequestParam(required = false) Integer pageIndex) {
        LogUtils.getLog().info("START getListBlogPending");
        if (pageIndex == null) {
            pageIndex = 1;
        }
        Page<BlogVo> listBlogPending = blogService.getListBlogPending(searchData, pageIndex-1, pageSize);
        model.addAttribute("listBlogPending", listBlogPending.toList());
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("numOfPages", listBlogPending.getTotalPages());
        LogUtils.getLog().info("END getListBlogPending");
        return ResponseEntity.ok(model);
    }

    //phê duyệt blog
    @PostMapping("/admin/approveBlog")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> approveBlog(@RequestParam(value = "blogId") Integer blogId) {
        return blogService.approveBlog(blogId);
    }

    //Tuần 4: like blog
    @PostMapping("/likeBlog")
    @PreAuthorize("hasRole('ROLE_ADMIN')or hasRole('ROLE_MOD')or hasRole('ROLE_USER')")
    public ResponseEntity<?> likeBlog(@RequestParam(value = "blogId") Integer blogId,
                                             Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Account account = accountRepository.findAccountByUserName(userDetails.getUsername()); //lấy ra thông tin ng đăng nhập
        return blogService.likeBlog(blogId, account);

    }

    @PostMapping("/dislikeBlog")
    @PreAuthorize("hasRole('ROLE_ADMIN')or hasRole('ROLE_MOD')or hasRole('ROLE_USER')")
    public ResponseEntity<?> dislikeBlog(@RequestParam(value = "blogId") Integer blogId,
                                             Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Account account = accountRepository.findAccountByUserName(userDetails.getUsername()); //lấy ra thông tin ng đăng nhập
        return blogService.dislikeBlog(blogId, account);

    }

    //Tuần 4: Xem bình luân bài
    @GetMapping("/getBlogComment")
    public ResponseEntity<?> getBlogComments(Model model,@RequestParam(value = "blogId") Integer blogId,
                                             @RequestParam(required = false) Integer pageIndex,
                                             Authentication authentication) {
        LogUtils.getLog().info("START getBlogComments");
        if (pageIndex == null) {
            pageIndex = 1;
        }
        Page<BlogCommentAccountVo> blogCommentAccountVos = blogCommentService.findBlogCommentByBlogId(blogId, pageIndex-1,pageSize);
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Account account = accountRepository.findAccountByUserName(userDetails.getUsername());
            for (BlogCommentAccountVo blogCommentAccountVo : blogCommentAccountVos) {
                //kiểm tra xem người đăng nhập đã like hay dislike bài đó chưa: 0 chưa, 1 rồi
                CheckLikeDislikeReportVo checkLikeOrDislikes = checkLikeDislikeReportBCRepository.getCheckLikeDislikeReportBC(blogCommentAccountVo.getBlogCommentID(),account.getAccountId());
                if(checkLikeOrDislikes != null) {
                    blogCommentAccountVo.setCheckLike(checkLikeOrDislikes.getCheckLike());
                    blogCommentAccountVo.setCheckDislike(checkLikeOrDislikes.getCheckDislike());
                }
                if (blogCommentAccountVo.getAccountUserName().equals(account.getUserName())){// nếu người tạo comment là người đăng nhập
                    blogCommentAccountVo.setCheckEdit(1);// được quyền edit, delete
                }else{
                    blogCommentAccountVo.setCheckEdit(0);// ko dc quyền edit, delete
                }
            }
        }
        model.addAttribute("blogCommentAccountVos", blogCommentAccountVos.toList());
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("numOfPages", blogCommentAccountVos.getTotalPages());
        LogUtils.getLog().info("END getBlogComments");
        return ResponseEntity.ok(model);

    }

    //Tuần 4: Đăng bình luận blog
    @PostMapping("/saveBlogComment")
    @PreAuthorize("hasRole('ROLE_ADMIN')or hasRole('ROLE_MOD')or hasRole('ROLE_USER')")
    public ResponseEntity<?> saveBlogComment(@Valid @RequestBody SaveBlogCommentRequest saveBlogCommentRequest,
                                             Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Account account = accountRepository.findAccountByUserName(userDetails.getUsername());

        if(saveBlogCommentRequest.getBlogCommentId() == null){
            return blogCommentService.createComment(saveBlogCommentRequest.getBlogId(), saveBlogCommentRequest.getContent(),account);
        }else{
            return blogCommentService.updateComment(saveBlogCommentRequest.getBlogId(),saveBlogCommentRequest.getBlogCommentId(),
                    saveBlogCommentRequest.getContent(),account);
        }
    }

    //Tuần 4: Báo cáo comment blog
    @PostMapping("/reportBlogComment")
    @PreAuthorize("hasRole('ROLE_ADMIN')or hasRole('ROLE_MOD')or hasRole('ROLE_USER')")
    public ResponseEntity<?> reportComment( @RequestParam(value = "blogCommentId", required = false) Integer blogCommentId,
                                            Authentication authentication) {
        MessageVo message = new MessageVo();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Account account = accountRepository.findAccountByUserName(userDetails.getUsername()); //lấy ra thông tin ng đăng nhập
        return blogCommentService.reportComment(blogCommentId, account);

    }

    //Tuần 4: like comment blog
    @PostMapping("/likeBlogComment")
    @PreAuthorize("hasRole('ROLE_ADMIN')or hasRole('ROLE_MOD')or hasRole('ROLE_USER')")
    public ResponseEntity<?> likeBlogComment(Model model,@RequestParam(value = "blogCommentId") Integer blogCommentId,
                                             @RequestParam(required = false) Integer pageIndex,
                                      Authentication authentication) {
        if (pageIndex == null) {
            pageIndex = 1;
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Account account = accountRepository.findAccountByUserName(userDetails.getUsername()); //lấy ra thông tin ng đăng nhập
        Page<BlogCommentAccountVo> blogCommentAccountVos = blogCommentService.likeBlogComment(blogCommentId, account, pageIndex);
        model.addAttribute("blogCommentAccountVos", blogCommentAccountVos.toList());
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("numOfPages", blogCommentAccountVos.getTotalPages());
        return ResponseEntity.ok(model);

    }

    //Tuần 4: dislike comment blog
    @PostMapping("/dislikeBlogComment")
    @PreAuthorize("hasRole('ROLE_ADMIN')or hasRole('ROLE_MOD')or hasRole('ROLE_USER')")
    public ResponseEntity<?> dislikeBlogComment(Model model,@RequestParam(value = "blogCommentId") Integer blogCommentId,
                                                @RequestParam(required = false) Integer pageIndex,
                                         Authentication authentication) {
        if (pageIndex == null) {
            pageIndex = 1;
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Account account = accountRepository.findAccountByUserName(userDetails.getUsername()); //lấy ra thông tin ng đăng nhập
        Page<BlogCommentAccountVo> blogCommentAccountVos = blogCommentService.dislikeBlogComment(blogCommentId, account, pageIndex);
        model.addAttribute("blogCommentAccountVos", blogCommentAccountVos.toList());
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("numOfPages", blogCommentAccountVos.getTotalPages());
        return ResponseEntity.ok(model);

    }

    //Tuần 4: Xóa comment blog
    @PostMapping("/deleteBlogComment")
    @PreAuthorize("hasRole('ROLE_ADMIN')or hasRole('ROLE_MOD')or hasRole('ROLE_USER')")
    public ResponseEntity<?> deleteBlogComment(@RequestParam(value = "blogCommentId") Integer blogCommentId,
                                        Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Account account = accountRepository.findAccountByUserName(userDetails.getUsername()); //lấy ra thông tin ng đăng nhập
        return blogCommentService.deleteBlogComment(blogCommentId, account);
    }

    //Tuần 6: Tìm kiếm bạn bè.
    @GetMapping("/searchFriends")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> searchFriends(Authentication authentication, String nameSearch) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Account account = accountRepository.findAccountByUserName(userDetails.getUsername()); //lấy ra thông tin ng đăng nhập
        List<FriendsVo> listFriends = blogService.searchFriends(account, nameSearch);
        if (listFriends == null){
            return ResponseEntity.ok(new MessageVo("Không có ai phù hợp cho tìm kiếm " + nameSearch, "success"));
        }
        return ResponseEntity.ok(listFriends);
    }

    @GetMapping("/getTop5SearchFriends")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getTop5SearchFriends(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Account account = accountRepository.findAccountByUserName(userDetails.getUsername()); //lấy ra thông tin ng đăng nhập
        List<String> listSearch = blogService.getTop5SearchFriends(account);
        if (listSearch == null){
            return ResponseEntity.ok(new MessageVo("Bạn chưa tìm kiếm ai!!!", "success"));
        }
        return ResponseEntity.ok(listSearch);
    }

    //Tuần 8: API add friend, block
    @PostMapping("/addFriends")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> addFriends(@RequestParam(value = "accountId") Integer accountId, Integer status,
                                        Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Account account = accountRepository.findAccountByUserName(userDetails.getUsername()); //lấy ra thông tin ng đăng nhập
        return blogService.addFriends(accountId, account, status);
    }

    @PostMapping("/confirmFriends")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> confirmFriends(@RequestParam(value = "accountId") Integer accountId,
                                        Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Account account = accountRepository.findAccountByUserName(userDetails.getUsername()); //lấy ra thông tin ng đăng nhập
        return blogService.confirmFriends(accountId, account);
    }

    //Tuần 8: Xem danh sách bạn bè, hoặc những người bị chặn hoặc chặn ai
    @GetMapping("/getListFriends")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getListFriends(Authentication authentication, Integer status) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Account account = accountRepository.findAccountByUserName(userDetails.getUsername()); //lấy ra thông tin ng đăng nhập
        List<FriendsVo> listFriends = blogService.getListFriends(account.getAccountId(), status);
        if (listFriends == null && status == 3){
            return ResponseEntity.ok(new MessageVo("Bạn hòa đồng với mọi người. Bạn không bị ai chặn hết á.", "success"));
        }
        if (listFriends == null && status == 2){
            return ResponseEntity.ok(new MessageVo("Bạn là người tuyệt vời. Bạn chưa chặn ai cả :v", "success"));
        }
        if (listFriends == null && status == 1){
            return ResponseEntity.ok(new MessageVo("Bạn là người sống nội tâm. Bạn chưa có bạn bè nào :(", "success"));
        }
        return ResponseEntity.ok(listFriends);
    }


}
