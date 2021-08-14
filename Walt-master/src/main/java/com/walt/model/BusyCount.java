package com.walt.model;

import javax.management.loading.PrivateClassLoader;

import org.springframework.beans.factory.annotation.Autowired;

public class BusyCount {
	
	@Autowired
	Driver driver;
	
	private Long totalDeliveries;

	public BusyCount(Driver driver, Long totalDeliveries) {
		this.driver = driver;
		this.totalDeliveries = totalDeliveries;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public Long getTotalDeliveries() {
		return totalDeliveries;
	}

	public void setTotalDeliveries(Long totalDeliveries) {
		this.totalDeliveries = totalDeliveries;
	}
	
	

	
	
}
