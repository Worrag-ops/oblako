package entities.view;

import entities.Team;

public class TeamView {
	private String name;
	private String disciplineName;
	private double profit;
	
	public TeamView(Team t) {
		this.name = t.getName();
		this.disciplineName = t.getDiscipline().getName();
		this.profit = t.getProfit().doubleValue();
	}
	
	public String getName() { return name; }
	public void setName(String name) {	this.name = name; }
	
	public String getDisciplineName() {	return disciplineName; }
	public void setDisciplineName(String disciplineName) { this.disciplineName = disciplineName; }
	
	public double getProfit() { return profit; }
	public void setProfit(double profit) { this.profit = profit; }
}
