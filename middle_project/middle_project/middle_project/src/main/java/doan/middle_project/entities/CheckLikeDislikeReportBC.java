package doan.middle_project.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "CheckLikeDislikeReportBlogComment" )
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class CheckLikeDislikeReportBC {
    @EmbeddedId
    private CheckLikeDislikeReportId checkId = new CheckLikeDislikeReportId();

    @ManyToOne
    private Account account;

    @ManyToOne
    private BlogComment blogComment;

    @Column(name = "check_like")
    private Integer checkLike;

    @Column(name = "check_dislike")
    private Integer checkDislike;

    @Column(name = "check_report")
    private Integer checkReport;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CheckLikeDislikeReportBC)) return false;
        CheckLikeDislikeReportBC that = (CheckLikeDislikeReportBC) o;
        return Objects.equals(account, that.account) && Objects.equals(blogComment, that.blogComment);
    }


}
