package doan.middle_project.repositories;

import doan.middle_project.common.vo.FriendsVo;
import doan.middle_project.entities.Account;
import doan.middle_project.entities.Friends;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, Integer> {

    @Query("select f from Friends f where f.friends2ID = :accountID and f.account.accountId = :accountID2")
    Friends findAccount(Integer accountID, Integer accountID2);

    @Query("select new doan.middle_project.common.vo.FriendsVo(f.friends2ID) " +
            "from Friends f join f.account a " +
            "where a.accountId = :accountId and f.status = :status")
    List<FriendsVo> getListFriendsId(Integer accountId, Integer status);

    @Query("select new doan.middle_project.common.vo.FriendsVo(a.accountId, a.name, a.avatarImage) " +
            "from Account a " +
            "where a.accountId = :accountId")
    FriendsVo getListFriends(Integer accountId);


}
