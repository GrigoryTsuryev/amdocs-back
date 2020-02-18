package com.amdocs.crmdashboardinnovationapp.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.amdocs.crmdashboardinnovationapp.conroller.ISonar;

@Service
public class SonarService implements ISonar {
	
	
	@Override
	public String getUsersIssues(String auth) {
		HttpHeaders headers = new HttpHeaders();
		String base64Credentials =  auth.substring("Basic".length()).trim();
	    byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
	    String credentials = new String(credDecoded, StandardCharsets.UTF_8);
	    final String[] values = credentials.split(":", 2);
		headers.add("Authorization", auth);
		RestTemplate restTemplate =  new RestTemplate();
		HttpEntity<String> request = new HttpEntity<String>(headers);
		String username = values[0];
		String url = "http://illin018.corp.amdocs.com:9000/api/issues/search?assignees="+username+"&statuses=OPEN,REOPENED&severities=BLOCKER,CRITICAL,MAJOR";
		ResponseEntity<String> response ;
		try {
			response = restTemplate.exchange(url , HttpMethod.GET, request, String.class);
		} catch (Exception e){
			return e.getLocalizedMessage();
		}
		return response.getBody();
	}

}
