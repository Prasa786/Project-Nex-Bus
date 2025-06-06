package com.nexbus.nexbus_backend.repository;

import com.nexbus.nexbus_backend.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Integer> {

    @Query("SELECT r FROM Route r JOIN Bus b ON r.routeId = b.route.routeId " +
           "JOIN BusOperator bo ON b.operator.operatorId = bo.operatorId " +
           "WHERE bo.user.userId = :userId")
    List<Route> findByOperatorUserId(@Param("userId") Integer userId);
    @Query("SELECT r FROM Route r WHERE r.startLocation = :startLocation AND r.endLocation = :endLocation")
    List<Route> findByStartLocationAndEndLocation(@Param("startLocation") String startLocation, @Param("endLocation") String endLocation);
    
    
}


