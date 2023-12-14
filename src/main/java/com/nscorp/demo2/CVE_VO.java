package com.nscorp.demo2;

public class CVE_VO implements Comparable {

	private String name;
	private String vul_name;
	private String desc;
	private int severity;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVul_name() {
		return vul_name;
	}
	public void setVul_name(String vul_name) {
		this.vul_name = vul_name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getSeverity() {
		return severity;
	}
	public void setSeverity(int severity) {
		this.severity = severity;
	}
	
    @Override
    public int compareTo(Object vo) {
    	return this.severity - ((CVE_VO)vo).severity;
    }
	
}
