package entities.view;

import java.util.List;

import entities.Discipline;
import entities.Tournament;

public class TournamentView {
	private String name;
	private String disciplines = "";
	private double profit;
	
	public TournamentView(Tournament t) {
		this.name = t.getName();
		setDisciplines(t.getDisciplines());
		this.profit = t.getProfit().doubleValue();
	}
	
	public String getName() { return name; }
	public void setName(String name) {	this.name = name; }
	
	public String getDisciplines() {	return disciplines; }
	public void setDisciplines(List<Discipline> discs) { 
		for (Discipline disc : discs) {
			disciplines = disciplines + disc.getName() + "  ";
		}
	}
	
	public double getProfit() { return profit; }
	public void setProfit(double profit) { this.profit = profit; }
}
