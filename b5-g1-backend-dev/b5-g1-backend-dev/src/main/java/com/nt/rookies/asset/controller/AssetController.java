package com.nt.rookies.asset.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.nt.rookies.asset.dto.AssetDTO;
import com.nt.rookies.asset.dto.asset.response.DetailAssetDTO;
import com.nt.rookies.asset.dto.asset.response.ViewAssetDTO;
import com.nt.rookies.asset.exception.NotFoundException;
import com.nt.rookies.asset.jwt.JwtTokenUtil;
import com.nt.rookies.asset.mapper.LocationMapper;
import com.nt.rookies.asset.service.AssetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assets")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AssetController {

	@Autowired
	private AssetService assetService;

	@Autowired
	private LocationMapper locationMapper;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;


	@GetMapping("")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<List<AssetDTO>> getDefaultList() {
		return new ResponseEntity(assetService.getAllDefault(), HttpStatus.OK);
	}

	@PostMapping("")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<AssetDTO> createAsset(@RequestBody AssetDTO dto) {
		return new ResponseEntity<>(assetService.createAsset(dto), HttpStatus.CREATED);
	}

	@DeleteMapping("/{assetCode}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity deleteAsset(@PathVariable("assetCode") String assetCode) {
		assetService.deleteAsset(assetCode);
		return new ResponseEntity<>("Success delete", HttpStatus.OK);
	}

	@GetMapping("/has-historical/{assetCode}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Boolean> hasHistoricalAssigns(@PathVariable("assetCode") String assetCode) {
		return new ResponseEntity(assetService.hasHistoricalAssign(assetCode), HttpStatus.OK);
	}

	@GetMapping("/asset-has-waiting-accept-assign/{assetCode}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Boolean> hasWaitingAssigns(@PathVariable("assetCode") String assetCode) {
		return new ResponseEntity(assetService.hasWaitingAssign(assetCode), HttpStatus.OK);
	}

	@GetMapping("/default-set")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<List<ViewAssetDTO>>
	retrieveDefaultAssets(HttpServletRequest req) throws NotFoundException {
		String jwtToken = req.getHeader("Authorization").substring(7, req.getHeader("Authorization").length());
		String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
		String location = locationMapper.getLocationFromUsername(username);
		return ResponseEntity.ok(assetService.retrieveAllAssetsByLocationAndDefaultState(location));
	}

	@GetMapping("/sub-set")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<List<ViewAssetDTO>>
	retrieveNonDefaultAssets(HttpServletRequest req) throws NotFoundException {
		String jwtToken = req.getHeader("Authorization").substring(7, req.getHeader("Authorization").length());
		String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
		String location = locationMapper.getLocationFromUsername(username);
		return ResponseEntity.ok(assetService.retrieveAllAssetsByLocationAndNonDefaultState(location));
	}

	@GetMapping("/{assetCode}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<DetailAssetDTO>
	retrieveAssetByAssetCode(HttpServletRequest req,
			@PathVariable("assetCode") String assetCode) throws NotFoundException {
		String jwtToken = req.getHeader("Authorization").substring(7, req.getHeader("Authorization").length());
		String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
		String location = locationMapper.getLocationFromUsername(username);
		return ResponseEntity.ok(assetService.retrieveAssetByAssetCode(location, assetCode));
	}

	@GetMapping("/available-for-assignment")
	public ResponseEntity<List<AssetDTO>> getAvailableAssetForAssignment() {
		return new ResponseEntity<>(assetService.getAvailableAssetForAssignment(), HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/available-for-update-assignment/{assignmentId}")
	public ResponseEntity<List<AssetDTO>> getAvailableAssetForUpdateAssignemnt(@PathVariable("assignmentId") Integer assignmentId) {
		return new ResponseEntity<>(assetService.getAvailableAssetForUpdateAssignment(assignmentId), HttpStatus.OK);
	}
}
