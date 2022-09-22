package com.nt.rookies.asset.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AssetException extends RuntimeException{

  private CodeResponse codeResponse;

  public AssetException(CodeResponse codeResponse) {
    this.codeResponse = codeResponse;
  }

  public static final CodeResponse SUCCESS = new CodeResponse("ASSET-01", "Retrieve assets successfully",
      HttpStatus.OK);

  public static final CodeResponse ASSET_NOT_FOUND = new CodeResponse("ASSET-02", "Asset not found",
      HttpStatus.NOT_FOUND);

  public static final CodeResponse ERR_RETRIEVE_NUMBER_OF_ASSETS_FAIL = new CodeResponse("ASSET-03",
      "Failed to retrieve number of assets", HttpStatus.INTERNAL_SERVER_ERROR);

  public static final CodeResponse ERR_RETRIEVE_ASSET_FAIL = new CodeResponse("ASSET-04", "Failed to retrieve asset",
      HttpStatus.INTERNAL_SERVER_ERROR);

  public static final CodeResponse ERR_CONVERT_DTO_ENTITY_FAIL = new CodeResponse("ASSET-05", "Failed to convert asset",
      HttpStatus.INTERNAL_SERVER_ERROR);

  public static final CodeResponse ASSET_STATE_WRONG = new CodeResponse("ASSET-08", "Asset state is wrong",
      HttpStatus.BAD_REQUEST);

  public static final CodeResponse CREATE_ASSET_FAIL = new CodeResponse("ASSET-06", "Create Asset fail",
      HttpStatus.BAD_REQUEST);

  public static final CodeResponse ASSET_CATEGORY_NOT_FOUND = new CodeResponse("ASSET-07",
      "Cannot found any category for this asset", HttpStatus.BAD_REQUEST);

  public static final CodeResponse ASSET_LIST_EMPTY = new CodeResponse("ASSET-09", "Not found any asset",
      HttpStatus.NOT_FOUND);

  public static final CodeResponse DELETE_NOT_FOUND = new CodeResponse("ASSET-10", "Not found asset to delete",
      HttpStatus.NOT_FOUND);

  public static final CodeResponse ASSET_HAS_HISTORICAL_ASSIGNS = new CodeResponse("ASSET-11",
      "Asset has historical assignments. Cannot delete", HttpStatus.BAD_REQUEST);

  public static final CodeResponse ASSET_STATE_CANNOT_DELETE = new CodeResponse("ASSET-12",
      "Asset is waiting for recycling or is recycled. Cannot delete", HttpStatus.BAD_REQUEST);

  public static final CodeResponse ASSET_HAS_WAITING_ASSIGNS = new CodeResponse("ASSET-13",
          "Asset has waiting for acceptance assignments. Cannot delete", HttpStatus.BAD_REQUEST);

}
