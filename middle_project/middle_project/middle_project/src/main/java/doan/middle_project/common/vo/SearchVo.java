package doan.middle_project.common.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchVo {

    private String contentSearch;
    public SearchVo(String contentSearch) {
        this.contentSearch = contentSearch;
    }
}
