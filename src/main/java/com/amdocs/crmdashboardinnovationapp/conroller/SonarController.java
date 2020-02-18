package com.amdocs.crmdashboardinnovationapp.conroller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amdocs.crmdashboardinnovationapp.service.SonarService;

@RestController
@RequestMapping("/sonar")
public class SonarController {
	
	@Autowired
	protected SonarService sonarService;
	
	@GetMapping(value= "/getIssues")
	public String getSonar(@RequestHeader(value="authorization") String auth) {
		return this.sonarService.getUsersIssues(auth);
	}
}