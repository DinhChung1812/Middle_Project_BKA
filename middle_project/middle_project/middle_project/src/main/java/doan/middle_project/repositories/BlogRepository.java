package doan.middle_project.repositories;

import doan.middle_project.common.vo.BlogVo;
import doan.middle_project.entities.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {


    @Query("select new doan.middle_project.common.vo.BlogVo(" +
            "b.blogID, b.title, b.content,b.totalLike,b.totalDisLike," +
            "b.numberComment, a.accountId, a.userName, a.avatarImage , b.status, b.createDate, b.updateDate)" +
            " from Blog b join b.account a where b.status = 1 and a.status <> 3" +
            " and ( b.title like :searchData or a.userName like :searchData ) order by b.blogID desc " )
    public Page<BlogVo> getListBlogActive (String searchData, Pageable pageable);

    @Query("select new doan.middle_project.common.vo.BlogVo(" +
            "b.blogID, b.title, b.content,b.totalLike,b.totalDisLike," +
            "b.numberComment, a.accountId, a.userName, a.avatarImage, b.status,b.createDate, b.updateDate )" +
            " from Blog b join b.account a where b.status = 0 and a.status <> 3" +
            " and ( b.title like :searchData or a.userName like :searchData ) order by b.blogID asc " )
    public Page<BlogVo> getListBlogPending (String searchData, Pageable pageable);

    @Query("select new doan.middle_project.common.vo.BlogVo(" +
            "b.blogID, b.title, b.content,b.totalLike,b.totalDisLike," +
            "b.numberComment, a.accountId, a.userName, a.avatarImage, b.status, b.createDate, b.updateDate )" +
            " from Blog b join b.account a where b.status = 1 and a.status <> 3" +
            " and b.blogID = :blogId" )
    public BlogVo getBlogDetail (Integer blogId);
    @Query("select b.blogID from BlogComment bc join  bc.blogID b where bc.blogCommentID = :blogCommentId and bc.status <> 3 and b.status <> 3")
    Integer getBlogIdByBlogCommentId(Integer blogCommentId);




}
