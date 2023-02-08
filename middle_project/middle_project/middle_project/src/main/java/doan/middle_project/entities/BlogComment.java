package doan.middle_project.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "BlogComment" )
@Data
public class BlogComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_comment_id")
    private Integer blogCommentID;

    @Column(nullable=true, name = "content", columnDefinition = "nvarchar(max)")
    private String content;

    @Column(nullable=true, name = "flag")
    private Integer flag;

    @Column(name = "total_like")
    private Integer totalLike;

    @Column(name = "total_dis_like")
    private Integer totalDisLike;

    @Column(name = "create_date", columnDefinition = "DATE")
    private LocalDate createDate;

    @Column(name = "update_date", columnDefinition = "DATE")
    private LocalDate updateDate;

    @Column(name = "status")
    private Integer status;

    @ManyToOne
    @JoinColumn(name = "blog_id", referencedColumnName = "blog_id", nullable = false)
    private Blog blogID;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "account_id", nullable = false)
    private Account account;

    @OneToMany( mappedBy = "blogComment",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<CheckLikeDislikeReportBC> checkLikeDislikeReportBC;
}
