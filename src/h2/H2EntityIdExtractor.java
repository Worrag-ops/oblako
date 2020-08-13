package h2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import entities.Bet;

public class H2EntityIdExtractor {

	public static int getIdForEntByName(String entityType, String name) {
		int id = 0;
		entityType.toLowerCase();
		switch(entityType) {
			case "discipline":
				id = getIdByMonotonousSQL("SELECT id FROM disciplines WHERE name = ?", name);
				break;
			case "bookmaker":
				id = getIdByMonotonousSQL("SELECT id FROM bookmakers WHERE name = ?", name);
				break;
			case "tournament":
				id = getIdByMonotonousSQL("SELECT id FROM tournaments WHERE name = ?", name);
				break;
			case "currency":
				id = getIdByMonotonousSQL("SELECT id FROM currency WHERE name = ?", name);
				break;
		}
		return id;
	}
	
	public static String getNameForEntById(String entityType, int id) {
		String name = "";
		entityType.toLowerCase();
		switch(entityType) {
			case "discipline":
				name = getNameByMonotonousSQL("SELECT name FROM disciplines WHERE id = ?", id);
				break;
			case "bookmaker":
				name = getNameByMonotonousSQL("SELECT name FROM bookmakers WHERE id = ?", id);
				break;
			case "tournament":
				name = getNameByMonotonousSQL("SELECT name FROM tournaments WHERE id = ?", id);
				break;
			case "team":
				name = getNameByMonotonousSQL("SELECT name FROM teams WHERE id = ?", id);
				break;
			case "currency":
				name = getNameByMonotonousSQL("SELECT name FROM currency WHERE id = ?", id);
				break;
		}
		return name;
	}
	
	public static int getTeamId(String name, String disciplineName) {
		int id = 0;
		int disc_id = getIdForEntByName("discipline", disciplineName);
		try {
			PreparedStatement st = ConnectH2.getConnection().prepareStatement("SELECT id FROM teams WHERE name = ? AND discipline_id = ?");
			ResultSet rs;
			st.setString(1, name);
			st.setInt(2, disc_id);
			rs = st.executeQuery();
			rs.next();
			id = rs.getInt("id");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQL ERROR");
		}
		return id;
	}
	
	public static int getBetId(Bet b) {
		int id = 0;	
		try {
			PreparedStatement pst = ConnectH2.getConnection().prepareStatement("SELECT bet_id FROM bets WHERE date = ? AND discipline_id = ? AND team1_id = ? AND team2_id = ? AND tournament_id = ? AND betEvent = ? AND coef = ? AND moneyBet = ? AND result = ? AND profit = ? AND book_id = ? AND isTotal = ?");
			ResultSet rs;
			pst.setObject(1, b.getDate());
			pst.setInt(2, b.getDiscipline().getId());
			pst.setInt(3, b.getTeamFor().getId());
			pst.setInt(4, b.getTeamOpp().getId());			
			pst.setInt(5, b.getTournament().getId());
			pst.setString(6, b.getBetEvent());
			pst.setFloat(7, b.getCoefficient());		
			pst.setBigDecimal(8, b.getMoneyBet());	
			pst.setString(9, b.getResult());
			pst.setBigDecimal(10, b.getProfit());
			pst.setInt(11, b.getBookmaker().getId());		
			pst.setBoolean(12, b.isTotal());
			
			rs = pst.executeQuery();
			rs.next();
			id = rs.getInt("bet_id");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQL ERROR");
		}
		return id;
	}
	
	private static int getIdByMonotonousSQL(String sql, String name){
		int id = 0;
		try {
			PreparedStatement st = ConnectH2.getConnection().prepareStatement(sql);
			ResultSet rs;
			st.setString(1, name);
			rs = st.executeQuery();
			rs.next();
			id = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQL ERROR");
		}
		return id;
	}
	
	private static String getNameByMonotonousSQL(String sql, int id){
		String name = "";
		try {
			PreparedStatement st = ConnectH2.getConnection().prepareStatement(sql);
			ResultSet rs;
			st.setInt(1, id);
			rs = st.executeQuery();
			rs.next();
			name = rs.getString("name");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQL ERROR");
		}
		return name;
	}
	
}
