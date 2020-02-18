package com.amdocs.crmdashboardinnovationapp.conroller;

import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amdocs.crmdashboardinnovationapp.perforceDTO.MissingMerge;
import com.amdocs.crmdashboardinnovationapp.service.PerforceService;
import com.perforce.p4java.exception.P4JavaException;

@RestController
@RequestMapping("/perforce")
public class PerforceController {
	
	@Autowired
	protected PerforceService perforceService;
	
	@GetMapping(value= "/getMissingMerges")
	public Set<MissingMerge> getMissingMerges(@RequestHeader(value="authorization") String auth) throws URISyntaxException, P4JavaException, InterruptedException, ExecutionException {
		return this.perforceService.getMissingMerges(auth);
	}

}
