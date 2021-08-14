package com.walt;

import com.walt.dao.*;
import com.walt.model.BusyCount;
import com.walt.model.City;
import com.walt.model.Customer;
import com.walt.model.Delivery;
import com.walt.model.Driver;
import com.walt.model.DriverDistance;
import com.walt.model.Restaurant;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;

import javax.annotation.Resource;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest()
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class WaltTest {

    @TestConfiguration
    static class WaltServiceImplTestContextConfiguration {

        @Bean
        public WaltService waltService() {
            return new WaltServiceImpl();
        }
    }

    @Autowired
    WaltService waltService;

    @Resource
    CityRepository cityRepository;

    @Resource
    CustomerRepository customerRepository;

    @Resource
    DriverRepository driverRepository;

    @Resource
    DeliveryRepository deliveryRepository;

    @Resource
    RestaurantRepository restaurantRepository;

    @BeforeEach()
    public void prepareData(){

        City jerusalem = new City("Jerusalem");
        City tlv = new City("Tel-Aviv");
        City bash = new City("Beer-Sheva");
        City haifa = new City("Haifa");

        cityRepository.save(jerusalem);
        cityRepository.save(tlv);
        cityRepository.save(bash);
        cityRepository.save(haifa);

        createDrivers(jerusalem, tlv, bash, haifa);

        createCustomers(jerusalem, tlv, haifa);

        createRestaurant(jerusalem, tlv,haifa);
        
        createDeliveries();
    }

    
    
    private void createRestaurant(City jerusalem, City tlv,City haifa) {
        Restaurant meat = new Restaurant("meat", jerusalem, "All meat restaurant");
        Restaurant vegan = new Restaurant("vegan", tlv, "Only vegan");
        Restaurant cafe = new Restaurant("cafe", tlv, "Coffee shop");
        Restaurant chinese = new Restaurant("chinese", tlv, "chinese restaurant");
        Restaurant mexican = new Restaurant("restaurant", tlv, "mexican restaurant ");
        Restaurant italian = new Restaurant("italian", haifa, "check-post");

        restaurantRepository.saveAll(Lists.newArrayList(meat, vegan, cafe, chinese, mexican,italian));
    }

    private void createCustomers(City jerusalem, City tlv, City haifa) {
        Customer beethoven = new Customer("Beethoven", tlv, "Ludwig van Beethoven");
        Customer mozart = new Customer("Mozart", jerusalem, "Wolfgang Amadeus Mozart");
        Customer chopin = new Customer("Chopin", haifa, "Frédéric François Chopin");
        Customer rachmaninoff = new Customer("Rachmaninoff", tlv, "Sergei Rachmaninoff");
        Customer bach = new Customer("Bach", tlv, "Sebastian Bach. Johann");

        customerRepository.saveAll(Lists.newArrayList(beethoven, mozart, chopin, rachmaninoff, bach));
    }

    private void createDrivers(City jerusalem, City tlv, City bash, City haifa) {
        Driver mary = new Driver("Mary", tlv);
        Driver patricia = new Driver("Patricia", tlv);
        Driver jennifer = new Driver("Jennifer", haifa);
        Driver james = new Driver("James", bash);
        Driver john = new Driver("John", bash);
        Driver robert = new Driver("Robert", jerusalem);
        Driver david = new Driver("David", jerusalem);
        Driver daniel = new Driver("Daniel", tlv);
        Driver noa = new Driver("Noa", haifa);
        Driver ofri = new Driver("Ofri", haifa);
        Driver nata = new Driver("Neta", jerusalem);

        driverRepository.saveAll(Lists.newArrayList(mary, patricia, jennifer, james, john, robert, david, daniel, noa, ofri, nata));
    }
    
    private void createDeliveries() {
    	Customer customer1 = customerRepository.findByName("Bach");
    	Customer customer2 = customerRepository.findByName("Chopin");
    	Driver driver1 = driverRepository.findByName("Mary");
    	Driver driver2 = driverRepository.findByName("Patricia");
    	Driver driver3 = driverRepository.findByName("Noa");
    	Restaurant restaurant1 = restaurantRepository.findByName("meat");
    	Restaurant restaurant2 = restaurantRepository.findByName("italian");
    	Delivery delivery1=new Delivery(driver1, restaurant1, customer1, new Date(2021, 2, 2, 5, 0, 0));
		Delivery delivery2=new Delivery(driver1, restaurant1, customer1, new Date(2021, 3, 3, 12, 0, 0));
    	Delivery delivery3=new Delivery(driver2, restaurant1, customer1, new Date(2021, 1, 1, 18, 0, 0));
    	Delivery delivery4=new Delivery(driver3, restaurant2, customer2, new Date(2020, 1, 1, 18, 0, 0));
		deliveryRepository.saveAll(Lists.newArrayList(delivery1,delivery2,delivery3,delivery4));
	}
    
    @Test
    public void testBasics(){
    	
        assertEquals(((List<City>) cityRepository.findAll()).size(),4);
        assertEquals((driverRepository.findAllDriversByCity(cityRepository.findByName("Beer-Sheva")).size()), 2);
    }
    
   
    @Test
    public void testDeliverHistory(){
    	Driver driver= driverRepository.findByName("Mary");
    	List<Delivery> deliveriesHistory = deliveryRepository.findByDriverOrderByDeliveryTimeDesc(driver);
    	assertEquals((deliveriesHistory.size()),2);
    	Driver driver2 = driverRepository.findByName("Noa");
    	assertEquals(deliveryRepository.findByDriverOrderByDeliveryTimeDesc(driver2).size(), 1);
    	
    	
    }
    
    @SuppressWarnings("deprecation")
  	@Test
      public void testAssignDeliveryAndDriver() {
      	Customer customer = customerRepository.findByName("Bach");
      	Restaurant restaurant = restaurantRepository.findByName("cafe");
      	Date deliveryTime = new Date(2021, 3,3 , 17, 0, 0);
      	Delivery delivery=waltService.createOrderAndAssignDriver(customer, restaurant, deliveryTime);
      	
      	// Success! Patricia got the delivery ! 
      	assertTrue(delivery.getDriver().getName()=="Patricia");
      	
      	//Delivery stored in the DB
      	assertEquals( deliveryRepository.count(),5);
      	
      	//Delivery History working ! 
      	Driver driver = driverRepository.findByName("Patricia");
    	assertEquals(deliveryRepository.findByDriverOrderByDeliveryTimeDesc(driver).size(), 2);
    	
    	Customer customer2 = customerRepository.findByName("Beethoven");
      	Restaurant restaurant2 = restaurantRepository.findByName("vegan");
      	Date deliveryTime2 = new Date(2021, 3,3 , 17, 0, 0);
      	Delivery delivery2=waltService.createOrderAndAssignDriver(customer2, restaurant2, deliveryTime2);
      	
      	// success !! Patricia is not available at this hour . Mary was picked
      	assertTrue(delivery2.getDriver().getName()=="Mary");
   
    	
      	// let's try find availabe driver in Haifa
      	Customer customer3 = customerRepository.findByName("Chopin");
      	Restaurant restaurant3 = restaurantRepository.findByName("italian");
        Delivery delivery3 = waltService.createOrderAndAssignDriver(customer3, restaurant3, new Date(2021, 9, 9, 21, 0, 0));
      	
        // Success !! Noa was picked.
        assertTrue(delivery3.getDriver().getName() == "Noa");
    	
      }
    
    @Test
    public void testCountBusyQuary() {
    	List<BusyCount> busyCounts =  deliveryRepository.countTotalDeliveryByDriver();
    	assertEquals((busyCounts.size() ),3);
    	
  
    	
    }
    
    @Test
    public void testTotalRamkingAndRankingByCity() {
    	List<DriverDistance> rank = waltService.getDriverRankReport();
    	// Success !! Mary Has the highest rank based on her total distance and then Patricia ! 
    	assertTrue(rank.get(0).getDriver().getName()=="Mary");
    	assertTrue(rank.get(1).getDriver().getName()=="Patricia");
    	Driver driver = driverRepository.findByName("Noa");
    	List<DriverDistance> rankByCity = waltService.getDriverRankReportByCity(driver.getCity());
    	
    	// Success !!! Noa is in the highest rank in her city 
    	assertTrue(rankByCity.get(0).getDriver().getName()=="Noa");
    	
    	// Success !!! Mary has the highest rank in her city
    	Driver driver2 = driverRepository.findByName("Mary");
    	List<DriverDistance> rankByCity2 = waltService.getDriverRankReportByCity(driver2.getCity());
    	// Success !!! Mary has the highest rank in her city
    	assertTrue(rankByCity2.get(0).getDriver().getName()=="Mary");
    	
    	
    }
    

    
    
  
}
