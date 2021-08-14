package com.walt.dao;

import com.walt.model.BusyCount;
import com.walt.model.City;
import com.walt.model.Driver;
import com.walt.model.Delivery;
import com.walt.model.DriverDistance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DeliveryRepository extends CrudRepository<Delivery, Long> {

	// returning the count of orders made by each driver using class constructors
	@Query("SELECT new com.walt.model.BusyCount(d.driver , COUNT(d.driver) AS busy )"
			+ "FROM Delivery AS d GROUP BY d.driver ORDER BY busy ASC ")
	List<BusyCount> countTotalDeliveryByDriver();
	
	// returning the history deliveries of a driver ordered by time in descending order 
	List<Delivery>findByDriverOrderByDeliveryTimeDesc(Driver driver);
	
	// returning total ranking using projection
	@Query("SELECT d.driver AS driver , SUM(d.distance) AS totalDistance " +
			"FROM Delivery AS d GROUP BY d.driver ORDER BY totalDistance DESC ")
	List<DriverDistance> countTotalDistancesByDriverInterface();
	
	// returning ranking by city using projection
	@Query("SELECT d.driver AS driver , SUM(d.distance) AS totalDistance " +
			"FROM Delivery AS d WHERE d.driver.city = :city GROUP BY d.driver ORDER BY totalDistance DESC ")
	List<DriverDistance> countTotalDistancesByCityInterface(@Param("city")City city);
}


