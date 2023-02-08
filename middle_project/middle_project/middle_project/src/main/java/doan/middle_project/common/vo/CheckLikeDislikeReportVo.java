package doan.middle_project.common.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckLikeDislikeReportVo {


    private Integer accountId;

    private Integer dishCommentId;

    private Integer checkLike;

    private Integer checkDislike;

    private Integer checkReport;

    public CheckLikeDislikeReportVo( Integer checkLike, Integer checkDislike,Integer checkReport) {
        this.checkLike = checkLike;
        this.checkDislike = checkDislike;
        this.checkReport =checkReport;
    }
    public CheckLikeDislikeReportVo(Integer checkLike, Integer checkDislike) {
        this.checkLike = checkLike;
        this.checkDislike = checkDislike;
    }

}
