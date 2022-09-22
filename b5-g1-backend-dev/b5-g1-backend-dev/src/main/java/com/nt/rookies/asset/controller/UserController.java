package com.nt.rookies.asset.controller;

import java.util.List;

import javax.validation.Valid;

import com.nt.rookies.asset.dto.CheckPasswordDTO;
import com.nt.rookies.asset.dto.DisableUserDTO;
import com.nt.rookies.asset.dto.UpdatePasswordDTO;
import com.nt.rookies.asset.dto.UserDTO;
import com.nt.rookies.asset.dto.UserEditDTO;
import com.nt.rookies.asset.service.UserService;
import com.nt.rookies.asset.util.UserFormatUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	UserFormatUtils userFormatUtils;

	@PutMapping("/disable")
	ResponseEntity<DisableUserDTO> disabled(@RequestBody DisableUserDTO user) {
		DisableUserDTO disabledUser = userService.disabledUser(user);
		return new ResponseEntity<>(disabledUser, HttpStatus.OK);
	}

	@GetMapping("/check/{staffCode}")
	boolean checkUser(@PathVariable("staffCode") String staffCode) {
		boolean check = userService.checkUserInAssignment(staffCode);
		return check;
	}

	@PutMapping("/change-password")
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STAFF')")
	ResponseEntity<?> changePassword(@RequestBody UpdatePasswordDTO passwordDTO) {
		boolean result;
		result = userService.updatePassword(passwordDTO);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PostMapping("/get-password")
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STAFF')")
	ResponseEntity<?> getPassword(@RequestBody CheckPasswordDTO checkPasswordDTO) {
		String result = userService.checkPassword(checkPasswordDTO);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PostMapping("")
	@PreAuthorize("hasAuthority('ADMIN')")
	ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO user) {
		UserDTO dto = userService.createUser(user);
		return new ResponseEntity<>(dto, HttpStatus.CREATED);
	}

	@PutMapping("")
	@PreAuthorize("hasAuthority('ADMIN')")
	ResponseEntity<UserEditDTO> editUser(@Valid @RequestBody UserEditDTO user) {
		UserEditDTO dto = userService.editUser(user);
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	@GetMapping("")
	ResponseEntity<List<UserDTO>> getAllActiveUsers() {
		return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
	}

	@GetMapping("/search/{searchKey}")
	ResponseEntity<List<UserDTO>> getUsersBySearch(@PathVariable("searchKey") String searchKey) {
		return new ResponseEntity<>(userService.getUsersBySearch(searchKey), HttpStatus.OK);
	}

	@GetMapping("/get/{username}")
	ResponseEntity<List<UserDTO>> getUsersByLocation(@PathVariable("username") String username) {
		return new ResponseEntity<>(userService.getUsersByLocation(username), HttpStatus.OK);
	}

	@GetMapping("/filter-search/")
	ResponseEntity<List<UserDTO>> getUsersBySearchAndFilter(@RequestParam("searchKey") String searchKey,
			@RequestParam("filter") String filter) {
		if (filter == null || filter.trim().length() < 1) {
			return new ResponseEntity<>(userService.getUsersBySearch(searchKey), HttpStatus.OK);
		}
		if (searchKey == null || searchKey.trim().length() < 1) {
			return new ResponseEntity<>(userService.getUsersByType(filter), HttpStatus.OK);
		}
		return new ResponseEntity<>(userService.getUsersBySearchAndFilter(searchKey, filter), HttpStatus.OK);
	}

	@GetMapping("/type/{type}")
	ResponseEntity<List<UserDTO>> getUsersByType(@PathVariable("type") String type) {
		return new ResponseEntity<>(userService.getUsersByType(type), HttpStatus.OK);
	}

	@GetMapping("/{staffCode}")
	ResponseEntity<UserEditDTO> getUserById(@PathVariable("staffCode") String staffCode) {
		UserEditDTO dto = userService.getByStaffCode(staffCode);
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	@GetMapping("/list-with-last-created/{username}")
	ResponseEntity<List<UserDTO>> getUsersWithLastCreatedUser(@PathVariable("username") String username) {
		return new ResponseEntity<>(userService.findAllWithNewUserTop(username, "create"), HttpStatus.OK);
	}

	@GetMapping("/list-with-last-edit/{username}")
	ResponseEntity<List<UserDTO>> getUsersWithLastEditUser(@PathVariable("username") String username) {
		return new ResponseEntity<>(userService.findAllWithNewUserTop(username, "edit"), HttpStatus.OK);
	}
}
