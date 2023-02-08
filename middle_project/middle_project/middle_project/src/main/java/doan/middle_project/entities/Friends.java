package doan.middle_project.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "Friends" )
@Data
public class Friends {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friends_id")
    private Integer friendsID;

    @Column(name = "friends2_id")
    private Integer friends2ID;

    @Column(name = "status")
    private Integer status;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "account_id", nullable = false)
    private Account account;

}
