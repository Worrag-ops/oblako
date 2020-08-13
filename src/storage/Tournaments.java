package storage;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entities.Discipline;
import entities.Tournament;
import h2.ConnectH2;
import h2.H2EntityIdExtractor;

public class Tournaments {
	private List<Tournament> tourList = new ArrayList<Tournament>();
	private static Tournaments instance;
	
	private Tournaments() {}
	
	public void add(Tournament t) {
		tourList.add(t);
	}
	
	public Tournament get(String name) {
		for (Tournament tour : tourList) {
			if (tour.getName().equals(name)) return tour;
		}
		return null;
	}
	
	public List<Tournament> getAll(){
		return tourList;
	}
	
	public boolean remove(Tournament t) {
		return tourList.remove(t);
	}
	
	public void restore() {
		Connection conn = ConnectH2.getConnection();
		Statement st;
		ResultSet result;
		try {
			st = conn.createStatement();
			result = st.executeQuery("SELECT * FROM tournaments");
			while (result.next()) {
				List <Discipline> disciplines = new ArrayList<Discipline>();
				int id = result.getInt("id");
				String name = result.getString("name");
				BigDecimal profit = result.getBigDecimal("profit");
				PreparedStatement st2 = conn.prepareStatement("SELECT discipline_id FROM tournament_discipline WHERE tournament_id = ?");
				st2.setInt(1, id);
				ResultSet result2 = st2.executeQuery();
				while (result2.next()) {
					String discName = H2EntityIdExtractor.getNameForEntById("discipline", result2.getInt(1));
					disciplines.add(Disciplines.getInstance().get(discName));
				}
				Tournament t = new Tournament(id, name, disciplines, profit);
				this.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}	
	
	public static Tournaments getInstance() {
        if(instance == null){	
            instance = new Tournaments();
        }
		return instance;
	}
}
