package entities.view;

import java.time.LocalDate;

import entities.Bet;

public class BetView {

	private int id;
	private LocalDate date;
	private String discipline;
	private String teamFor;
	private String teamOpp;
	private String tourn;
	private String betEvent;
	private float coefficient;
	private double moneyBet;
	private String result;
	private double profit;
	private double total;
	private String isTotal;
	private String isCashOut;
	private String book;
	
	public BetView(Bet bet) {
		id = bet.getId();
		date = bet.getDate();
		discipline = bet.getDiscipline().getName();
		teamFor = bet.getTeamFor().getName();
		teamOpp = bet.getTeamOpp().getName();
		tourn = bet.getTournament().getName();
		betEvent = bet.getBetEvent();
		coefficient = bet.getCoefficient();
		moneyBet = bet.getMoneyBet().doubleValue();
		result = bet.getResult();
		profit = bet.getProfit().doubleValue();
		total = bet.getTotal().doubleValue();
		setTotal(bet.isTotal());
		setCashOut(bet.isCashOut());
		book = bet.getBookmaker().getName();
	}
	
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public String getDiscipline() {
		return discipline;
	}
	public void setDiscipline(String discipline) {
		this.discipline = discipline;
	}
	public String getTeamFor() {
		return teamFor;
	}
	public void setTeamFor(String teamFor) {
		this.teamFor = teamFor;
	}
	public String getTeamOpp() {
		return teamOpp;
	}
	public void setTeamOpp(String teamOpp) {
		this.teamOpp = teamOpp;
	}
	public String getTourn() {
		return tourn;
	}
	public void setTourn(String tourn) {
		this.tourn = tourn;
	}
	public String getBetEvent() {
		return betEvent;
	}
	public void setBetEvent(String betEvent) {
		this.betEvent = betEvent;
	}
	public float getCoefficient() {
		return coefficient;
	}
	public void setCoefficient(float coefficient) {
		this.coefficient = coefficient;
	}
	public double getMoneyBet() {
		return moneyBet;
	}
	public void setMoneyBet(double moneyBet) {
		this.moneyBet = moneyBet;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public double getProfit() {
		return profit;
	}
	public void setProfit(double profit) {
		this.profit = profit;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public String getBook() {
		return book;
	}
	public void setBook(String book) {
		this.book = book;
	}

	public String getIsTotal() {
		return isTotal;
	}

	public void setTotal(boolean isTotal) {
		if (isTotal)
			this.isTotal = "Да";
		else
			this.isTotal = "Нет";
	}

	public String getIsCashOut() {
		return isCashOut;
	}

	public void setCashOut(boolean isCashOut) {
		if (isCashOut)
			this.isCashOut = "Да";
		else
			this.isCashOut = "Нет";
	}
}
