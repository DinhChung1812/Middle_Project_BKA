package doan.middle_project.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "Account")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_id")
	private Integer accountId;

	@Column(name = "username", columnDefinition = "nvarchar(max)")
	private String userName;

	@Column(name = "password", columnDefinition = "nvarchar(max)")
	private String password;

	@Column(name = "role", columnDefinition = "nvarchar(max)")
	private String role;

	@Column(name = "name", columnDefinition = "nvarchar(max)")
	private String name;

	@Column(name = "address", columnDefinition = "nvarchar(max)")
	private String address;

	@Column(name = "gender", columnDefinition = "nvarchar(max)")
	private String gender;

	@Column(name = "email", columnDefinition = "nvarchar(max)")
	private String email;

	@Column(name = "phone", columnDefinition = "nvarchar(max)")
	private String phone;

	@Column(name = "avatarImage", columnDefinition = "nvarchar(max)")
	private String avatarImage;

	@Column(name = "dob", columnDefinition = "DATE")
	private LocalDate dob;

	@Column(nullable=true, name = "high")
	private Double high;

	@Column(nullable=true, name = "weight")
	private Double weight;

	@Column(nullable=true, name = "mobility")
	private Double mobility;

	@Column(nullable=true, name = "totalCalo")
	private Double totalCalo;

	@Column(name = "target", columnDefinition = "nvarchar(max)")
	private String target;

	@Column(name = "targetIndex")
	private Double targetIndex;

	@Column(name = "create_date", columnDefinition = "DATE")
	private LocalDate createDate;

	@Column(name = "update_date", columnDefinition = "DATE")
	private LocalDate updateDate;

	@Column(name = "status")
	private Integer status;

	@Column(name = "reset_password_token", columnDefinition = "nvarchar(max)")
	private String resetPasswordToken;

	@OneToMany( mappedBy = "account",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<Blog> blogs;

	@OneToMany( mappedBy = "account",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<BlogComment> blogComments;

	@OneToMany( mappedBy = "account",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<Friends> friends;

	@OneToMany( mappedBy = "account",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<Search> search;

	@OneToMany( mappedBy = "account",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<CheckLikeDislikeBlog> checkLikeDislikeBlogs;

	@OneToMany( mappedBy = "account",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<CheckLikeDislikeReportBC> checkLikeDislikeReportBC;

	public Account(String userName, String password,String email, String name) {
		this.userName = userName;
		this.password = password;
		this.email = email;
		this.name = name;
	}
}
