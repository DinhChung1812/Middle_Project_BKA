package doan.middle_project.repositories;


import doan.middle_project.common.vo.AccountManageVo;
import doan.middle_project.common.vo.FriendsVo;
import doan.middle_project.entities.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    @Query("select a from Account a where a.status = 1 and a.userName =?1")
    Optional<Account> findByUserName(String userName);

    @Query("select new doan.middle_project.common.vo.FriendsVo(a.accountId,a.name, a.avatarImage) from Account a where a.status = 1 and a.name = :searchName")
    List<FriendsVo> searchByName(String searchName);

    @Query("select a from Account a where a.status = 1 and a.userName =?1")
    Account findAccountByUserName(String userName);

    Boolean existsByUserName(String username);

    Boolean existsByEmail(String email);

    @Query("select a from Account a where a.status = 1 and a.email =?1")
    public Account findAccountByEmail(String email);

    @Query("select new doan.middle_project.common.vo.AccountManageVo(" +
            "a.accountId,a.userName,a.role,a.email,a.avatarImage, cast(a.createDate as string)" +
            ") from Account a where a.status = 1 and (cast(a.accountId as string ) LIKE :searchData " +
            "or a.userName LIKE :searchData )")
    public Page<AccountManageVo> findAll(String searchData, Pageable pageable);

    public Account findByResetPasswordToken(String token);

}
