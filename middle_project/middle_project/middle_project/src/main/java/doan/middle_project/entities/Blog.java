package doan.middle_project.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "Blog" )
@Data
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_id")
    private Integer blogID;

    @Column(name = "title", columnDefinition = "nvarchar(max)")
    private String title;

    @Column(nullable=true, name = "content", columnDefinition = "nvarchar(max)")
    private String content;

    @Column(name = "total_like")
    private Integer totalLike;

    @Column(name = "total_dis_like")
    private Integer totalDisLike;

    @Column(name = "number_comment")
    private Integer numberComment;

    @Column(name = "status")
    private Integer status;

    @Column(name = "create_date", columnDefinition = "DATE")
    private LocalDate createDate;

    @Column(name = "update_date", columnDefinition = "DATE")
    private LocalDate updateDate;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "account_id", nullable = false)
    private Account account;

    @OneToMany( mappedBy = "blogID",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<BlogComment> listBlogComment;

    @OneToMany( mappedBy = "blog",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<CheckLikeDislikeBlog> checkLikeDislikeBlogs;
}
