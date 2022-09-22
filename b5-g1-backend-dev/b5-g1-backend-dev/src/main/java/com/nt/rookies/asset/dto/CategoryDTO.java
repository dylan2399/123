package com.nt.rookies.asset.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
public class CategoryDTO {

    private Integer categoryId;
    private String categoryName;
    private String prefix;


}
