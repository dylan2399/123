package com.nt.rookies.asset.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.nt.rookies.asset.dto.assignment.ViewAssignmentDTO;
import com.nt.rookies.asset.dto.assignment.request.UserRespondToAssignmentDTO;
import com.nt.rookies.asset.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.nt.rookies.asset.dto.asset.response.AssignmentDTO;
import com.nt.rookies.asset.dto.assignment.ViewAssignmentDTO;
import com.nt.rookies.asset.entity.AssetEntity;
import com.nt.rookies.asset.entity.AssignmentEntity;
import com.nt.rookies.asset.entity.UserEntity;
import com.nt.rookies.asset.exception.AssetException;
import com.nt.rookies.asset.exception.AssignmentException;
import com.nt.rookies.asset.exception.NotFoundException;
import com.nt.rookies.asset.exception.UserException;
import com.nt.rookies.asset.mapper.AssignmentMapper;
import com.nt.rookies.asset.dto.assignment.SearchAssignmentDTO;
import com.nt.rookies.asset.repository.AssetRepository;
import com.nt.rookies.asset.repository.AssignmentRepository;
import com.nt.rookies.asset.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class AssignmentService {

    @Autowired
    private AssignmentMapper assignmentMapper;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private UserRepository userRepository;

    public AssignmentDTO createAssignment(AssignmentDTO inputDTO) {
        AssignmentDTO result = null;
        AssignmentEntity toSaveAssignment = null;
        try {
            toSaveAssignment = assignmentMapper.convertToEntity(inputDTO);
            AssetEntity toSaveAsset = assetRepository.findById(inputDTO.getAssetCode())
                    .orElseThrow(() -> new AssetException(AssetException.ASSET_NOT_FOUND));
            UserEntity toSaveAssignTo = userRepository.findById(inputDTO.getAssignTo())
                    .orElseThrow(() -> new UserException(UserException.USER_NOT_FOUND));
            UserEntity toSaveAssignBy = userRepository.findByUsername(inputDTO.getAssignBy())
                    .orElseThrow(() -> new UserException(UserException.USER_NOT_FOUND));
            toSaveAssignment.setAsset(toSaveAsset);
            toSaveAssignment.setAssignTo(toSaveAssignTo);
            toSaveAssignment.setAssignBy(toSaveAssignBy);
            toSaveAssignment.setCreatedBy(toSaveAssignBy.getStaffCode());

            toSaveAssignment.setState(AssignmentEntity.AssignStateEnum.WAITING_FOR_ACCEPTANCE);
            toSaveAssignment.setIsDeleted(false);
        } catch (Exception e) {
            throw new AssignmentException(AssignmentException.ERR_CONVERT_DTO_ENTITY_FAIL);
        }

        result = assignmentMapper.convertToDTO(assignmentRepository.save(toSaveAssignment));

        return result;
    }


    public AssignmentDTO getById(Integer id) {
        AssignmentEntity entity = assignmentRepository.findById(id)
                .orElseThrow(() -> new AssignmentException(AssignmentException.ASSIGN_NOT_FOUND));

        return assignmentMapper.convertToDTO(entity);
    }

    public List<AssignmentDTO> getDefaultList() {
        List<AssignmentEntity> list = assignmentRepository.findAll();
        if (list.isEmpty()) {
            throw new AssignmentException(AssignmentException.LIST_NOT_FOUND);
        }
        List<AssignmentEntity> defaultList = list.stream()
                .filter(assign -> !assign.getIsDeleted() &&
                        (assign.getState().equals(AssignmentEntity.AssignStateEnum.ACCEPTED) ||
                                assign.getState().equals(AssignmentEntity.AssignStateEnum.WAITING_FOR_ACCEPTANCE)))
                .collect(Collectors.toList());
        return assignmentMapper.convertToListDTO(defaultList);
    }

    public List<AssignmentDTO> getAllByFilterAndSearch(SearchAssignmentDTO searchData) {
        List<AssignmentEntity> list;
        if (searchData.getSearchKey().isEmpty()) {
            list = assignmentRepository.findAll().stream()
                    .filter(assign -> !assign.getIsDeleted())
                    .collect(Collectors.toList());
        } else {
            list = assignmentRepository.getListBySearchKey(searchData.getSearchKey());
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (list.isEmpty()) {
            return assignmentMapper.convertToListDTO(list);
        }
        List<AssignmentEntity> defaultFilterList = list.stream()
                .filter(assign -> assign.getState().equals(AssignmentEntity.AssignStateEnum.ACCEPTED) ||
                        assign.getState().equals(AssignmentEntity.AssignStateEnum.WAITING_FOR_ACCEPTANCE))
                .collect(Collectors.toList());
        if (searchData.getState().isEmpty() && searchData.getAssignedDate().isEmpty()) {
            return assignmentMapper.convertToListDTO(defaultFilterList);
        }
        if (!searchData.getState().isEmpty() && !searchData.getAssignedDate().isEmpty()) {
            List<AssignmentEntity> newList = list.stream()
                    .filter(assign -> assign.getState().equals(AssignmentEntity.AssignStateEnum.check(searchData.getState()).get())
                            && (assign.getAssignDate().isEqual(LocalDate.parse(searchData.getAssignedDate(), formatter).atStartOfDay())
                            || assign.getAssignDate().isAfter(LocalDate.parse(searchData.getAssignedDate(), formatter).atStartOfDay()))
                            && assign.getAssignDate().isBefore(LocalDate.parse(searchData.getAssignedDate(), formatter).plusDays(1).atStartOfDay()))
                    .collect(Collectors.toList());
            return assignmentMapper.convertToListDTO(newList);
        } else if (!searchData.getAssignedDate().isEmpty()) {
            List<AssignmentEntity> newList = defaultFilterList.stream()
                    .filter(assign -> (assign.getAssignDate().isEqual(LocalDate.parse(searchData.getAssignedDate(), formatter).atStartOfDay())
                            || assign.getAssignDate().isAfter(LocalDate.parse(searchData.getAssignedDate(), formatter).atStartOfDay()))
                            && assign.getAssignDate().isBefore(LocalDate.parse(searchData.getAssignedDate(), formatter).plusDays(1).atStartOfDay()))
                    .collect(Collectors.toList());
            return assignmentMapper.convertToListDTO(newList);
        } else {
            List<AssignmentEntity> newList = list.stream()
                    .filter(assign -> assign.getState().equals(AssignmentEntity.AssignStateEnum.check(searchData.getState()).get()))
                    .collect(Collectors.toList());
            return assignmentMapper.convertToListDTO(newList);
        }
    }

    public AssignmentDTO updateAssignment(AssignmentDTO inputDTO) {
        AssignmentEntity toUpdateAssignment = assignmentRepository.findById(inputDTO.getId())
                .orElseThrow(() -> new AssignmentException(AssignmentException.ASSIGN_NOT_FOUND));
        AssetEntity toUpdateAsset = assetRepository.findById(inputDTO.getAssetCode())
                .orElseThrow(() -> new AssetException(AssetException.ASSET_NOT_FOUND));
        UserEntity toUpdateUser = userRepository.findById(inputDTO.getAssignTo())
                .orElseThrow(() -> new UserException(UserException.USER_NOT_FOUND));
        UserEntity updatedAdmin = userRepository.findByUsername(inputDTO.getAssignBy())
                .orElseThrow(() -> new UserException(UserException.USER_NOT_FOUND));

        toUpdateAssignment.setAssignTo(toUpdateUser);
        toUpdateAssignment.setAsset(toUpdateAsset);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime toSaveAssignDate = LocalDate.parse(inputDTO.getAssignDateString(), formatter).atStartOfDay();
        toUpdateAssignment.setNote(inputDTO.getNote());
        toUpdateAssignment.setAssignDate(toSaveAssignDate);
        toUpdateAssignment.setLastUpdatedDate(LocalDateTime.now());
        toUpdateAssignment.setLastUpdatedBy(updatedAdmin.getStaffCode());
        return assignmentMapper.convertToDTO(assignmentRepository.save(toUpdateAssignment));
    }

    public List<ViewAssignmentDTO> getListAssignmentOfUser(String userCode) throws UserException {
        Optional<UserEntity> currentUser = userRepository.findById(userCode);
        if (currentUser.isEmpty()) {
            throw new UserException(UserException.USER_NOT_FOUND);
        }
        try {
            List<AssignmentEntity> assignments = assignmentRepository.findAllByUserCode(userCode);
            List<AssignmentEntity> newList = assignments.stream()
                    .filter(assign -> assign.getAssignDate().isBefore(LocalDate.now().plusDays(1).atStartOfDay()))
                    .collect(Collectors.toList());
            return assignmentMapper.toDTOList(newList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException("Cannot find any assignment");
        }
    }

    public AssignmentDTO acceptOrDeclineOwnAssignment(UserRespondToAssignmentDTO inputDTO) {
        Integer assignmentID = inputDTO.getAssignmentID();
        String action = inputDTO.getAction();
        if (inputDTO.getAction().isEmpty()) {
            throw new AssignmentException(AssignmentException.ASSIGN_RESPOND_FAIL);
        } else if (!action.isEmpty() && !(action.equals("Accept") || action.equals("Decline"))) {
            throw new AssignmentException(AssignmentException.ASSIGN_RESPOND_FAIL);
        }
        AssignmentEntity toUpdateAssignment = assignmentRepository.findById(assignmentID)
                .orElseThrow(() -> new AssignmentException(AssignmentException.ASSIGN_NOT_FOUND));
        AssetEntity toUpdateAsset = assetRepository.findById(toUpdateAssignment.getAsset().getAssetCode())
                .orElseThrow(() -> new AssetException(AssetException.ASSET_NOT_FOUND));
        if (!action.isEmpty() && action.equals("Accept")) {
            toUpdateAssignment.setState(AssignmentEntity.AssignStateEnum.ACCEPTED);
            toUpdateAsset.setState(AssetEntity.EState.ASSIGNED);
            toUpdateAssignment.setLastUpdatedDate(LocalDateTime.now());
            toUpdateAssignment.setLastUpdatedBy(toUpdateAssignment.getAssignTo().getStaffCode());
            toUpdateAsset.setLastUpdatedDate(LocalDateTime.now());
            toUpdateAsset.setLastUpdatedBy(toUpdateAssignment.getAssignTo().getStaffCode());
            assetRepository.save(toUpdateAsset);
            return assignmentMapper.convertToDTO(assignmentRepository.save(toUpdateAssignment));
        }
        toUpdateAssignment.setState(AssignmentEntity.AssignStateEnum.DECLINED);
        toUpdateAsset.setState(AssetEntity.EState.AVAILABLE);
        toUpdateAssignment.setLastUpdatedDate(LocalDateTime.now());
        toUpdateAssignment.setLastUpdatedBy(toUpdateAssignment.getAssignTo().getStaffCode());
        toUpdateAsset.setLastUpdatedDate(LocalDateTime.now());
        toUpdateAsset.setLastUpdatedBy(toUpdateAssignment.getAssignTo().getStaffCode());
        assetRepository.save(toUpdateAsset);
        return assignmentMapper.convertToDTO(assignmentRepository.save(toUpdateAssignment));
    }

    public AssignmentDTO disableAssignment(Integer Id) {
        AssignmentEntity toDisableAssignment = assignmentRepository.findById(Id)
                .orElseThrow(() -> new AssignmentException(AssignmentException.ASSIGN_NOT_FOUND));
        toDisableAssignment.setIsDeleted(true);
        AssignmentDTO result = assignmentMapper.convertToDTO(assignmentRepository.save(toDisableAssignment));
        return result;
    }
}
