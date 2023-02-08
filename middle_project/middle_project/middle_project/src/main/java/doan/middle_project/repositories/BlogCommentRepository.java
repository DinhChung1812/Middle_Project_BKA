package doan.middle_project.repositories;

import doan.middle_project.common.vo.BlogCommentAccountVo;
import doan.middle_project.entities.BlogComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogCommentRepository extends JpaRepository<BlogComment, Integer> {

    @Query("select new doan.middle_project.common.vo.BlogCommentAccountVo" +
            "(dc.blogCommentID,dc.content,dc.flag,dc.totalLike," +
            "dc.totalDisLike,cast(dc.createDate as string ),cast(dc.updateDate as string ),a.userName, a.avatarImage)" +
            " from BlogComment dc join dc.account a where dc.status <> 3 and dc.blogID.blogID = :blogId and a.status <> 3 order by dc.blogCommentID desc")
    public Page<BlogCommentAccountVo> findBlogCommentByBlogId(Integer blogId, Pageable pageable);

    @Query("select new doan.middle_project.common.vo.BlogCommentAccountVo" +
            "(dc.blogCommentID,dc.content,dc.flag,dc.totalLike," +
            "dc.totalDisLike,cast(dc.createDate as string ),cast(dc.updateDate as string ),a.userName, a.avatarImage)" +
            " from BlogComment dc join dc.account a where dc.status = 2  and a.status <> 3" +
            "and (cast(dc.blogCommentID as string ) like :searchData or dc.content like :searchData " +
            " or a.userName like :searchData ) order by dc.blogCommentID asc")
    public Page<BlogCommentAccountVo> findReportBlogComment(String searchData, Pageable pageable);
}
