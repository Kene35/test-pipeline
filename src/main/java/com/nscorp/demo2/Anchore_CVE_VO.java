package com.nscorp.demo2;

public class Anchore_CVE_VO implements Comparable {

	private String Image_Id;
	private String Repo_Tag;
	private String Trigger_Id;
	private String Gate;
	private String Trigger;
	private String Check_Output;
	private String Gate_Action;
	private Boolean Whitelisted;
	private String Policy_Id;
	private int severity;

	public String getImage_Id() {
		return Image_Id;
	}

	public void setImage_Id(String image_Id) {
		Image_Id = image_Id;
	}

	public String getRepo_Tag() {
		return Repo_Tag;
	}

	public void setRepo_Tag(String repo_Tag) {
		Repo_Tag = repo_Tag;
	}

	public String getTrigger_Id() {
		return Trigger_Id;
	}

	public void setTrigger_Id(String trigger_Id) {
		Trigger_Id = trigger_Id;
	}

	public String getGate() {
		return Gate;
	}

	public void setGate(String gate) {
		Gate = gate;
	}

	public String getTrigger() {
		return Trigger;
	}

	public void setTrigger(String trigger) {
		Trigger = trigger;
	}

	public String getGate_Action() {
		return Gate_Action;
	}

	public void setGate_Action(String gate_Action) {
		Gate_Action = gate_Action;
	}

	public Boolean getWhitelisted() {
		return Whitelisted;
	}

	public void setWhitelisted(Boolean whitelisted) {
		Whitelisted = whitelisted;
	}

	public String getPolicy_Id() {
		return Policy_Id;
	}

	public void setPolicy_Id(String policy_Id) {
		Policy_Id = policy_Id;
	}
	
	public String getCheck_Output() {
		return Check_Output;
	}

	public void setCheck_Output(String check_Output) {
		Check_Output = check_Output;
	}

	public int getSeverity() {
		return severity;
	}

	public void setSeverity(int severity) {
		this.severity = severity;
	}


	@Override
    public int compareTo(Object vo) {
		return this.severity - ((Anchore_CVE_VO)vo).severity;
    }


}
