package com.nt.rookies.asset.controller;


import com.nt.rookies.asset.dto.ReturnRequestDTO;
import com.nt.rookies.asset.dto.ViewReturnRequestDTO;
import com.nt.rookies.asset.entity.ReturnRequestEntity;
import com.nt.rookies.asset.service.ReturnRequestService;
import com.nt.rookies.asset.util.DecodedToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/return-requests")
@CrossOrigin(origins = "*")
public class ReturnRequestController {

	@Autowired
	private ReturnRequestService returnRequestService;


	@PostMapping("")
	ResponseEntity<ReturnRequestDTO> createReturnRequest(@RequestHeader(name = "Authorization") String stringToken,
														 @RequestBody ReturnRequestDTO returnRequestDTO) {
		DecodedToken token = DecodedToken.getDecoded(stringToken);
		return new ResponseEntity(returnRequestService.createReturnRequest(token.username, returnRequestDTO.getAssignmentId()), HttpStatus.CREATED);


	}

	@GetMapping("/if-assign-waiting-return/{assignId}")
	ResponseEntity ifAssignmentWaitForReturning(@PathVariable("assignId") Integer assignId) {
		return new ResponseEntity(returnRequestService.ifAssignmentHasReturnRequest(assignId), HttpStatus.OK);

	}

	@GetMapping("")
	ResponseEntity<List<ViewReturnRequestDTO>> getList() {
		return new ResponseEntity<>(returnRequestService.getList(), HttpStatus.OK);
	}

	@GetMapping("/returnId")
	ResponseEntity<ViewReturnRequestDTO> getRequestbyId(@PathVariable("requestId") Integer requestId) {
		return new ResponseEntity<>(returnRequestService.getReturnRequestById(requestId), HttpStatus.OK);
	}

	@PostMapping("/search")
	ResponseEntity<List<ViewReturnRequestDTO>> getListBySearchKey(@RequestParam(required = false) String state,
																  @RequestParam(required = false) String returnDate, @RequestParam(required = false) String searchKey) {
		return new ResponseEntity<>(returnRequestService.getAllByFilterAndSearch(state, returnDate, searchKey), HttpStatus.OK);
	}


	@GetMapping("/{assignId}")
	ResponseEntity<ViewReturnRequestDTO> getRequestDetail(@PathVariable("assignId") Integer id) {
		return new ResponseEntity<>(returnRequestService.getRequestDetail(id), HttpStatus.OK);
	}

	@PutMapping("accept/{requestId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	ResponseEntity<ViewReturnRequestDTO> acceptReturnRequestByAdmin(@PathVariable("requestId") Integer
																			requestId, @RequestHeader(name = "Authorization") String stringToken) {
		DecodedToken token = DecodedToken.getDecoded(stringToken);
		return new ResponseEntity<>(returnRequestService.acceptRequest(requestId, token.staffCode), HttpStatus.OK);
	}

}
