package doan.middle_project.serviceImpl;

import doan.middle_project.common.vo.*;
import doan.middle_project.entities.*;
import doan.middle_project.exception.NotFoundException;
import doan.middle_project.exception.StatusCode;
import doan.middle_project.repositories.*;
import doan.middle_project.service.BlogService;
import javassist.bytecode.annotation.IntegerMemberValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private FriendsRepository friendsRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SearchRepository searchRepository;

    @Autowired
    private CheckLikeDislikeBlogRepository checkLikeDislikeBlogRepository;

    @Override
    public Page<BlogVo> getListBlogActive(String searchData, Integer pageIndex, Integer pageSize) {
        if (searchData == null) {
            searchData = "";
        }
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return blogRepository.getListBlogActive("%" + searchData.trim() + "%", pageable);
    }

    @Override
    public Page<BlogVo> getListBlogPending(String searchData,Integer pageIndex, Integer pageSize) {
        if (searchData == null) {
            searchData = "";
        }
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return blogRepository.getListBlogPending("%" + searchData.trim() + "%", pageable);
    }

    @Override
    public BlogVo getBlogDetail(Integer blogId) {
        return blogRepository.getBlogDetail(blogId);
    }

    @Override
    public ResponseEntity<?> createBlog(String title, String content, Account account) {
        Blog blog = new Blog();
        blog.setTitle(title);
        blog.setContent(content);
        blog.setAccount(account);
        if (account.getRole().equals("ROLE_ADMIN") || account.getRole().equals("ROLE_MOD")) {
            blog.setStatus(1);
        } else {
            blog.setStatus(0);
        }
        blog.setTotalLike(0);
        blog.setTotalDisLike(0);
        blog.setNumberComment(0);
        blog.setCreateDate(LocalDate.now());
        blog.setUpdateDate(LocalDate.now());
        blogRepository.save(blog);
        return ResponseEntity.ok(new MessageVo("lưu bài viết thành công", "success"));
    }

    @Override
    public ResponseEntity<?> updateBlog(Integer blogId, String title, String content, Account account) {
        Blog blog = blogRepository.findById(blogId).orElseThrow(() ->
                new NotFoundException(StatusCode.Not_Found, "blog này không tồn tại "));
        if (blog.getAccount().getAccountId() == account.getAccountId()) {
            blog.setTitle(title);
            blog.setAccount(account);
            blog.setContent(content);
            blog.setUpdateDate(LocalDate.now());
            blogRepository.save(blog);
            return ResponseEntity.ok(new MessageVo("cập nhật bài viết thành công", "success"));
        } else {
            return ResponseEntity.ok(new MessageVo("Bạn không có quyền sửa bài viết này", "error"));
        }
    }

    @Override
    public ResponseEntity<?> deleteBlog(Integer blogId, Account account) {
        Blog blog = blogRepository.findById(blogId).orElseThrow(() ->
                new NotFoundException(StatusCode.Not_Found, "blog" + blogId + " Not exist or blog was blocked "));
        if (blog.getAccount().getAccountId() == account.getAccountId()) {
            blog.setStatus(3);
            blogRepository.save(blog);
            return ResponseEntity.ok(new MessageVo("Xóa bài viết thành công", "success"));
        } else if (account.getRole().equals("ROLE_ADMIN")) {
            blog.setStatus(3);
            blogRepository.save(blog);
            return ResponseEntity.ok(new MessageVo("Xóa bài viết thành công", "success"));
        } else {
            return ResponseEntity.ok(new MessageVo("Bạn không có quyền xóa bài viết này", "error"));
        }
    }

    @Override
    public ResponseEntity<?> approveBlog(Integer blogId) {
        Blog blog = blogRepository.findById(blogId).orElseThrow(() ->
                new NotFoundException(StatusCode.Not_Found, "blog" + blogId + " Not exist or blog was blocked "));
        blog.setStatus(1);
        blogRepository.save(blog);
        return ResponseEntity.ok(new MessageVo("Đã phê duyệt bài viết", "success"));
    }

    @Override
    public ResponseEntity<?> likeBlog(Integer blogId, Account account) {
        Blog blog = blogRepository.findById(blogId).orElseThrow(() ->
                new NotFoundException(StatusCode.Not_Found, "blog" + blogId + " Not exist or blog was blocked "));
        CheckLikeDislikeReportVo checkLikeOrDislikes = checkLikeDislikeBlogRepository.getCheckLikeDislikeBlog(blogId, account.getAccountId());
        CheckLikeDislikeBlog checkLikeDislikeBlog = new CheckLikeDislikeBlog();
        checkLikeDislikeBlog.setBlog(blog);
        checkLikeDislikeBlog.setAccount(account);
        checkLikeDislikeBlog.setCheckId(new CheckLikeDislikeReportId(account.getAccountId(), blogId));
        if (checkLikeOrDislikes == null) { //chưa like , dislike  bao giờ
            blog.setTotalLike(blog.getTotalLike() + 1); //tăng total like lên 1
            blog.setTotalDisLike(blog.getTotalDisLike());// giữ nguyên total dislike
            checkLikeDislikeBlog.setCheckLike(1);//like
            checkLikeDislikeBlog.setCheckDislike(0);//ko dislike
        } else if (checkLikeOrDislikes.getCheckLike() == null || checkLikeOrDislikes.getCheckLike() != 1) { //người đăng nhập chưa like, đã dislike
            blog.setTotalLike(blog.getTotalLike() + 1); //tăng total like lên 1
            if (checkLikeOrDislikes.getCheckDislike() == 1 && blog.getTotalDisLike() > 0) { //nếu đang dislike
                blog.setTotalDisLike(blog.getTotalDisLike() - 1); //giảm total dislike đi 1
            }
            checkLikeDislikeBlog.setCheckLike(1); //like
            checkLikeDislikeBlog.setCheckDislike(0); //bỏ dislike
        } else { // người đăng nhập đã like => bấm nút để bỏ like
            if (blog.getTotalLike() > 0) {
                blog.setTotalLike(blog.getTotalLike() - 1); // giảm total like xuống 1
            }
            blog.setTotalDisLike(blog.getTotalDisLike());// giữ nguyên total dislike
            checkLikeDislikeBlog.setCheckLike(0); //bỏ like
            checkLikeDislikeBlog.setCheckDislike(checkLikeOrDislikes.getCheckDislike()); // giữ nguyên dislike
        }
        checkLikeDislikeBlogRepository.save(checkLikeDislikeBlog);
        blogRepository.save(blog);
        BlogVo blogDetail = getBlogDetail(blogId);
 //       CheckLikeDislikeReportVo newCheckLikeOrDislikes = checkLikeDislikeBlogRepository.getCheckLikeDislikeBlog(blogId, account.getAccountId());
//        if (newCheckLikeOrDislikes != null) {
            blogDetail.setCheckLike(checkLikeDislikeBlog.getCheckLike());
            blogDetail.setCheckDislike(checkLikeDislikeBlog.getCheckDislike());
//        }
        if (blogDetail.getUserName().equals(account.getUserName())) {// nếu người tạo comment là người đăng nhập
            blogDetail.setCheckEdit(1);// được quyền edit, delete
        } else {
            blogDetail.setCheckEdit(0);// ko dc quyền edit, delete
        }
        return ResponseEntity.ok(blogDetail);
    }

    @Override
    public ResponseEntity<?> dislikeBlog(Integer blogId, Account account) {
        Blog blog = blogRepository.findById(blogId).orElseThrow(() ->
                new NotFoundException(StatusCode.Not_Found, "blog" + blogId + " Not exist or blog was blocked "));
        CheckLikeDislikeReportVo checkLikeOrDislikes = checkLikeDislikeBlogRepository.getCheckLikeDislikeBlog(blogId, account.getAccountId());
        CheckLikeDislikeBlog checkLikeDislikeBlog = new CheckLikeDislikeBlog();
        checkLikeDislikeBlog.setBlog(blog);
        checkLikeDislikeBlog.setAccount(account);
        checkLikeDislikeBlog.setCheckId(new CheckLikeDislikeReportId(account.getAccountId(), blogId));
        if (checkLikeOrDislikes == null) { //chưa like , dislike hay report bao giờ
            blog.setTotalLike(blog.getTotalLike()); //giữ nguyên total like
            blog.setTotalDisLike(blog.getTotalDisLike() + 1);// tăng total dislike lên 1
            checkLikeDislikeBlog.setCheckLike(0);//ko like
            checkLikeDislikeBlog.setCheckDislike(1);//dislike

        } else if (checkLikeOrDislikes.getCheckDislike() == null || checkLikeOrDislikes.getCheckDislike() != 1) { //người đăng nhập chưa dislike, đã like hoặc report
            blog.setTotalDisLike(blog.getTotalDisLike() + 1); //tăng total dislike lên 1
            if (checkLikeOrDislikes.getCheckLike() == 1 && blog.getTotalLike() > 0) { //nếu đã từng like
                blog.setTotalLike(blog.getTotalLike() - 1); //giảm total like đi 1
            }
            checkLikeDislikeBlog.setCheckLike(0); //bỏ like
            checkLikeDislikeBlog.setCheckDislike(1); // dislike

        } else { // người đăng nhập đã dislike => bấm nút để bỏ dislike
            if (blog.getTotalDisLike() > 0) {
                blog.setTotalDisLike(blog.getTotalDisLike() - 1); // giảm total dislike xuống 1
            }
            blog.setTotalLike(blog.getTotalLike());// giữ nguyên total like
            checkLikeDislikeBlog.setCheckDislike(0); //bỏ dislike
            checkLikeDislikeBlog.setCheckLike(checkLikeOrDislikes.getCheckLike()); // giữ nguyên like
        }
        checkLikeDislikeBlogRepository.save(checkLikeDislikeBlog);
        blogRepository.save(blog);
        BlogVo blogDetail = getBlogDetail(blogId);
        //       CheckLikeDislikeReportVo newCheckLikeOrDislikes = checkLikeDislikeBlogRepository.getCheckLikeDislikeBlog(blogId, account.getAccountId());
//        if (newCheckLikeOrDislikes != null) {
        blogDetail.setCheckLike(checkLikeDislikeBlog.getCheckLike());
        blogDetail.setCheckDislike(checkLikeDislikeBlog.getCheckDislike());
//        }
        if (blogDetail.getUserName().equals(account.getUserName())) {// nếu người tạo comment là người đăng nhập
            blogDetail.setCheckEdit(1);// được quyền edit, delete
        } else {
            blogDetail.setCheckEdit(0);// ko dc quyền edit, delete
        }
        return ResponseEntity.ok(blogDetail);
    }

    @Override
    public ResponseEntity<?> addFriends(Integer accountId, Account account, Integer status) {
        if(status == null){
            Friends friends = new Friends();
            friends.setFriends2ID(accountId);
            friends.setAccount(account);
            friends.setStatus(0); // 0 là danh sách chờ
            friendsRepository.save(friends);
            return ResponseEntity.ok(new MessageVo("Gửi lời mời kết bạn thành công.", "success"));
        } else {
            Friends friend = friendsRepository.findAccount(account.getAccountId(), accountId);
            friend.setStatus(2); // chặn ng khác
            friendsRepository.save(friend);
            Friends friend2 = friendsRepository.findAccount(accountId, account.getAccountId());
            friend2.setStatus(3); // ng khác bị chặn
            friendsRepository.save(friend2);
            return ResponseEntity.ok(new MessageVo("Đã chặn " + accountId, "success"));
        }
    }

    @Override
    public ResponseEntity<?> confirmFriends(Integer accountId, Account account) {
        Friends friends = friendsRepository.findAccount(account.getAccountId(), accountId);
        friends.setStatus(1); // 1 là bạn bè
        friendsRepository.save(friends);
        Friends friends2 = new Friends();
        friends2.setFriends2ID(accountId);
        friends2.setAccount(account);
        friends2.setStatus(1); //
        friendsRepository.save(friends2);
        return ResponseEntity.ok(new MessageVo("Chúc mừng :v Hai bạn đã trở thành bạn bè.", "success"));
    }

    @Override
    public List<FriendsVo> getListFriends(Integer accountId, Integer status) {
        List<FriendsVo> listFriendsId = friendsRepository.getListFriendsId(accountId, status);
        List<FriendsVo> listFriends = new ArrayList<>();
        if(listFriendsId.size() == 0){
            return null;
        }
        for (int i = 0; i < listFriendsId.size(); i++) {
            FriendsVo friends = friendsRepository.getListFriends(listFriendsId.get(i).getUsernameID());
            if(friends == null){
                ResponseEntity.ok(new MessageVo("Không tìm thấy account có ID " + friends.getUsernameID(), "fail"));
            }
            listFriends.add(friends);
        }
        return listFriends;
    }

    @Override
    public List<FriendsVo> searchFriends(Account account, String nameSearch) {
        Search search = new Search();
        search.setContentSearch(nameSearch);
        search.setStatus(0);
        search.setAccount(account);
        searchRepository.save(search);
        List<FriendsVo> listFriends = accountRepository.searchByName(nameSearch);
        if(listFriends.size() == 0){
            return null;
        }
        return listFriends;
    }

    @Override
    public List<String> getTop5SearchFriends(Account account) {
        List<SearchVo> list = searchRepository.getTop5SearchFriends(account.getAccountId());
        if (list.size() == 0){
            return null;
        }
        List<String> listNew = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            listNew.add(list.get(i).getContentSearch());
        }
        return listNew;
    }
}



