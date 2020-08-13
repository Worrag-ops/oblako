package storage;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import entities.Bet;
import entities.Bookmaker;
import entities.Bookmaker.Wallet.Operation;
import entities.Discipline;
import entities.MyCurrency;
import entities.Team;
import entities.Tournament;
import h2.ConnectH2;
import h2.H2EntityIdExtractor;

public class Bets {
	private static Bets instance;
	private List<Bet> bets = new ArrayList<Bet>();
	
	private Bets() {};
	
	public void add(Bet bet) { //add with sort by date
		int size = bets.size();
		LocalDate date = bet.getDate();
		if (!bets.isEmpty() && bets.get(size - 1).getDate().isAfter(date)) {
			for (int i = bets.size() - 1; i > 0; i--) {
				if (i > 0 && bets.get(i-1).getDate().isBefore(date)) {
					bets.add(i, bet);	
					return;
				}
				if (bets.get(i).getDate().isEqual(date)) {
					bets.add(i + 1, bet);
					return;
				}
			}
			
			bets.add(0, bet); //if no matches add in begin
			return;
		}
		bets.add(bet);
	}
	
	public static Bets getInstance() {
        if(instance == null){	
            instance = new Bets();
        }
		return instance;
	}
	
	public Bet get(int id) {
		for (Bet b : bets) {
			if (b.getId() == id) return b;
		}
		return null;
	}
	
	public List<Bet> getAll(){
		return bets;
	}
	
	public boolean remove(Bet b, boolean updateBook, boolean updateTeam, boolean updateTour) { 
		if(bets.remove(b)) {
			//recalculate bookmakers's balance
			if (updateBook) {
				Bookmaker book = b.getBookmaker();
				book.getWallet().setMoney(book.getWallet().getMoney().subtract(b.getProfit()));
				book.update();
				updateTotals(b.getBookmaker());
			}

			if (!b.isTotal()) {
				//recalculate team's profit
				if(updateTeam) {
					Team team = b.getTeamFor();
					team.setProfit(team.getProfit().subtract(b.getProfit())); 
					team.update();
				}
				//recalculate tournamennt's profit
				if(updateTour) {
					Tournament tour = b.getTournament();
					tour.setProfit(tour.getProfit().subtract(b.getProfit()));
					tour.update();
				}
			}
			
			return true;
		}
		return false;
	}
	
	public boolean remove(Bet b) {
		return bets.remove(b);
	}
	
	public boolean removeAll(List<Bet> list, boolean updateBook, boolean updateTeam, boolean updateTour) {
		for (Bet b : list) {
			if(!this.remove(b, updateBook, updateTeam, updateTour)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean removeAll(List<Bet> list) {
		for (Bet b : list) {
			if(!this.remove(b)) {
				return false;
			}
		}
		return true;
	}	
	
	public void restore() {
		Connection conn = ConnectH2.getConnection();
		Statement st;
		ResultSet result;
		try {
			st = conn.createStatement();
			result = st.executeQuery("SELECT * FROM bets");
			while (result.next()) {
				int id = result.getInt("bet_id");
				LocalDate date = result.getDate("date").toLocalDate();
				//getting discipline
				String discName = H2EntityIdExtractor.getNameForEntById("discipline", result.getInt("discipline_id"));
				Discipline discipline = Disciplines.getInstance().get(discName);
				//getting team1
				String team1Name = H2EntityIdExtractor.getNameForEntById("team", result.getInt("team1_id"));
				Team team1 = Teams.getInstance().get(team1Name, discipline);
				//getting team2
				String team2Name = H2EntityIdExtractor.getNameForEntById("team", result.getInt("team2_id"));
				Team team2 = Teams.getInstance().get(team2Name, discipline);
				//getting tournament
				String tourName = H2EntityIdExtractor.getNameForEntById("tournament", result.getInt("tournament_id"));
				Tournament tournament = Tournaments.getInstance().get(tourName);
				String betEvent = result.getString("betEvent");
				Float coef = result.getFloat("coef");
				BigDecimal money = result.getBigDecimal("moneyBet");
				String betResult = result.getString("result");
				BigDecimal profit = result.getBigDecimal("profit");
				//getting bookmaker
				String bookName = H2EntityIdExtractor.getNameForEntById("bookmaker", result.getInt("book_id"));
				Bookmaker book = Bookmakers.getInstance().get(bookName);
				boolean isTotal = result.getBoolean("isTotal");
				boolean isCashOut = result.getBoolean("isCashOut");
				
				Bet bet = new Bet(id, date, discipline, team1, team2, tournament, betEvent, coef, money, betResult, isTotal, isCashOut, book, profit);
				this.add(bet);
			}
			
			for (Bookmaker b : Bookmakers.getInstance().getAll()) {
				updateTotals(b);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	public void updateTotals(Bookmaker book) {
		betsTotalsToZero(book);
		List<Bet> betsList = getBetsByBookmaker(book);
		int index = 0;
		LocalDate betDate;
		LocalDate nextDate;
		Set<LocalDate> deposited = new HashSet<LocalDate>();
		Set<LocalDate> withdrawn = new HashSet<LocalDate>();
		Set<LocalDate> checkedDates = new HashSet<LocalDate>();
		for(Operation op : book.getWallet().getDeposit()) {
			deposited.add(op.getDate());
		}
		for(Operation op : book.getWallet().getWithdrawn()) {
			withdrawn.add(op.getDate());
		}
		
		for (Bet b : betsList) {
			betDate = b.getDate();
			if (index < betsList.size()) 
				nextDate = betsList.get(index).getDate();
			else
				nextDate = null;
			
			if (!checkedDates.contains(betDate)) {
				for (LocalDate d : deposited) {
					if (d.isEqual(betDate) || d.isBefore(betDate)) {
						for (Operation op : book.getWallet().getDeposit()) {
							if (op.getDate().equals(d))
								b.setTotal(b.getTotal().add(op.getMoney()));
						}						
						deposited.remove(d);
					}
				}
				checkedDates.add(betDate);
			}
			
			if (index != 0) {
				b.setTotal(b.getTotal().add(betsList.get(index-1).getTotal()).add(b.getProfit()).setScale(3,BigDecimal.ROUND_HALF_UP));
			} else {
				b.setTotal(b.getTotal().add(b.getProfit()));
			}
			
			index++;
		}
	}
	
	private List<Bet> getBetsByBookmaker(Bookmaker book){
		List<Bet> blist = new ArrayList<Bet>();
		for (Bet b : bets) {
			if (b.getBookmaker().getName().equals(book.getName()))
				blist.add(b);
		}
		return blist;
	}
	
	private void betsTotalsToZero(Bookmaker book) {
		for (Bet b : Bets.getInstance().getAll()) {
			if (b.getBookmaker().getName().equals(book.getName())) b.setTotal(BigDecimal.ZERO);
		}
	}
	
}
