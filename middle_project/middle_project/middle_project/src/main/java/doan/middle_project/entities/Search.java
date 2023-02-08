package doan.middle_project.entities;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "Search" )
@Data
public class Search {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "search_id")
    private Integer searchID;

    @Column(name = "contentSearch", columnDefinition = "nvarchar(max)")
    private String contentSearch;

    @Column(name = "status")
    private Integer status;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "account_id", nullable = false)
    private Account account;
}
