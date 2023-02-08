package doan.middle_project.service;

import doan.middle_project.common.vo.BlogVo;
import doan.middle_project.common.vo.FriendsVo;
import doan.middle_project.common.vo.SearchVo;
import doan.middle_project.entities.Account;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BlogService {

    public Page<BlogVo> getListBlogActive (String searchData, Integer pageIndex, Integer pageSize);

    public Page<BlogVo> getListBlogPending(String searchData,Integer pageIndex, Integer pageSize);

    public BlogVo getBlogDetail (Integer blogId);

    ResponseEntity<?> createBlog(String title, String content, Account account);

    ResponseEntity<?> updateBlog(Integer blogId,String title,String content, Account account);

    ResponseEntity<?> deleteBlog(Integer blogId,  Account account);

    ResponseEntity<?> approveBlog(Integer blogId);

    ResponseEntity<?> likeBlog(Integer blogId, Account account);

    ResponseEntity<?> dislikeBlog(Integer blogId, Account account);

    ResponseEntity<?> addFriends(Integer accountId,  Account account, Integer status);

    ResponseEntity<?> confirmFriends(Integer accountId,  Account account);

    List<FriendsVo> getListFriends(Integer accountId, Integer status);

    List<FriendsVo> searchFriends(Account account, String nameSearch);

    List<String> getTop5SearchFriends(Account account);
}
