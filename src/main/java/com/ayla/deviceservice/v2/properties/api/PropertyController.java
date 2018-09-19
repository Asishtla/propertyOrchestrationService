package com.ayla.deviceservice.v2.properties.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ayla.cloud.properties.exception.Error;
import com.ayla.cloud.properties.exception.ErrorMapping;
import com.ayla.cloud.properties.exception.ErrorWrapper;
import com.ayla.cloud.properties.exception.TPSException;
import com.ayla.cloud.properties.model.TemplateProperty;
import com.ayla.cloud.properties.model.TemplatePropertyWrapper;
import com.ayla.cloud.properties.service.TemplatePropertyService;
import com.ayla.cloud.properties.model.TemplateProperty;
import com.ayla.cloud.properties.model.TemplatePropertyWrapper;
import com.ayla.cloud.properties.service.TemplatePropertyService;

import com.ayla.template.common.models.ApiError;
import com.ayla.template.common.models.ApiErrorResponse;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;


import javax.inject.Inject;
import javax.validation.Valid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;



@RestController
public class PropertyController {

    /*TODO: Inject these two properties. */
    private final String DEVICETEMPLATEPROPERTYSERVICE = "localhost";
    private final String TEMPLATEPROPERTYSERVICE = "localhost";
    
    //@Inject
    //private TemplatePropertyService propertyService;

    //@Inject
    //private TemplatePropertyService propertyService;
        
    
    @ApiOperation(value = "Get Porperties for a given dsn.", response=Iterable.class)
    @ApiResponses(value= {
            @ApiResponse(code=422, message="[{'code':'TPS-001', 'message':'Invalid Template Id'}"),
            @ApiResponse(code=404, message="[{'code':'TPS-012', 'message': 'Missing Authorization']")})
    @RequestMapping(value = "/apiv2/dsns/{dsn}/properties", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiErrorResponse> getDevicePropertiesUsingDsn(@PathVariable("dsn") final String deviceDsn) {
        
        final RestTemplateBuilder deviceTemplatePropertyServiceBuilder = new RestTemplateBuilder();
        deviceTemplatePropertyServiceBuilder.rootUri(DEVICETEMPLATEPROPERTYSERVICE);
        
        final RestTemplate deviceTemplatePropertyServiceRestTemplate = deviceTemplatePropertyServiceBuilder.build();
        final String devicetemplateMappingPath = "/devicetemplatemapping/v2/dsns/" + deviceDsn;
        /*Assume for now that device template mapping returns a String as value. It should actually return a JSON which we can be parsed 
         */
        String templateId = deviceTemplatePropertyServiceRestTemplate.getForObject(devicetemplateMappingPath, String.class);        
        if ((templateId == null) || templateId.isEmpty()) {
            List<ApiError> apiErrors = new ArrayList<ApiError>();
            ApiError apiError = new ApiError("TMP-004", "TemplateId not found for dsn:" + deviceDsn);
            apiErrors.add(apiError);

            ApiErrorResponse response = new ApiErrorResponse(apiErrors);
            return new ResponseEntity<ApiErrorResponse>(response, new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
        
        
        final RestTemplateBuilder templatePropertyServiceBuilder = new RestTemplateBuilder();
        templatePropertyServiceBuilder.rootUri(TEMPLATEPROPERTYSERVICE);
        
        RestTemplate templatePropertyServiceRestTemplate = templatePropertyServiceBuilder.build();
        final String templatePropertyServiceMappingPath = "/templatepropertyservice/v2/templates/" + templateId + "/properties";
        /*Assume for now that  template property service returns a String as value. It should actually return a JSON which we can be parsed 
         */
        
        List<TemplateProperty> templateProperties = templatePropertyServiceRestTemplate.getForObject(templatePropertyServiceMappingPath, 
                List<TemplateProperty>.class);
        
        if ((templateProperties == null) || templateProperties.isEmpty()) {
            List<ApiError> apiErrors = new ArrayList<ApiError>();
            ApiError apiError = new ApiError("TMP-004", "Template for templateId:"  + templateId + " not found");
            apiErrors.add(apiError);

            ApiErrorResponse response = new ApiErrorResponse(apiErrors);
            return new ResponseEntity<ApiErrorResponse>(response, new HttpHeaders(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<List<TemplateProperty>>(templateProperties, HttpStatus.OK);
    }
        
    
}