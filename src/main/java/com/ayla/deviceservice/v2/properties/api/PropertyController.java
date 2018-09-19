package com.ayla.deviceservice.v2.properties.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PropertyController {
	
	@RequestMapping(value = "/apiv2/dsns/{dsn}/properties", method = RequestMethod.GET)
	public String getDevicePropertiesUsingDsn(@PathVariable("dsn") final String deviceDsn) {
		return deviceDsn;
	}

}
