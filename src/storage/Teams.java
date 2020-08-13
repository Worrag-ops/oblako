package storage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entities.Discipline;
import entities.Team;
import h2.ConnectH2;
import h2.H2EntityIdExtractor;

public class Teams {
	private List<Team> teamList = new ArrayList<Team>();
	private static Teams instance;
	
	private Teams() {}	
	
	public void add(Team d) {
		teamList.add(d);
	}
	
	public Team get(String name, Discipline discipline) {
		for (Team t : teamList) {
			if (t.getName().equals(name) && t.getDiscipline().equals(discipline)) return t;
		}
		return null;
	}
	
	public List<Team> getAll(){
		return teamList;
	}
	
	public boolean remove(Team t) {
		return teamList.remove(t);
	}
	
	public boolean removeAll(List<Team> teams) {
		return teamList.removeAll(teams);
	}
	
	public void printAll() {
		int i = 0;
		for (Team d : teamList) {
			i++;
			System.out.println(String.valueOf(i)+": " + d.toString());
		}
	}
	
	public void restore() {
		Connection conn = ConnectH2.getConnection();
		Statement st;
		ResultSet result;
		try {
			st = conn.createStatement();
			result = st.executeQuery("SELECT * FROM teams");
			while (result.next()) {
				String discName = H2EntityIdExtractor.getNameForEntById("discipline", result.getInt("discipline_id"));
				Discipline d = Disciplines.getInstance().get(discName);
				Team t = new Team(result.getInt("id"), result.getString("name"),d, result.getBigDecimal("profit"));
				this.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Teams getInstance() {
        if(instance == null){	
            instance = new Teams();
        }
		return instance;
	}
}
