package doan.middle_project.repositories;

import doan.middle_project.common.vo.CheckLikeDislikeReportVo;
import doan.middle_project.entities.CheckLikeDislikeBlog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckLikeDislikeBlogRepository extends JpaRepository<CheckLikeDislikeBlog, Integer> {

    @Query("select new doan.middle_project.common.vo.CheckLikeDislikeReportVo(" +
            " c.checkLike, c.checkDislike)" +
            " from CheckLikeDislikeBlog c where c.account.accountId =:accountId and c.blog.blogID =:blogId")
    public CheckLikeDislikeReportVo getCheckLikeDislikeBlog(Integer blogId, Integer accountId);
}
