package com.nexbus.nexbus_backend.repository;

import com.nexbus.nexbus_backend.model.Bus;
import com.nexbus.nexbus_backend.model.BusOperator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BusRepository extends JpaRepository<Bus, Integer> {

    List<Bus> findByRoute_RouteId(Integer routeId);
    List<Bus> findByOperator_OperatorId(Integer operatorId);
    Long countByOperatorOperatorId(Integer operatorId);
    List<Bus> findByOperator(BusOperator operator);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
           "FROM Bus b WHERE b.busId = :busId AND b.operator.user.role.roleName = :roleName")
    boolean existsByBusIdAndOperatorUserRoleName(@Param("busId") Integer busId, 
                                                 @Param("roleName") String roleName);
}




