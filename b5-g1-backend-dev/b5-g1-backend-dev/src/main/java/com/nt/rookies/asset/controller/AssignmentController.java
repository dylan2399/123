package com.nt.rookies.asset.controller;

import java.util.List;

import com.nt.rookies.asset.dto.asset.response.AssignmentDTO;
import com.nt.rookies.asset.dto.assignment.request.UserRespondToAssignmentDTO;
import com.nt.rookies.asset.dto.assignment.SearchAssignmentDTO;
import com.nt.rookies.asset.dto.assignment.ViewAssignmentDTO;
import com.nt.rookies.asset.service.AssignmentService;
import com.nt.rookies.asset.util.DecodedToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assignments")
@CrossOrigin(origins = "*")
public class AssignmentController {

	@Autowired
	private AssignmentService assignmentService;

	@PostMapping("")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<AssignmentDTO> createAssignment(@RequestBody AssignmentDTO inputDTO) {
		return new ResponseEntity<AssignmentDTO>(assignmentService.createAssignment(inputDTO), HttpStatus.CREATED);
	}


	@GetMapping("")
	@PreAuthorize("hasAuthority('ADMIN')")
	ResponseEntity<List<AssignmentDTO>> getDefaultList() {
		return new ResponseEntity<>(assignmentService.getDefaultList(), HttpStatus.OK);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	ResponseEntity<AssignmentDTO> getById(@PathVariable("id") Integer id) {
		return new ResponseEntity<>(assignmentService.getById(id), HttpStatus.OK);
	}

	@PostMapping("/search")
	ResponseEntity<List<AssignmentDTO>> getListBySearchKey(@RequestBody SearchAssignmentDTO searchData) {
		return new ResponseEntity<>(assignmentService.getAllByFilterAndSearch(searchData), HttpStatus.OK);
	}

	@PutMapping("")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<AssignmentDTO> updateAssignment(@RequestBody AssignmentDTO inputDTO) {
		return new ResponseEntity<AssignmentDTO>(assignmentService.updateAssignment(inputDTO), HttpStatus.OK);
	}

	@PostMapping("/my-assignments")
	ResponseEntity<List<ViewAssignmentDTO>> getAllUserAssignment(@RequestHeader(name = "Authorization") String stringToken) {
		DecodedToken token = DecodedToken.getDecoded(stringToken);
		return new ResponseEntity<>(assignmentService.getListAssignmentOfUser(token.staffCode), HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/{id}")
	ResponseEntity<AssignmentDTO> deleteAssignment(@PathVariable("id") Integer id) {
		return new ResponseEntity<AssignmentDTO>(assignmentService.disableAssignment(id), HttpStatus.OK);
	}

	@PatchMapping("/users-response")
	ResponseEntity<AssignmentDTO> respondToAssignment(@RequestBody UserRespondToAssignmentDTO inputDTO) {
		return new ResponseEntity<AssignmentDTO>(assignmentService.acceptOrDeclineOwnAssignment(inputDTO), HttpStatus.OK);
	}

}
