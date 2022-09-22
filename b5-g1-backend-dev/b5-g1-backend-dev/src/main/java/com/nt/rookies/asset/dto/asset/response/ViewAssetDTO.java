package com.nt.rookies.asset.dto.asset.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ViewAssetDTO {
    private String assetCode;

    private String assetName;

    private String state;

    private String category;
}
