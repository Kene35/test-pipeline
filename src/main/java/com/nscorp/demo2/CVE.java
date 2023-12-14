package com.nscorp.demo2;

import org.springframework.boot.web.client.RestTemplateBuilder;

import com.fasterxml.jackson.databind.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class CVE {

  private String layer;

  public CVE(String layer)
  {
	  this.layer = layer;
  }

  public String parse () {
		String features = null;
		
//	  StringBuffer url = new StringBuffer("http://clair:6060/v1/layers/")
	  StringBuffer url = new StringBuffer("http://clair-nsjenkins.apps.ocptest01.nscorp.com/v1/layers/")
			.append(this.layer).append("?features&vulnerabilities&fixes");
		if (this.layer.equals("test")){
			String resourceName = "cveData.json";
	 		ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource(resourceName).getFile());
			try {
				byte[] b = Files.readAllBytes(file.toPath());
				features = new String(b);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "bad test data";
			}
		}
		else {
			features = new RestTemplateBuilder().build().getForObject(url.toString(), String.class);
		}
	  ObjectMapper om = new ObjectMapper();
	  JsonNode node = null;
	  try {
		node = om.readTree(features);
      } catch (Exception e) {
		e.printStackTrace();
	    return "haha";
	  }
	  Iterator it = node.path("Layer").path("Features").iterator();
	  int[] vul = {0,0,0,0,0,0};
	  List<CVE_VO> cves = new ArrayList<CVE_VO>();
	  while (it.hasNext()) {
		  JsonNode n = (JsonNode) it.next();
		  String name = n.path("Name").asText();
		  JsonNode vulnerabilties = n.path("Vulnerabilities");
		  if (! vulnerabilties.isMissingNode()) {
			  Iterator vit = vulnerabilties.iterator();
			  while  (vit.hasNext()) {
				  CVE_VO vo = new CVE_VO();
				  vo.setName(name);
				  JsonNode vn = (JsonNode) vit.next();
				  vo.setVul_name(vn.path("Name").asText());
				  vo.setDesc(vn.path("Description").asText());
				  String severity = vn.path("Severity").asText();
				  
				  switch (severity){
				  	case "Negligible":
				  		vul[0]++;
				  		vo.setSeverity(6);
				  		break;
				  	case "Low":
				  		vul[1]++;
				  		vo.setSeverity(5);
				  		break;
				  	case "Medium":
				  		vul[2]++;
				  		vo.setSeverity(3);
				  		break;
				  	case "High":
				  		vul[3]++;
				  		vo.setSeverity(2);
				  		break;
				  	case "Critical":
				  		vul[4]++;
				  		vo.setSeverity(1);
				  		break;
				  	default:
				  		vul[5]++;
				  		vo.setSeverity(4);
				  }
				  cves.add(vo);
			  }
	      }
	  }
	  String top = String.format("Negligible: %d<br/> Low: %d<br/> Medium: %d<br/> High: %d<br/> Critical: %d<br/> Unknown: %d<br/><br/>", vul[0],vul[1],vul[2],vul[3],vul[4],vul[5]);
	  Collections.sort(cves);
	  StringBuffer sb = new StringBuffer("");
	  int sev = 0;
	  for (CVE_VO cve: cves) {
		  if (cve.getSeverity() != sev) {
			  sev = cve.getSeverity();
			  switch (cve.getSeverity()) {
				  case 1:
					  sb.append("<h2>Critical</h2><br/>");
					  break;
				  case 2:
					  sb.append("<h2>High</h2><br/>");
					  break;
				  case 3:
					  sb.append("<h2>Medium</h2><br/>");
					  break;
				  case 4:
					  sb.append("<h2>Unknown</h2><br/>");
					  break;
				  case 5:
					  sb.append("<h2>Low</h2><br/>");
					  break;
				  case 6:
					  sb.append("<h2>Negligible</h2><br/>");
					  break;
				  default:
					  sb.append("<h2>Error</h2><br/>");
			  }

		  }
		  sb.append("Name: ").append(cve.getName()).append("<br/>");
		  sb.append("Vuln: ").append(cve.getVul_name()).append("<br/>");
		  sb.append("Desc: ").append(cve.getDesc()).append("<br/><br/>");
	  }
	  return top + sb.toString();
	  
  }
}
