package com.walt;

import com.walt.dao.CustomerRepository;
import com.walt.dao.DeliveryRepository;
import com.walt.dao.DriverRepository;
import com.walt.model.*;

import ch.qos.logback.core.util.TimeUtil;
import javassist.expr.NewArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@Service
public class WaltServiceImpl implements WaltService {

	@Autowired 
	DriverRepository driverRepository;
	
	@Autowired
	DeliveryRepository deliveryRepository;
	
	@Autowired
	CustomerRepository customerRepository;
	
    @SuppressWarnings("deprecation")
	@Override
    public Delivery createOrderAndAssignDriver(Customer customer, Restaurant restaurant, Date deliveryTime) 
    {
    		
  	    List<BusyCount> driversBusyList = deliveryRepository.countTotalDeliveryByDriver();
  	    Random r = new Random();
  	    Driver driver = null;
  	
  	    try {
  	    	//checks if the customer exists in the DB
  	    	if(!customerRepository.existsById(customer.getId()))
  	    	{
  	    		throw new Exception("Customer Not Exist"); 
  	    	}
  	      //  Delivery DB is not empty
  	      if(!driversBusyList.isEmpty()) 
  	      {
  	    	  for( BusyCount driverBusyCount: driversBusyList)
  	    	  {
  	    	 
  	    		  // driver lives in the same city of the restaurant and customer 
  	    		  String driverCity = driverBusyCount.getDriver().getCity().getName();
  	    		  if (driverCity == customer.getCity().getName() && driverCity == restaurant.getCity().getName())
  	    		  {
  	    			  
  	    			  // checks if the driver is available
  	    			  List<Delivery> availableDeliveries = deliveryRepository.findByDriverOrderByDeliveryTimeDesc(driverBusyCount.getDriver());
    	    			  
  	    			  
  	    			  // if he does not have any delivery history or driver's last delivery is  at least 1 hour less than the order time.
  	    			  if( (availableDeliveries.isEmpty())  ||  ((availableDeliveries.get(0).getDeliveryTime().before(deliveryTime))) && (deliveryTime.getHours() >= availableDeliveries.get(0).getDeliveryTime().getHours()-1))
  	    			  {
    	    			
  	    				  // assign new driver
  	    				  driver = driverBusyCount.getDriver();   	 
  	    				  break;
  	    			  }
  	    		  }
  	    	  }
  	      }
  	      
  	      
  	      // if the delivery DB empty insert any driver matches the relevant place condition
  	      else
  	      {
  	      
  	       List<Driver> driverByCity= driverRepository.findAllDriversByCity(customer.getCity());
  	       for(Driver cityDriver :driverByCity)
  	       {
  	    	   if(cityDriver.getCity().getName() == restaurant.getCity().getName())
  	    	   {
  	    		   driver =cityDriver;
  	    		   break;
  	    	   }
  	       }
  	     }  
  	    
  	    // if driver is not available for delivery 
    	if (driver== null)
    		throw new Exception("No Available Driver In City");
   			
    		 
    	// assign delivery
    	Delivery delivery = new Delivery(driver, restaurant, customer, deliveryTime);
    	//assign distance
    	int distance = r.nextInt(21);
    	delivery.setDistance(distance);
    		 
    	// save to DB
    	deliveryRepository.save(delivery);
    	return delivery;
    		
	 } 
  	    
  	    catch (Exception e) 
  	    {
			e.printStackTrace();
		}
  	 
  	    return null;
    	
    }

    @Override
    public List<DriverDistance> getDriverRankReport() 
    {

    	return deliveryRepository.countTotalDistancesByDriverInterface();
    	      
    }

    @Override
    public List<DriverDistance> getDriverRankReportByCity(City city) {

    	return deliveryRepository.countTotalDistancesByCityInterface(city);
    }
}
