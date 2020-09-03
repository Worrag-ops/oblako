package entities;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import h2.ConnectH2;
import h2.H2EntityIdExtractor;

public class Bet {
	private int id = 0;
	private LocalDate date;
	private Discipline discipline;
	private Team teamFor;
	private Team teamOpp;
	private Tournament tourn;
	private String betEvent;
	private float coefficient;
	private BigDecimal moneyBet;
	private String result;
	private BigDecimal profit;
	private BigDecimal total;
	private boolean isTotal;
	private boolean isCashOut;
	private Bookmaker book;

	public Bet(int id, LocalDate date, Discipline discipline, Team teamFor, Team teamOpp, Tournament tourn, String betEvent, float coefficient, BigDecimal moneyBet, String result, boolean isTotal, boolean isCashOut, Bookmaker book, BigDecimal profit) {
		this(date, discipline, teamFor, teamOpp, tourn, betEvent, coefficient, moneyBet, result, isTotal, isCashOut, book, profit);
		this.id = id;
	}
	
	public Bet(LocalDate date, Discipline discipline, Team teamFor, Team teamOpp, Tournament tourn, String betEvent, float coefficient, BigDecimal moneyBet, String result, boolean isTotal, boolean isCashOut, Bookmaker book, BigDecimal profit) {
		this.date = date;
		this.discipline = discipline;
		this.teamFor = teamFor;
		this.teamOpp = teamOpp;
		this.tourn = tourn;
		this.book = book;
		this.betEvent = betEvent;
		this.coefficient = coefficient;
		this.moneyBet = moneyBet;
		this.isTotal = isTotal;
		this.result = result;
		this.profit = profit;
	}
	
	public LocalDate getDate() {
		return date;
	}
	
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	public Discipline getDiscipline() {
		return discipline;
	}
	
	public void setDiscipline(Discipline discipline) {
		this.discipline = discipline;
	}
	
	public Team getTeamFor() {
		return teamFor;
	}
	
	public void setTeamFor(Team teamFor) {
		this.teamFor = teamFor;
	}
	
	public Team getTeamOpp() {
		return teamOpp;
	}
	
	public void setTeamOpp(Team teamOpp) {
		this.teamOpp = teamOpp;
	}
	
	public Tournament getTournament() {
		return tourn;
	}
	
	public void setTournament(Tournament tourn) {
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
	
	public String getResult() {
		return result;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public BigDecimal getProfit() {
		return profit;
	}
	
	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}
	
	public BigDecimal getTotal() {
		return total;
	}
	
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	
	public Bookmaker getBookmaker() {
		return book;
	}
	
	public void setBookmaker(Bookmaker book) {
		this.book = book;
	}

	public BigDecimal getMoneyBet() {
		return moneyBet;
	}

	public void setMoneyBet(BigDecimal moneyBet) {
		this.moneyBet = moneyBet;
	}
	
	public boolean isTotal() {
		return isTotal;
	}
	
	public void setIsTotal(boolean total) {
		this.isTotal = total;
	}
	
	public boolean save() {
		try {
			int teamForId = this.getTeamFor().getId();
			int teamOppId = this.getTeamOpp().getId();
			int disc_id = this.getDiscipline().getId();
			int tour_id = this.getTournament().getId();		
			int book_id = this.getBookmaker().getId();
			
			Connection conn = ConnectH2.getConnection();
			PreparedStatement pst = conn.prepareStatement("insert into bets(date, discipline_id, team1_id, team2_id, tournament_id, betEvent, coef, moneyBet, result, profit, book_id, isTotal, isCashOut) values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			pst.setObject(1, this.getDate());
			pst.setInt(2, disc_id);
			pst.setInt(3, teamForId);
			pst.setInt(4, teamOppId);
			pst.setInt(5, tour_id);
			pst.setString(6, this.getBetEvent());
			pst.setFloat( 7,this.getCoefficient());
			pst.setBigDecimal(8,this.getMoneyBet());
			pst.setString(9, this.getResult());
			pst.setBigDecimal(10, this.getProfit());
			pst.setInt(11, book_id);
			pst.setBoolean(12, this.isTotal());
			pst.setBoolean(13, this.isCashOut());
			pst.execute();
			
			setId(H2EntityIdExtractor.getBetId(this));
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean update() {
		try {
			Connection conn = ConnectH2.getConnection();			
			int teamForId = this.getTeamFor().getId();
			int teamOppId = this.getTeamOpp().getId();
			int disc_id = this.getDiscipline().getId();
			int tour_id = this.getTournament().getId();		
			int book_id = this.getBookmaker().getId();
			
			PreparedStatement pst = conn.prepareStatement("UPDATE bets SET date = ?, discipline_id = ?, team1_id = ?, team2_id = ?, tournament_id = ?, betEvent = ?, coef = ?, moneyBet = ?, result = ?, profit = ?, book_id = ?, isTotal = ?, isCashOut = ? WHERE bet_id = ?");
			pst.setObject(1, this.getDate());
			pst.setInt(2, disc_id);
			pst.setInt(3, teamForId);
			pst.setInt(4, teamOppId);
			pst.setInt(5, tour_id);
			pst.setString(6, this.getBetEvent());
			pst.setFloat( 7,this.getCoefficient());
			pst.setBigDecimal(8,this.getMoneyBet());
			pst.setString(9, this.getResult());
			pst.setBigDecimal(10, this.getProfit());
			pst.setInt(11, book_id);
			pst.setBoolean(12, this.isTotal());
			pst.setBoolean(13, this.isCashOut());
			pst.setInt(14, this.getId());
			pst.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
		
	public boolean delete() {
		try {
			Connection conn = ConnectH2.getConnection();			
			PreparedStatement pst = conn.prepareStatement("DELETE FROM bets WHERE bet_id = ?");
			pst.setInt(1, this.getId());
			pst.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public String toString() {
		return (date + " " + discipline + " " + teamFor + " " + teamOpp + " " + tourn + " " + betEvent + " " + coefficient + " " + moneyBet + " " + result + " " + profit + " "  + isTotal + " " + isCashOut + " " + total + " " + book);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isCashOut() {
		return isCashOut;
	}

	public void setCashOut(boolean isCashOut) {
		this.isCashOut = isCashOut;
	}
	
}
