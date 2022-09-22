package com.nt.rookies.asset.service;


import com.nt.rookies.asset.dto.ReturnRequestDTO;
import com.nt.rookies.asset.dto.ViewReturnRequestDTO;
import com.nt.rookies.asset.entity.AssetEntity;
import com.nt.rookies.asset.entity.AssignmentEntity;
import com.nt.rookies.asset.entity.ReturnRequestEntity;
import com.nt.rookies.asset.entity.UserEntity;
import com.nt.rookies.asset.exception.AssignmentException;
import com.nt.rookies.asset.exception.NotFoundException;
import com.nt.rookies.asset.exception.RequestException;
import com.nt.rookies.asset.exception.UserException;
import com.nt.rookies.asset.mapper.RequestMapper;
import com.nt.rookies.asset.repository.AssetRepository;
import com.nt.rookies.asset.repository.AssignmentRepository;
import com.nt.rookies.asset.repository.ReturnRequestRepository;
import com.nt.rookies.asset.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.nt.rookies.asset.entity.AssetEntity.EState.AVAILABLE;
import static com.nt.rookies.asset.entity.AssignmentEntity.AssignStateEnum.ACCEPTED;
import static com.nt.rookies.asset.entity.ReturnRequestEntity.ReturnState.COMPLETED;


@Service
@Transactional
public class ReturnRequestService {

	@Autowired
	AssetRepository assetRepository;
	@Autowired
	private ReturnRequestRepository repository;
	@Autowired
	private AssignmentRepository assignmentRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RequestMapper requestMapper;


