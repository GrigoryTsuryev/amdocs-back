package com.amdocs.crmdashboardinnovationapp.service;

import static com.amdocs.crmdashboardinnovationapp.utils.CRMConstants.CRM_5014;
import static com.amdocs.crmdashboardinnovationapp.utils.CRMConstants.CRM_6000;
import static com.amdocs.crmdashboardinnovationapp.utils.CRMConstants.CRM_6014;
import static com.amdocs.crmdashboardinnovationapp.utils.CRMConstants.CRM_6016;
import static com.amdocs.crmdashboardinnovationapp.utils.CRMConstants.CRM_6017;
import static com.amdocs.crmdashboardinnovationapp.utils.CRMConstants.CRM_6018;
import static com.amdocs.crmdashboardinnovationapp.utils.CRMConstants.IGNORE_5014;
import static com.amdocs.crmdashboardinnovationapp.utils.CRMConstants.IGNORE_6014;
import static com.amdocs.crmdashboardinnovationapp.utils.CRMConstants.IGNORE_6016;
import static com.amdocs.crmdashboardinnovationapp.utils.CRMConstants.IGNORE_6017;
import static com.amdocs.crmdashboardinnovationapp.utils.CRMConstants.IGNORE_6018;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.amdocs.crmdashboardinnovationapp.conroller.IPerforce;
import com.amdocs.crmdashboardinnovationapp.perforceDTO.MissingMerge;
import com.perforce.p4java.core.IChangelist;
import com.perforce.p4java.core.file.FileSpecBuilder;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.exception.AccessException;
import com.perforce.p4java.exception.ConnectionException;
import com.perforce.p4java.exception.P4JavaException;
import com.perforce.p4java.server.IOptionsServer;
import com.perforce.p4java.server.ServerFactory;

@Service
public class PerforceService implements IPerforce {

	private static Map<String, String> crmDepots = new HashMap<>();
	private static IOptionsServer server;
	private static Set<String> ignoreLists = new HashSet<String>();

	
	static {
		crmDepots.put(CRM_6014, CRM_6000);
		crmDepots.put(CRM_6016, CRM_6018);
		crmDepots.put(CRM_6016, CRM_6000);
		crmDepots.put(CRM_6016, CRM_6017);
		crmDepots.put(CRM_6017, CRM_6018);
		crmDepots.put(CRM_6017, CRM_6000);
		crmDepots.put(CRM_6018, CRM_6000);
		crmDepots.put(CRM_6014, CRM_5014);
	
		ignoreLists.add(IGNORE_5014);
		ignoreLists.add(IGNORE_6017);
		ignoreLists.add(IGNORE_6018);
		ignoreLists.add(IGNORE_6016);
		ignoreLists.add(IGNORE_6014);
		
	}
	@Override
	public Set<MissingMerge> getMissingMerges(String auth) throws URISyntaxException, P4JavaException, InterruptedException, ExecutionException {
		
		
		server = ServerFactory.getOptionsServer("p4java://p4comcast:1666", null, null);
		
		String base64Credentials =  auth.substring("Basic".length()).trim();
	    byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
	    String credentials = new String(credDecoded, StandardCharsets.UTF_8);
	    final String[] values = credentials.split(":", 2);
	    String user = values[0];
	    String password = values[1];
		server.connect();
		server.setUserName(user);
		server.login(password);

		Set<MissingMerge> listMMS = getMMs(auth);
		Map<String, Set<MissingMerge>> map = new HashMap<String, Set<MissingMerge>>();
		
		listMMS.stream().forEach(mm -> putToMap(mm, map));
		Set<String> ignoredCLs = getIgnoredCLs();
		
		Set<MissingMerge> filteredMMS = new HashSet<MissingMerge>();
		map.entrySet().forEach(el ->{
			String stream = el.getKey();
			Set<MissingMerge> filteredList = listMMS.stream()
			.filter(mm->mm.getFrom().equals(stream))
			.filter(mm->!ignoredCLs.contains(mm.getCl()))
			.collect(Collectors.toSet());
			filteredMMS.addAll(filteredList);
		});
		
	

		return filteredMMS;	
	}



	private void putToMap(MissingMerge mm, Map<String, Set<MissingMerge>> map) {
		Set<MissingMerge> data = map.getOrDefault(mm.getFrom(), new HashSet<>());
		data.add(mm);
		map.put(mm.getFrom(), data);
	}



	private  Set<MissingMerge> getMMs(String user) {
		Set<MissingMerge> missingMerges = new HashSet<MissingMerge>();
		    	crmDepots.entrySet().parallelStream().forEach(depot-> {
					try {
						List<IChangelist> cls = getChangeLists(server, depot.getKey(), depot.getValue());
						Set<MissingMerge> mms = cls.stream().map(change -> new MissingMerge(getCrmDepotName(depot.getKey()), getCrmDepotName(depot.getValue()), String.valueOf(change.getId()), change.getUsername()))
						.filter(mm -> !mm.getUser().equals("mb_ccbld"))
						.filter(mm->   mm.getUser().equals(user))
						.collect(Collectors.toSet());	
						missingMerges.addAll(mms);
					} catch (P4JavaException e) {
						e.printStackTrace();
					} 
				});
			return missingMerges;
	}



	
	 public Set<String> getIgnoredCLs() {     
	        
	        	
	        	List<IFileSpec> files = new ArrayList<IFileSpec>();
	    		ignoreLists.forEach(ignoreList -> getIgnoreList(files, ignoreList));
	    		Set<String> finalCLs = new HashSet<>();
	    		files.stream().forEach(file -> {
	    			try {
	    				InputStream data = file.getContents(null);
	    				Set<String> response = new BufferedReader(new InputStreamReader(data)).lines().collect(Collectors.toSet());
	    				Set<String> cls = response.stream()
	    						.map(x->x.replaceAll("[^\\d.]", ""))
	    						.filter(x->!x.isEmpty())
	    						.collect(Collectors.toSet());
	    				finalCLs.addAll(cls);		
	    			} catch (P4JavaException e) {
	    				// 
	    				e.printStackTrace();
	    			}
	    		});    		
				return finalCLs;
     
	    }


	private String getCrmDepotName(String filePath) {
		return filePath.substring(filePath.lastIndexOf("/COMCAST/") + 9, filePath.indexOf("/cust/"));
	}
	

	private void getIgnoreList(List<IFileSpec> files, String ignoreList) {
		 try {
			List<IFileSpec> file = server.getDepotFiles(
			            FileSpecBuilder.makeFileSpecList(ignoreList), false);
			files.addAll(file);
		} catch (ConnectionException | AccessException e) {
			e.printStackTrace();
		}
	}

	private List<IChangelist> getChangeLists(IOptionsServer server,String from, String to) throws P4JavaException {
		return server.getInterchanges(null, FileSpecBuilder.makeFileSpecList(new String[] { from }),
				FileSpecBuilder.makeFileSpecList(new String[] { to }), null);
	}
	
}
