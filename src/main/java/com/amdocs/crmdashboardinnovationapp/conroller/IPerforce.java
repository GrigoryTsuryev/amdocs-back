package com.amdocs.crmdashboardinnovationapp.conroller;

import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.amdocs.crmdashboardinnovationapp.perforceDTO.MissingMerge;
import com.perforce.p4java.exception.P4JavaException;

public interface IPerforce {
	
	Set<MissingMerge> getMissingMerges(String auth) throws URISyntaxException, P4JavaException, InterruptedException, ExecutionException;
}
