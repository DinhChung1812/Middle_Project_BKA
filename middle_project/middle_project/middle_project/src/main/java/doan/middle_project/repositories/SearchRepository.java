package doan.middle_project.repositories;

import doan.middle_project.common.vo.FriendsVo;
import doan.middle_project.common.vo.SearchVo;
import doan.middle_project.entities.Search;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<Search,Integer> {
    @Query("select new doan.middle_project.common.vo.SearchVo(s.contentSearch ) " +
            "from Search s join s.account a " +
            "where s.account.accountId = :accountId")
    List<SearchVo> getTop5SearchFriends(Integer accountId);
}
