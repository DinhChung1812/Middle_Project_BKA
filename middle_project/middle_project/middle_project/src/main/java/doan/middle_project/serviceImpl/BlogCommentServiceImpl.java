package doan.middle_project.serviceImpl;

import doan.middle_project.exception.StatusCode;
import doan.middle_project.exception.NotFoundException;
import doan.middle_project.common.vo.BlogCommentAccountVo;
import doan.middle_project.common.vo.CheckLikeDislikeReportVo;
import doan.middle_project.common.vo.MessageVo;
import doan.middle_project.entities.*;
import doan.middle_project.repositories.BlogCommentRepository;
import doan.middle_project.repositories.BlogRepository;
import doan.middle_project.repositories.CheckLikeDislikeReportBCRepository;
import doan.middle_project.service.BlogCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class BlogCommentServiceImpl implements BlogCommentService {

    @Autowired
    private BlogCommentRepository blogCommentRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private CheckLikeDislikeReportBCRepository checkLikeDislikeReportBCRepository;


    @Override
    public Page<BlogCommentAccountVo> findBlogCommentByBlogId(Integer blogId, Integer pageIndex, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return blogCommentRepository.findBlogCommentByBlogId(blogId,pageable);
    }

    @Override
    public ResponseEntity<?> createComment(Integer blogId, String content, Account account) {
        Blog blog = blogRepository.findById(blogId).orElseThrow(() -> new NotFoundException(StatusCode.Not_Found,"blog " + blogId + " Not exist or blog was blocked "));
        BlogComment blogComment = new BlogComment();
        blogComment.setAccount(account);
        blogComment.setFlag(0);
        blogComment.setContent(content);
        blogComment.setBlogID(blog);
        blogComment.setCreateDate(LocalDate.now());
        blogComment.setUpdateDate(LocalDate.now());
        blogComment.setTotalDisLike(0);
        blogComment.setTotalLike(0);
        blogComment.setStatus(1); // status = 1 active
        blog.setNumberComment(blog.getNumberComment()+1);
        blogCommentRepository.save(blogComment);
        blogRepository.save(blog);
        return ResponseEntity.ok( new MessageVo("lưu bình luận thành công", "success"));
    }

    @Override
    public ResponseEntity<?> updateComment(Integer blogId, Integer blogCommentId, String content, Account account) {
        BlogComment blogComment = blogCommentRepository.findById(blogCommentId).orElseThrow(() ->
                new NotFoundException(StatusCode.Not_Found,"blog comment" + blogCommentId + " Not exist or blog comment was blocked "));
        if(blogComment.getAccount().getAccountId() == account.getAccountId()){
            blogComment.setAccount(account);
            blogComment.setContent(content);
            blogComment.setUpdateDate(LocalDate.now());
            blogComment.setStatus(1);
            blogCommentRepository.save(blogComment);
            return ResponseEntity.ok( new MessageVo("cập nhật bình luận thành công", "success"));
        } else{
            return ResponseEntity.ok( new MessageVo("Bạn không có quyền sửa bình luận này", "error"));
        }
    }

    @Transactional
    @Override
    public ResponseEntity<?> reportComment(Integer blogCommentId, Account account) {
        CheckLikeDislikeReportVo checkReport = checkLikeDislikeReportBCRepository.getCheckLikeDislikeReportBC(blogCommentId,account.getAccountId());
        if(checkReport == null || checkReport.getCheckReport() == null || checkReport.getCheckReport() != 1){ //nếu ng đăng nhập chưa report bình luận
            BlogComment blogComment = blogCommentRepository.findById(blogCommentId).orElseThrow(() ->
                    new NotFoundException(StatusCode.Not_Found,"blog comment" + blogCommentId + " Not exist or blog comment was blocked "));
            blogComment.setFlag(blogComment.getFlag()+1);
            if(blogComment.getFlag()>=3){ // nếu có 3 ng đổ lên report
                blogComment.setStatus(2); //chuyển trạng thái sang danh sách báo cáo cho admin
            }
            CheckLikeDislikeReportBC checkLikeDislikeReportBC = new CheckLikeDislikeReportBC();
            if(checkReport == null){
                checkLikeDislikeReportBC.setCheckReport(1);
                checkLikeDislikeReportBC.setCheckDislike(0);
                checkLikeDislikeReportBC.setCheckLike(0);
                checkLikeDislikeReportBC.setBlogComment(blogComment);
                checkLikeDislikeReportBC.setAccount(account);
                checkLikeDislikeReportBCRepository.save(checkLikeDislikeReportBC);
            }else {
                checkLikeDislikeReportBCRepository.updateCheckReportBC(account.getAccountId(), blogCommentId);
            }
            blogCommentRepository.save(blogComment);
            return ResponseEntity.ok(new MessageVo("Đã báo cáo bình luận cho quản trị viên","success"));
        }else {
            return ResponseEntity.ok(new MessageVo("Bạn đã báo cáo bình luận này rồi","info"));
        }
    }

    @Override
    public Page<BlogCommentAccountVo> likeBlogComment(Integer blogCommentId, Account account,Integer pageIndex) {
        BlogComment blogComment = blogCommentRepository.findById(blogCommentId).orElseThrow(() ->
                new NotFoundException(StatusCode.Not_Found,"blog comment" + blogCommentId + " Not exist or blog comment was blocked "));
        CheckLikeDislikeReportVo checkLikeOrDislikes = checkLikeDislikeReportBCRepository.getCheckLikeDislikeReportBC(blogCommentId,account.getAccountId());
        CheckLikeDislikeReportBC checkLikeDislikeReportBC = new CheckLikeDislikeReportBC();
        checkLikeDislikeReportBC.setBlogComment(blogComment);
        checkLikeDislikeReportBC.setAccount(account);
        checkLikeDislikeReportBC.setCheckId(new CheckLikeDislikeReportId(account.getAccountId(), blogCommentId));
        if (checkLikeOrDislikes == null) { //chưa like , dislike  bao giờ
            blogComment.setTotalLike(blogComment.getTotalLike() + 1); //tăng total like lên 1
            blogComment.setTotalDisLike(blogComment.getTotalDisLike());// giữ nguyên total dislike
            checkLikeDislikeReportBC.setCheckLike(1);//like
            checkLikeDislikeReportBC.setCheckDislike(0);//ko dislike
            checkLikeDislikeReportBC.setCheckReport(0);
        } else if (checkLikeOrDislikes.getCheckLike() == null || checkLikeOrDislikes.getCheckLike() != 1) { //người đăng nhập chưa like, đã dislike
            blogComment.setTotalLike(blogComment.getTotalLike() + 1); //tăng total like lên 1
            if (checkLikeOrDislikes.getCheckDislike() == 1 && blogComment.getTotalDisLike() > 0) { //nếu đang dislike
                blogComment.setTotalDisLike(blogComment.getTotalDisLike() - 1); //giảm total dislike đi 1
            }
            checkLikeDislikeReportBC.setCheckLike(1); //like
            checkLikeDislikeReportBC.setCheckDislike(0); //bỏ dislike
            checkLikeDislikeReportBC.setCheckReport(checkLikeOrDislikes.getCheckReport());// giữ nguyên report
        } else { // người đăng nhập đã like => bấm nút để bỏ like
            if (blogComment.getTotalLike() > 0) {
                blogComment.setTotalLike(blogComment.getTotalLike() - 1); // giảm total like xuống 1
            }
            blogComment.setTotalDisLike(blogComment.getTotalDisLike());// giữ nguyên total dislike
            checkLikeDislikeReportBC.setCheckLike(0); //bỏ like
            checkLikeDislikeReportBC.setCheckDislike(checkLikeOrDislikes.getCheckDislike()); // giữ nguyên dislike
            checkLikeDislikeReportBC.setCheckReport(checkLikeOrDislikes.getCheckReport());// giữ nguyên report
        }
        checkLikeDislikeReportBCRepository.save(checkLikeDislikeReportBC);
        blogCommentRepository.save(blogComment);
        Page<BlogCommentAccountVo> blogCommentAccountVos = findBlogCommentByBlogId(blogRepository.getBlogIdByBlogCommentId(blogCommentId), pageIndex-1,5);
        for (BlogCommentAccountVo blogCommentAccountVo : blogCommentAccountVos) {
                blogCommentAccountVo.setCheckLike(checkLikeDislikeReportBC.getCheckLike());
                blogCommentAccountVo.setCheckDislike(checkLikeDislikeReportBC.getCheckDislike());

            if (blogCommentAccountVo.getAccountUserName().equals(account.getUserName())) {// nếu người tạo comment là người đăng nhập
                blogCommentAccountVo.setCheckEdit(1);// được quyền edit, delete
            } else {
                blogCommentAccountVo.setCheckEdit(0);// ko dc quyền edit, delete
            }
        }
        return blogCommentAccountVos;
    }

    @Override
    public Page<BlogCommentAccountVo> dislikeBlogComment(Integer blogCommentId, Account account,Integer pageIndex) {
        BlogComment blogComment = blogCommentRepository.findById(blogCommentId).orElseThrow(() ->
                new NotFoundException(StatusCode.Not_Found,"blog comment" + blogCommentId + " Not exist or blog comment was blocked "));
        CheckLikeDislikeReportVo checkLikeOrDislikes = checkLikeDislikeReportBCRepository.getCheckLikeDislikeReportBC(blogCommentId,account.getAccountId());
        CheckLikeDislikeReportBC checkLikeDislikeReportBC = new CheckLikeDislikeReportBC();
        checkLikeDislikeReportBC.setBlogComment(blogComment);
        checkLikeDislikeReportBC.setAccount(account);
        checkLikeDislikeReportBC.setCheckId(new CheckLikeDislikeReportId(account.getAccountId(), blogCommentId));
        if (checkLikeOrDislikes == null) { //chưa like , dislike hay report bao giờ
            blogComment.setTotalLike(blogComment.getTotalLike()); //giữ nguyên total like
            blogComment.setTotalDisLike(blogComment.getTotalDisLike() + 1);// tăng total dislike lên 1
            checkLikeDislikeReportBC.setCheckLike(0);//ko like
            checkLikeDislikeReportBC.setCheckDislike(1);//dislike
            checkLikeDislikeReportBC.setCheckReport(0);
        } else if (checkLikeOrDislikes.getCheckDislike() == null || checkLikeOrDislikes.getCheckDislike() != 1) { //người đăng nhập chưa dislike, đã like hoặc report
            blogComment.setTotalDisLike(blogComment.getTotalDisLike() + 1); //tăng total dislike lên 1
            if (checkLikeOrDislikes.getCheckLike() == 1 && blogComment.getTotalLike() > 0) { //nếu đã từng like
                blogComment.setTotalLike(blogComment.getTotalLike() - 1); //giảm total like đi 1
            }
            checkLikeDislikeReportBC.setCheckLike(0); //bỏ like
            checkLikeDislikeReportBC.setCheckDislike(1); // dislike
            checkLikeDislikeReportBC.setCheckReport(checkLikeOrDislikes.getCheckReport());// giữ nguyên report
        } else { // người đăng nhập đã dislike => bấm nút để bỏ dislike
            if (blogComment.getTotalDisLike() > 0) {
                blogComment.setTotalDisLike(blogComment.getTotalDisLike() - 1); // giảm total dislike xuống 1
            }
            blogComment.setTotalLike(blogComment.getTotalLike());// giữ nguyên total like
            checkLikeDislikeReportBC.setCheckDislike(0); //bỏ dislike
            checkLikeDislikeReportBC.setCheckLike(checkLikeOrDislikes.getCheckLike()); // giữ nguyên like
            checkLikeDislikeReportBC.setCheckReport(checkLikeOrDislikes.getCheckReport());// giữ nguyên report
        }
        checkLikeDislikeReportBCRepository.save(checkLikeDislikeReportBC);
        blogCommentRepository.save(blogComment);
        Page<BlogCommentAccountVo> blogCommentAccountVos = findBlogCommentByBlogId(blogRepository.getBlogIdByBlogCommentId(blogCommentId), pageIndex-1,5);
        for (BlogCommentAccountVo blogCommentAccountVo : blogCommentAccountVos) {
            blogCommentAccountVo.setCheckLike(checkLikeDislikeReportBC.getCheckLike());
            blogCommentAccountVo.setCheckDislike(checkLikeDislikeReportBC.getCheckDislike());

            if (blogCommentAccountVo.getAccountUserName().equals(account.getUserName())) {// nếu người tạo comment là người đăng nhập
                blogCommentAccountVo.setCheckEdit(1);// được quyền edit, delete
            } else {
                blogCommentAccountVo.setCheckEdit(0);// ko dc quyền edit, delete
            }
        }
        return blogCommentAccountVos;
    }

    @Override
    public ResponseEntity<?> deleteBlogComment(Integer blogCommentId, Account account) {
        BlogComment blogComment = blogCommentRepository.findById(blogCommentId).orElseThrow(() ->
                new NotFoundException(StatusCode.Not_Found,"blog comment" + blogCommentId + " Not exist or blog comment was blocked "));
        Blog blog = blogComment.getBlogID();
        if (blogComment.getAccount().getAccountId() == account.getAccountId()) {
            if(blog.getNumberComment()>0){
                blog.setNumberComment(blog.getNumberComment()-1);
            }
            blogComment.setStatus(3);
            blogCommentRepository.save(blogComment);
            blogRepository.save(blog);
            return ResponseEntity.ok(new MessageVo("Xóa bình luận thành công", "success"));
        } else if (account.getRole().equals("ROLE_ADMIN")) {
            if(blog.getNumberComment()>0){
                blog.setNumberComment(blog.getNumberComment()-1);
            }
            blogComment.setStatus(3);
            blogCommentRepository.save(blogComment);
            blogRepository.save(blog);
            return ResponseEntity.ok(new MessageVo("Xóa bình luận thành công", "success"));
        } else {
            return ResponseEntity.ok(new MessageVo("Bạn không có quyền xóa bình luận này", "error"));
        }
    }

    @Override
    public Page<BlogCommentAccountVo> findReportBlogComment(String searchData, Integer pageIndex, Integer pageSize) {
        if (searchData == null) {
            searchData = "";
        }
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return blogCommentRepository.findReportBlogComment("%" + searchData.trim() + "%", pageable);
    }

    @Override
    public ResponseEntity<?> approveComment(Integer blogCommentId) {
        BlogComment blogComment = blogCommentRepository.findById(blogCommentId).orElseThrow(() ->
                new NotFoundException(StatusCode.Not_Found,"blog comment" + blogCommentId + " Not exist or blog comment was blocked "));
            blogComment.setFlag(0);
            blogComment.setStatus(1);
            blogCommentRepository.save(blogComment);
            return ResponseEntity.ok(new MessageVo("Đã phê duyệt bình luận", "success"));
    }




}
