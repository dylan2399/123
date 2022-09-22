package com.nt.rookies.asset.repository;

import com.nt.rookies.asset.entity.AssignmentEntity;
import com.nt.rookies.asset.entity.ReturnRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequestEntity, Integer> {

	boolean existsByAssignment(AssignmentEntity entity);

	boolean existsByAssignmentAndState(AssignmentEntity entity, ReturnRequestEntity.ReturnState state);

	@Query(value = "select * from return_request inner join\n" +
			"(select assignment.id, assignment.asset_code, asset.name from assignment inner join asset on asset.code = assignment.asset_code) as jtbl\n" +
			"on jtbl.id = return_request.assignment_id\n" +
			"where jtbl.asset_code like %:searchKey% or jtbl.name like %:searchKey% or return_request.request_by like %:searchKey% "
			, nativeQuery = true)
	List<ReturnRequestEntity> getListBySearchKey(@Param("searchKey") String searchKey);

}
