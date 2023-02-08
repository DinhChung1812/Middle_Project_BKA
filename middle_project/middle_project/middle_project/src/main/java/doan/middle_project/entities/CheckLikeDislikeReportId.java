package doan.middle_project.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class CheckLikeDislikeReportId implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer accountId;
    private Integer dishCommentId;

    public CheckLikeDislikeReportId() {

    }
}
