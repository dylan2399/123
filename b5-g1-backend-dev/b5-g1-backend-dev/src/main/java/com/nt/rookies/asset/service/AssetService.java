package com.nt.rookies.asset.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.nt.rookies.asset.dto.AssetDTO;
import com.nt.rookies.asset.dto.asset.response.DetailAssetDTO;
import com.nt.rookies.asset.dto.asset.response.ViewAssetDTO;
import com.nt.rookies.asset.entity.AssetEntity;
import com.nt.rookies.asset.entity.AssignmentEntity;
import com.nt.rookies.asset.entity.CategoryEntity;
import com.nt.rookies.asset.exception.AssetException;
import com.nt.rookies.asset.exception.AssignmentException;
import com.nt.rookies.asset.mapper.AssetMapper;
import com.nt.rookies.asset.mapper.AssignmentMapper;
import com.nt.rookies.asset.repository.AssetRepository;
import com.nt.rookies.asset.repository.AssignmentRepository;
import com.nt.rookies.asset.repository.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private AssetMapper assetMapper;

    @Autowired
    private AssignmentMapper assignmentMapper;

    public List<AssetDTO> getAllDefault() {
        List<AssetEntity> list = assetRepository.findAll().stream()
                .filter(assetEntity -> !assetEntity.getIsDeleted() &&
                        !assetEntity.getState().equals(AssetEntity.EState.WAITING_FOR_RECYCLING) &&
                        !assetEntity.getState().equals(AssetEntity.EState.RECYCLED)).collect(Collectors.toList());
        if (list.isEmpty()) {
            throw new AssetException(AssetException.ASSET_LIST_EMPTY);
        }
        return assetMapper.toListDto(list);
    }

    public AssetDTO createAsset(AssetDTO asset) {

        AssetEntity entity = assetMapper.convertToEntity(asset);
        entity.setState(AssetEntity.EState.check(asset.getState())
                .orElseThrow(() -> new AssetException(AssetException.ASSET_STATE_WRONG)));
        CategoryEntity category = categoryRepository.findByCategoryName(asset.getCategoryName())
                .orElseThrow(() -> new AssetException(AssetException.ASSET_CATEGORY_NOT_FOUND));
        try {
            entity.setCategory(category);
            entity.setCreatedDate(LocalDateTime.now());
            entity.setIsDeleted(false);
            return assetMapper.convertToDto(assetRepository.save(entity));
        } catch (Exception ex) {
            throw new AssetException(AssetException.CREATE_ASSET_FAIL);
        }
    }

    public void deleteAsset(String assetCode) {
        AssetEntity entity = assetRepository.findById(assetCode)
                .orElseThrow(() -> new AssetException(AssetException.DELETE_NOT_FOUND));
        if (entity.getState().equals(AssetEntity.EState.AVAILABLE)
                || entity.getState().equals(AssetEntity.EState.NOT_AVAILABLE)) {
            if (!hasHistoricalAssign(entity.getAssetCode()) && !hasWaitingAssign(assetCode)) {
                entity.setIsDeleted(true);
                assetRepository.save(entity);
            } else if (hasHistoricalAssign(entity.getAssetCode())) {
                throw new AssetException(AssetException.ASSET_HAS_HISTORICAL_ASSIGNS);
            } else {
                throw new AssetException(AssetException.ASSET_HAS_WAITING_ASSIGNS);
            }
        } else if (entity.getState().equals(AssetEntity.EState.WAITING_FOR_RECYCLING)
                || entity.getState().equals(AssetEntity.EState.RECYCLED)) {
            throw new AssetException(AssetException.ASSET_STATE_CANNOT_DELETE);
        }
    }


    // get all available asset for assignment
    public List<AssetDTO> getAvailableAssetForAssignment() {
        List<AssetDTO> result = null;
        // find asset available
        List<AssetEntity> availableAsset = assetRepository.findByStateEquals(AssetEntity.EState.AVAILABLE);
        List<String> invalidAssetCodes = assignmentRepository
                .findByStateEqualsAndIsDeletedEquals(AssignmentEntity.AssignStateEnum.WAITING_FOR_ACCEPTANCE, false).stream()
                .map((assignment) -> assignment.getAsset().getAssetCode()).collect(Collectors.toList());

        List<AssetEntity> validAssets = availableAsset.stream()
                .filter(asset -> (!asset.getIsDeleted()) && (!invalidAssetCodes.contains(asset.getAssetCode()))).collect(Collectors.toList());

        result = assetMapper.toListDto(validAssets);

        return result;
    }


    public boolean hasHistoricalAssign(String assetCode) {
        return !assignmentRepository.findHistoricalByAssetCode(assetCode).isEmpty();
    }

    public List<ViewAssetDTO> retrieveAllAssetsByLocationAndDefaultState(String location) {
        try {
            List<ViewAssetDTO> listAssetsDTO;
            List<AssetEntity> assets;
            try {
                assets = assetRepository.getAllByLocationAndDefaultState(location);
            } catch (Exception e) {
                e.printStackTrace();
                throw new AssetException(AssetException.ERR_RETRIEVE_ASSET_FAIL);
            }
            listAssetsDTO = assetMapper.convertToListDTO(assets);
            return listAssetsDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AssetException(AssetException.ERR_RETRIEVE_ASSET_FAIL);
        }
    }

    public List<ViewAssetDTO> retrieveAllAssetsByLocationAndNonDefaultState(String location) {
        try {
            List<ViewAssetDTO> listAssetsDTO;
            List<AssetEntity> assets;
            try {
                assets = assetRepository.getAllByLocationAndNonDefaultState(location);
            } catch (Exception e) {
                e.printStackTrace();
                throw new AssetException(AssetException.ERR_RETRIEVE_ASSET_FAIL);
            }
            listAssetsDTO = assetMapper.convertToListDTO(assets);
            return listAssetsDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AssetException(AssetException.ERR_RETRIEVE_ASSET_FAIL);
        }
    }

    public DetailAssetDTO retrieveAssetByAssetCode(String location, String assetCode) {
        try {
            Optional<AssetEntity> asset;
            List<AssignmentEntity> listAssignmentsByAssetCode;
            asset = assetRepository.findByAssetCode(location, assetCode);
            if (!asset.isPresent()) {
                throw new AssetException(AssetException.ASSET_NOT_FOUND);
            }
            listAssignmentsByAssetCode = assignmentRepository.findHistoricalByAssetCode(assetCode);
            DetailAssetDTO detailAssetDTO = assetMapper.convertToDetailDTO(asset.get());
            detailAssetDTO.setAssignments(assignmentMapper.convertToListDTO(listAssignmentsByAssetCode));
            return detailAssetDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AssetException(AssetException.ASSET_NOT_FOUND);
        }
    }

    public List<AssetDTO> getAvailableAssetForUpdateAssignment(Integer assignmentId) {
        AssignmentEntity toUpdateAssignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AssignmentException(AssignmentException.ASSIGN_NOT_FOUND));
        List<AssetDTO> availableAssets = this.getAvailableAssetForAssignment();

        AssetEntity oldAsset = assetRepository.findById(toUpdateAssignment.getAsset().getAssetCode()).orElseThrow(() -> new AssetException(AssetException.ASSET_NOT_FOUND));
        availableAssets.add(assetMapper.convertToDto(oldAsset));

        return availableAssets;
    }

    public boolean hasWaitingAssign(String assetCode) {
        AssetEntity asset = assetRepository.findById(assetCode)
                .orElseThrow(() -> new AssetException(AssetException.DELETE_NOT_FOUND));
        return assignmentRepository.existsByAssetAndStateAndIsDeleted(asset,
                AssignmentEntity.AssignStateEnum.WAITING_FOR_ACCEPTANCE, false);
    }
}