	public ReturnRequestDTO createReturnRequest(String username, Integer assignId) {

		AssignmentEntity assignment = assignmentRepository.findById(assignId)
				.orElseThrow(() -> new AssignmentException(AssignmentException.ASSIGN_NOT_FOUND));
		UserEntity user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UserException(UserException.USER_NOT_FOUND));
		try {
			if (!repository.existsByAssignment(assignment)) {
				ReturnRequestEntity entity = new ReturnRequestEntity();
				entity.setAssignment(assignment);
				entity.setRequestBy(user.getUsername());
				entity.setState(ReturnRequestEntity.ReturnState.WAITING_FOR_RETURNING);
				entity.setCreatedDate(LocalDateTime.now());
				entity.setCreatedBy(user.getUsername());
				updateStateOfAssignment(assignment);
				return requestMapper.convertToDto(repository.save(entity));
			}
			return null;
		} catch (Exception ex) {
			throw new RequestException(RequestException.CREATED_REQUEST_FAIL);
		}
	}


	public void updateStateOfAssignment(AssignmentEntity assignment) {
		try {
			assignment.setState(AssignmentEntity.AssignStateEnum.WAITING_FOR_RETURNING);
			assignment.setLastUpdatedDate(LocalDateTime.now());
			assignmentRepository.save(assignment);
		} catch (Exception ex) {
			throw new AssignmentException(AssignmentException.ASSIGN_UPDATE_FAIL);
		}
	}


	public boolean ifAssignmentHasReturnRequest(Integer assignId) {
		AssignmentEntity assignment = assignmentRepository.findById(assignId)
				.orElseThrow(() -> new AssignmentException(AssignmentException.ASSIGN_NOT_FOUND));
		return repository.existsByAssignmentAndState(assignment, ReturnRequestEntity.ReturnState.WAITING_FOR_RETURNING);
	}

	public List<ViewReturnRequestDTO> getList() {
		List<ReturnRequestEntity> viewReturnRequest = repository.findAll();
		List<ViewReturnRequestDTO> result = convertToViewReturnRequestDTOList(viewReturnRequest);

		return result;
	}

	public ViewReturnRequestDTO getReturnRequestById(int id) {
		ReturnRequestEntity entity = repository.findById(id).get();
		return requestMapper.convertViewDto(entity);
	}

	public List<ViewReturnRequestDTO> getAllByFilterAndSearch(String state, String returnDate, String searchKey) {
		List<ReturnRequestEntity> list;
		if (searchKey.isEmpty()) {
			list = repository.findAll();
		} else {
			list = repository.getListBySearchKey(searchKey);
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		if (list.isEmpty()) {
			return requestMapper.toViewListDto(list);
		}
		if (state.isEmpty() && returnDate.isEmpty()) {
			return convertToViewReturnRequestDTOList(list);

		}
		if (!state.isEmpty() && !returnDate.isEmpty()) {
			List<ReturnRequestEntity> newList = list.stream()
					.filter(assign -> assign.getState().equals(ReturnRequestEntity.ReturnState.check(state).get())
							&& assign.getReturnDate().isEqual(LocalDate.parse(returnDate, formatter).atStartOfDay()))
					.collect(Collectors.toList());
			return convertToViewReturnRequestDTOList(newList);

		} else if (!returnDate.isEmpty()) {
			List<ReturnRequestEntity> newList = list.stream()
					.filter(assign -> assign.getReturnDate().isEqual(LocalDate.parse(returnDate, formatter).atStartOfDay()))
					.collect(Collectors.toList());
			return convertToViewReturnRequestDTOList(newList);

		} else {
			List<ReturnRequestEntity> newList = list.stream()
					.filter(assign -> assign.getState().equals(ReturnRequestEntity.ReturnState.check(state).get()))
					.collect(Collectors.toList());
			return convertToViewReturnRequestDTOList(newList);
		}
	}

	public ViewReturnRequestDTO getRequestDetail(Integer id) {
		ReturnRequestEntity entity = repository.findById(id)
				.orElseThrow(() -> new RequestException(RequestException.REQUEST_NOT_FOUND));
		ViewReturnRequestDTO result = requestMapper.convertViewDto(entity);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		result.setAssetName(entity.getAssignment().getAsset().getAssetName());
		result.setAssetCode(entity.getAssignment().getAsset().getAssetCode());
		result.setAssignDateString(entity.getAssignment().getAssignDate().format(formatter));
		result.setRequestBy(entity.getRequestBy());
		result.setAcceptBy(entity.getAcceptBy());
		result.setReturnDate(entity.getReturnDate().toString());

		return result;
	}

	private ViewReturnRequestDTO convertToViewReturnRequestDTO(ReturnRequestEntity input) {
		ViewReturnRequestDTO result = requestMapper.convertViewDto(input);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		result.setAssetName(input.getAssignment().getAsset().getAssetName());
		result.setAssetCode(input.getAssignment().getAsset().getAssetCode());
		result.setAssignDateString(input.getAssignment().getAssignDate().format(formatter));
		result.setRequestBy(input.getRequestBy());
		result.setAcceptBy(input.getAcceptBy());
		if (input.getReturnDate() == null)
			result.setReturnDate("");
		else
			result.setReturnDate(input.getReturnDate().toString());
		

		return result;
	}

	private List<ViewReturnRequestDTO> convertToViewReturnRequestDTOList(List<ReturnRequestEntity> input) {
		List<ViewReturnRequestDTO> result = input.stream().map(returnRequest
				-> convertToViewReturnRequestDTO(returnRequest)).collect(Collectors.toList());

		return result;
	}

	public ViewReturnRequestDTO acceptRequest(Integer requestId, String userCode) throws NotFoundException {
		try {
			Optional<ReturnRequestEntity> request = repository.findById(requestId);
			if (!request.isPresent()) {
				throw new NotFoundException("Cannot find return request");
			}

			ReturnRequestEntity returnRequest = request.get();
			if (returnRequest.getState().toString().equals("COMPLETED")) {
				throw new NotFoundException("Return request has been accepted before");
			}

			Optional<UserEntity> user = userRepository.findById(userCode);
			if (!user.isPresent()) {
				throw new NotFoundException("Cannot find user doing accept request");
			}

			AssetEntity asset = returnRequest.getAssignment().getAsset();
			asset.setState(AVAILABLE);
			assetRepository.save(asset);

			AssignmentEntity assignment = returnRequest.getAssignment();
			assignment.setIsDeleted(true);
			assignment.setState(ACCEPTED);
			assignmentRepository.save(assignment);

			returnRequest.setState(COMPLETED);
			returnRequest.setAcceptBy(user.get().getUsername());
			returnRequest.setReturnDate(LocalDateTime.now());
			repository.save(returnRequest);

			ViewReturnRequestDTO result = requestMapper.convertToDTO(returnRequest);

			return result;
		} catch (NotFoundException e) {
			String message = e.getMessage();
			if (message.equals("Cannot find return request")) {
				throw new NotFoundException("Cannot find return request");
			} else if (message.equals("Cannot find user doing accept request")) {
				throw new NotFoundException("Cannot find user doing accept request");
			} else {
				throw new NotFoundException("Return request has been accepted before");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new NotFoundException("Cannot accept request");
		}
	}
}

