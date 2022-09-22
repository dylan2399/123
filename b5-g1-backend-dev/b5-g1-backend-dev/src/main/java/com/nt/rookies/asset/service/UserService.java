package com.nt.rookies.asset.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.nt.rookies.asset.dto.CheckPasswordDTO;
import com.nt.rookies.asset.dto.DisableUserDTO;
import com.nt.rookies.asset.dto.UpdatePasswordDTO;
import com.nt.rookies.asset.dto.UserDTO;
import com.nt.rookies.asset.dto.UserEditDTO;
import com.nt.rookies.asset.entity.UserEntity;
import com.nt.rookies.asset.entity.UserEntity.EStatus;
import com.nt.rookies.asset.exception.UserException;
import com.nt.rookies.asset.mapper.UserMapper;
import com.nt.rookies.asset.repository.AssignmentRepository;
import com.nt.rookies.asset.repository.UserRepository;
import com.nt.rookies.asset.util.UserFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserFormatUtils userFormatUtils;

	@Autowired
	private AssignmentRepository assignmentRepository;

	private PasswordEncoder encoder = new BCryptPasswordEncoder();

	@Autowired
	private UserMapper userMapper;

	public boolean disableUser(DisableUserDTO userDTO) {

		UserEntity entity = userRepository.findById(userDTO.getStaffCode())
				.orElseThrow(() -> new UserException(UserException.USER_NOT_FOUND));
		entity.setStatus(EStatus.DISABLE);
		try {
			// set new status
			userRepository.save(entity);
			return true;

		}
		catch (Exception e) {
			logger.warn(e.getMessage());
			throw new UserException(UserException.ERR_DISABLE_USER_FAIL);// ERR_DISABLE_USER_FAIL
		}

	}

	public DisableUserDTO disabledUser(DisableUserDTO userDTO) {
		UserEntity entity = userRepository.findById(userDTO.getStaffCode())
				.orElseThrow(() -> new UserException(UserException.USER_NOT_FOUND));
		entity.setStatus(EStatus.DISABLE);
		try {
			userRepository.save(entity);
			return userMapper.toDto(entity);
		}
		catch (Exception e) {
			logger.warn(e.getMessage());
			throw new UserException(UserException.ERR_DISABLE_USER_FAIL);// ERR_DISABLE_USER_FAIL
		}
	}

	public boolean checkUserInAssignment(String staffCode) {
		try {
//				List<AssignmentEntity> list = assignmentRepository.existsByAssignBy(staffcode);
//				System.out.println(list.get(0).getId());
			if (assignmentRepository.existsByAssignBy(staffCode).size() > 0
					|| assignmentRepository.existsByAssignTo(staffCode).size() > 0) {
				return true;
			}
		}
		catch (Exception ex) {
			logger.warn(ex.getMessage());
			throw new UserException(UserException.USER_NOT_FOUND);
		}

		return false;
	}

	public boolean updatePassword(UpdatePasswordDTO updatePasswordDTO) {
		boolean result = false;
		try {
			Optional<UserEntity> existedUser = userRepository.findByUsername(updatePasswordDTO.getUsername());
			if (!existedUser.isPresent()) {
				logger.info("User {} not found", updatePasswordDTO.getUsername());
				throw new UserException(UserException.USER_NOT_FOUND);
			}
			UserEntity user = existedUser.get();
			if (this.encoder.matches(updatePasswordDTO.getOldPassword(), user.getPassword())) {
				user.setPassword(encoder.encode(updatePasswordDTO.getNewPassword()));
				userRepository.save(user);
				result = true;
			}
			else {
				throw new UserException(UserException.ERR_WRONG_OLD_PASSWORD);
			}
		}
		catch (UserException ex) {
			throw new UserException(ex.getCodeResponse());
		}
		catch (Exception e) {
			logger.info("Fail to update user {}", updatePasswordDTO.getUsername());
			throw new UserException(UserException.ERR_UPDATE_USER_FAIL);
		}

		return result;
	}

	public String checkPassword(CheckPasswordDTO checkPasswordDTO) {
		try {
			Optional<UserEntity> existedUser = userRepository.findByUsername(checkPasswordDTO.getUsername());
			if (!existedUser.isPresent()) {
				logger.info("User {} not found", checkPasswordDTO.getUsername());
				throw new UserException(UserException.USER_NOT_FOUND);
			}
			UserEntity user = existedUser.get();
			if (!this.encoder.matches(checkPasswordDTO.getOldPassword(), user.getPassword())) {
				return UserException.ERR_WRONG_OLD_PASSWORD.getMessage();
			}
		}
		catch (UserException ex) {
			throw new UserException(ex.getCodeResponse());
		}

		return "";
	}

	public UserEntity findByUsername(String username) {
		return userRepository.findByUsername(username).orElse(null);
	}

	public UserDTO createUser(UserDTO user) {
		String role = user.getType();
		if (!role.equalsIgnoreCase(UserEntity.EType.ADMIN.name())
				&& !role.equalsIgnoreCase(UserEntity.EType.STAFF.name())) {
			throw new UserException(UserException.USER_TYPE_NOT_FOUND);
		}
		user.setType(role.toUpperCase());
		UserEntity entity = userMapper.convertToEntity(user);
		if (userFormatUtils.isFirstNameContainWhiteSpace(entity.getFirstName())) {
			throw new UserException(UserException.FIRST_NAME_CONTAINS_WHITESPACE);
		}
		entity.setFirstName(userFormatUtils.formatName(entity.getFirstName()));
		entity.setLastName(userFormatUtils.formatName(entity.getLastName()));
		entity.setFirstLogin(true);
		entity.setStatus(UserEntity.EStatus.ENABLE);
		if (user.getLocation() == null || user.getLocation().equalsIgnoreCase("undefined")
				|| user.getLocation().length() == 0) {
			entity.setLocation("Hanoi");
		}
		String username = getGeneratedUsername(entity.getFirstName(), entity.getLastName());
		entity.setUsername(username);
		int userAge = userFormatUtils.userAge(entity.getDob());
		if (userAge < 18) {
			throw new UserException(UserException.USER_DOB_INVALID);
		}
		if (!userFormatUtils.compareDate(entity.getDob(), entity.getJoinedDate())) {
			throw new UserException(UserException.USER_JOINED_DATE_EARLIER);
		}
		if (!userFormatUtils.joinedDateIsValid(entity.getDob(), entity.getJoinedDate())) {
			throw new UserException(UserException.USER_JOINED_DATE_VALID);
		}
		if (userFormatUtils.isWeekend(entity.getJoinedDate())) {
			throw new UserException(UserException.USER_JOINED_DATE_IS_WEEKEND);
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
		String password = entity.getUsername() + "@" + entity.getDob().format(formatter);
		entity.setPassword(encoder.encode(password));
		entity.setCreatedDate(LocalDateTime.now());
		entity.setLastUpdatedDate(LocalDateTime.now());
		if(this.findByUsername(user.getCreatedBy()) != null){
			entity.setCreatedBy(this.findByUsername(user.getCreatedBy()).getStaffCode());
		}
		// save
		try {
			UserEntity saveUser = userRepository.save(entity);
			return userMapper.convertToDto(saveUser);
		}
		catch (Exception ex) {
			throw new UserException(UserException.USER_CREATE_DATA_FAIL);
		}

	}

	public List<UserDTO> getUsersBySearch(String searchKey) {
		List<UserEntity> list = userRepository.findUserBySearchKey(searchKey);
		if (list.isEmpty()) {
			throw new UserException(UserException.LIST_NOT_FOUND);
		}
		return userMapper.toListDto(list);
	}

	public List<UserDTO> getAll() {
		List<UserEntity> list = userRepository.findAllByStatus(UserEntity.EStatus.ENABLE);
		if (list.isEmpty()) {
			throw new UserException(UserException.LIST_NOT_FOUND);
		}
		return userMapper.toListDto(list);
	}

	public List<UserDTO> getUsersBySearchAndFilter(String searchKey, String filter) {
		List<UserEntity> list = userRepository.findUserBySearchKeyAndFilter(searchKey, filter);
		if (list.isEmpty()) {
			throw new UserException(UserException.LIST_NOT_FOUND);
		}
		return userMapper.toListDto(list);
	}

	public List<UserDTO> getUsersByType(String type) {
		List<UserEntity> list = userRepository.findAllByStatusAndType(UserEntity.EStatus.ENABLE.name(), type);
		if (list.isEmpty()) {
			throw new UserException(UserException.LIST_NOT_FOUND);
		}
		return userMapper.toListDto(list);
	}

	public List<UserDTO> getUsersByLocation(String username) {
		UserEntity userEntity = userRepository.findByUsername(username)
				.orElseThrow(() -> new UserException(UserException.USER_NOT_FOUND));
		List<UserEntity> list = userRepository.findByLocation(userEntity.getLocation(), userEntity.getStaffCode());
		if (list.isEmpty()) {
			throw new UserException(UserException.LIST_NOT_FOUND);
		}
		try {
			return userMapper.toListDto(list);
		}
		catch (Exception ex) {
			logger.warn(ex.getMessage());
			throw new UserException(UserException.LIST_NOT_FOUND);
		}
	}

	public String getGeneratedUsername(String fName, String lName) {
		String userName = fName.trim().toLowerCase();
		String afterStr = "";
		for (String s : lName.trim().split(" ")) {
			afterStr += s.charAt(0);
		}
		userName += afterStr;
		String lastIndex = "";
		if (userRepository.findLastUsername(userName) != null) {
			String lastUsername = userRepository.findLastUsername(userName);
			char[] chars = lastUsername.toCharArray();
			for (char c : chars) {
				if (Character.isDigit(c)) {
					String lastNumb = String.valueOf(c);
					lastIndex += Integer.parseInt(lastNumb) + 1;
				}
				if (c == chars[chars.length - 1] && !Character.isDigit(c)) {
					lastIndex += 1;
				}
			}
		}
		return userName.toLowerCase() + lastIndex;
	}

	public UserEditDTO getByStaffCode(String staffCode) {
		UserEntity entity = userRepository.findById(staffCode)
				.orElseThrow(() -> new UserException(UserException.USER_NOT_FOUND));
		try {
			return userMapper.convertToEditDto(entity);
		}
		catch (Exception ex) {
			throw new UserException(UserException.ERR_CONVERT_DTO_ENTITY_FAIL);
		}
	}

	public UserEntity updatePasswordFirstTime(UserEntity updateEntity) {

		return userRepository.save(updateEntity);
	}

	public UserEditDTO editUser(UserEditDTO user) {
		UserEntity oldUser = userRepository.findById(user.getStaffCode())
				.orElseThrow(() -> new UserException(UserException.USER_NOT_FOUND));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		oldUser.setDob(LocalDate.parse(user.getDob(), formatter));
		if (!user.getGender().equalsIgnoreCase("Male") && !user.getGender().equalsIgnoreCase("Female")) {
			throw new UserException(UserException.USER_GENDER_INVALID);
		}
		oldUser.setGender(user.getGender());
		oldUser.setJoinedDate(LocalDate.parse(user.getJoinedDate(), formatter));
		String role = user.getType();
		if (!role.equalsIgnoreCase(UserEntity.EType.ADMIN.name())
				&& !role.equalsIgnoreCase(UserEntity.EType.STAFF.name())) {
			throw new UserException(UserException.USER_TYPE_NOT_FOUND);
		}
		oldUser.setType(user.getType().toUpperCase());
		oldUser.setLastUpdatedDate(LocalDateTime.now());
		if(this.findByUsername(user.getUpdatedBy()) != null){
			oldUser.setLastUpdatedBy(this.findByUsername(user.getUpdatedBy()).getStaffCode());
		}
		try {
			UserEntity newUser = userRepository.save(oldUser);
			return userMapper.convertToEditDto(newUser);
		}
		catch (Exception ex) {
			throw new UserException(UserException.USER_UPDATE_FAIL);
		}

	}

	public List<UserDTO> findAllWithNewUserTop(String username, String action){
		UserEntity admin = userRepository.findByUsername(username)
				.orElseThrow(() -> new UserException(UserException.ADMIN_NOT_FOUND));
		UserEntity newUser = new UserEntity();
		if(action.equals("create")){
			newUser = findLastCreatedUser(admin);
		}
		if(action.equals("edit")){
			newUser = findLastEditUser(admin);
		}
		List<UserEntity> list = userRepository.findAllWithNewUserTop(admin.getLocation(),
				admin.getStaffCode(), newUser.getStaffCode());
		List<UserEntity> resultList = new ArrayList<>();
		resultList.add(newUser);
		resultList.addAll(list);
		return userMapper.toListDto(resultList);
	}

	public UserEntity findLastCreatedUser(UserEntity admin){
		UserEntity entity = userRepository.findFirstByLocationAndCreatedByOrderByCreatedDateDesc(admin.getLocation()
						, admin.getStaffCode()).orElseThrow(() -> new UserException(UserException.USER_NOT_FOUND));

		return entity;
	}

	public UserEntity findLastEditUser(UserEntity admin){
		UserEntity entity = userRepository.findFirstByLocationAndLastUpdatedByOrderByLastUpdatedDateDesc(admin.getLocation()
						, admin.getStaffCode()).orElseThrow(() -> new UserException(UserException.USER_NOT_FOUND));

		return entity;
	}
}
