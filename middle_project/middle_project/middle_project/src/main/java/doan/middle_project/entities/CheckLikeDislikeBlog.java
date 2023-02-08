package doan.middle_project.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "CheckLikeDislikeBlog" )
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class CheckLikeDislikeBlog {
    @EmbeddedId
    private CheckLikeDislikeReportId checkId = new CheckLikeDislikeReportId();

    @ManyToOne
    private Account account;

    @ManyToOne
    private Blog blog;

    @Column(name = "check_like")
    private Integer checkLike;

    @Column(name = "check_dislike")
    private Integer checkDislike;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CheckLikeDislikeBlog)) return false;
        CheckLikeDislikeBlog that = (CheckLikeDislikeBlog) o;
        return Objects.equals(account, that.account) && Objects.equals(blog, that.blog);
    }
}
