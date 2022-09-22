package com.nt.rookies.asset.repository;


import java.util.List;

import com.nt.rookies.asset.entity.AssetEntity;
import com.nt.rookies.asset.entity.AssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<AssignmentEntity, Integer> {

	List<AssignmentEntity> findByStateEqualsAndIsDeletedEquals(AssignmentEntity.AssignStateEnum state, Boolean isDeleted);

	@Query(value = "select * from assignment where assign_to = :assignTo", nativeQuery = true)
	List<AssignmentEntity> existsByAssignTo(@Param("assignTo") String assignTo);

	@Query(value = "select assignment.*, asset.name as asset_name, asset.specification\n" +
			"from assignment inner join asset \n" +
			"on assignment.asset_code = asset.code\n" +
			"where assign_to = :assignTo and assignment.state != 'DECLINED'", nativeQuery = true)
	List<AssignmentEntity> findAllByUserCode(@Param("assignTo") String assignTo);

	@Query(value = "select assignment.*\n" +
			"from assignment inner join return_request\n" +
			"on assignment.id = return_request.assignment_id\n" +
			"where asset_code = :assetCode and assignment.is_deleted = 1 and return_request.state = 'COMPLETED'", nativeQuery = true)
	List<AssignmentEntity> findHistoricalByAssetCode(@Param("assetCode") String assetCode);

	@Query(value = "select assignment.* from assignment inner join user on user.staff_code = assignment.assign_to\n" +
			"inner join asset on asset.code = assignment.asset_code \n" +
			"where (asset_code LIKE %:searchKey% OR asset.name LIKE %:searchKey% OR user.username LIKE %:searchKey%) AND assignment.is_deleted = 0 "
			, nativeQuery = true)
	List<AssignmentEntity> getListBySearchKey(@Param("searchKey") String searchKey);

	@Query(value = "select * from assignment where assign_by = :assignBy", nativeQuery = true)
	List<AssignmentEntity> existsByAssignBy(@Param("assignBy") String assignBy);

	boolean existsByAssetAndStateAndIsDeleted(AssetEntity asset, AssignmentEntity.AssignStateEnum state, boolean isDeleted);
}
