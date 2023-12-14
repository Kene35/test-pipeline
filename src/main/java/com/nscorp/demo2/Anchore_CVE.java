package com.nscorp.demo2;


import com.fasterxml.jackson.databind.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;


public class Anchore_CVE {

  private String imgID;
  private String name;

  public Anchore_CVE(String imgID, String name)
  {
	  this.imgID = imgID;
	  this.name = name;
  }

  public String parse () {
		System.out.println("parser");
		StringBuffer results = new StringBuffer();
	  	URL url;
		StringBuffer urladdr = new StringBuffer("http://anchore-engine-api.apps.okd01.nscorp.com/v1/images/by_id/")
			.append(this.imgID).append("/check?tag=").append(this.name);
		System.out.println(urladdr);
		String htpw = "Basic YWRtaW46RUBTRDN2b3Bz";
		if (this.imgID.equals("test")){
			System.out.println("test only run");
			String resourceName = "anchore.json";
	 		ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource(resourceName).getFile());
			try {
				byte[] b = Files.readAllBytes(file.toPath());
//				results = "{\"object\":" + new String(b) + "}";
				results.append(new String(b));
			} catch (IOException e) {
				e.printStackTrace();
				return "bad test data";
			}
		}
		else {
//			results = new RestTemplateBuilder().basicAuthentication("admin", "xxxxxxxxx").build().getForObject(url.toString(), String.class);
			HttpURLConnection con;
			try {
				
				url = new URL(urladdr.toString());
				con = (HttpURLConnection) url.openConnection();
				con.addRequestProperty("Authorization", htpw);
				
				
//				HttpInputStream r = con.getContent();
//				System.out.println(r.getClass());
				
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					results.append(inputLine);
				}
				in.close();
				con.disconnect();
			
//			HttpHeaders hdr = new HttpHeaders();
//			hdr.add("Authorization", "Basic YWRtaW46RUBTRDN2b3Bz");
//			HttpEntity request = new HttpEntity(hdr);
//			ResponseEntity<String> response = new RestTemplate().exchange(url, HttpMethod.GET, request, String.class);

//			String json = response.getBody();
			}
			catch (Exception e) {
				e.printStackTrace();
				return ("500: cannot read results");
			}
		}
//		System.out.println(results);
		System.out.println("start parsing");
		ObjectMapper om = new ObjectMapper();
		JsonNode node = null;
		try {
			node = om.readTree(results.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			return "haha";
		}
		List<Anchore_CVE_VO> cves = new ArrayList<Anchore_CVE_VO>();
		int[] vul = {0,0,0,0,0,0,0};
		Iterator<JsonNode> it = node.iterator();
		while (it.hasNext()) {
			JsonNode n = it.next();
			Iterator<JsonNode> dit1 = n.iterator();
			JsonNode dnode1 = dit1.next();  // bypass 
			Iterator<JsonNode> dit2 = dnode1.iterator();
			JsonNode n1 = dit2.next();
//			JsonNode n1 = n.at("/sha256:aaae47b6ecb4347ae909b71a459d10f26621cc5cd580aec2745d42e76bbd0d9b/nsos-pr1-nex.atldc.nscorp.com:8082/mobile/dwtest:latest");
			Iterator<JsonNode> itd = n1.iterator();
			while (itd.hasNext()) {
				JsonNode n2 = itd.next();
				JsonNode nd = n2.at("/detail/result/result/" + imgID + "/result/rows");
				Iterator<JsonNode> itrows = nd.iterator();
				while (itrows.hasNext()) {
					JsonNode nr = itrows.next();
					Anchore_CVE_VO vo = new Anchore_CVE_VO();
					vo.setImage_Id(nr.get(0).asText());
					vo.setRepo_Tag(nr.get(1).asText());
					vo.setTrigger_Id(nr.get(2).asText());
					vo.setGate(nr.get(3).asText());
					vo.setTrigger(nr.get(4).asText());
					vo.setCheck_Output(nr.get(5).asText());
					vo.setGate_Action(nr.get(6).asText());
					vo.setWhitelisted(nr.get(7).asBoolean());
					vo.setPolicy_Id(nr.get(8).asText());
					if (vo.getGate().equals("vulnerabilities")) {
						if (vo.getCheck_Output().startsWith("MEDIUM")){
							vo.setSeverity(3);
							vul[2]++;
						}
						else if (vo.getCheck_Output().startsWith("LOW")){
							vo.setSeverity(5);
							vul[1]++;
						}
						else if (vo.getCheck_Output().startsWith("UNKNOWN")){
							vo.setSeverity(4);
							vul[5]++;
						}
						else if (vo.getCheck_Output().startsWith("CRITICAL")){
							vo.setSeverity(1);
							vul[4]++;
						}
						else if (vo.getCheck_Output().startsWith("HIGH")){
							vo.setSeverity(2);
							vul[3]++;
						}
						else {
							vo.setSeverity(0);
							vul[6]++;
							
						}
						cves.add(vo);
					}
				}
			}
		}

//		String top = String.format("Negligible: %d<br/> Low: %d<br/> Medium: %d<br/> High: %d<br/> Critical: %d<br/> Unknown: %d<br/>No Match: %d<br/><br/>", vul[0],vul[1],vul[2],vul[3],vul[4],vul[5],vul[6]);
		Collections.sort(cves);
		StringBuffer sb = new StringBuffer("");
		int sev = -1;
		for (Anchore_CVE_VO cve: cves) {
		  if (cve.getSeverity() != sev) {
			  sev = cve.getSeverity();
		    switch (cve.getSeverity()) {
			  case 1:
				  sb.append("<h2>Critical --> " + vul[4] + "</h2><br/>");
				  break;
			  case 2:
				  sb.append("<h2>High --> " + vul[3] + "</h2><br/>");
				  break;
			  case 3:
				  sb.append("<h2>Medium --> " + vul[2] + "</h2><br/>");
				  break;
			  case 4:
				  sb.append("<h2>Unknown --> " + vul[5] + "</h2><br/>");
				  break;
			  case 5:
				  sb.append("<h2>Low --> " + vul[1] + "</h2><br/>");
				  break;
			  case 6:
				  sb.append("<h2>Negligible --> " + vul[0] + "</h2><br/>");
				  break;
			  default:
				  sb.append("<h2>Error --> " + vul[6] + "</h2><br/>");
		    }
		  }

		  sb.append("Name: ").append(cve.getTrigger_Id()).append("<br/>");
		  sb.append("Desc: ").append(cve.getCheck_Output()).append("<br/><br/>");
	  }
	  return sb.toString();
	  
  }
  
  public static void main(String[] args) {
	  Anchore_CVE cve = new Anchore_CVE("87567dc89859e2c8602bc7499cc3a54414fb20f60465cc586d776bdc199e6850", "nsos-pr1-nex.atldc.nscorp.com:8082/mobile/dwtest:latest");
//	  Anchore_CVE cve = new Anchore_CVE("test", "dw-start");
	  System.out.println(cve.parse());
  }
}
