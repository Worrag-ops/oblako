package storage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entities.Discipline;
import h2.ConnectH2;

public class Disciplines {
	private List<Discipline> discList = new ArrayList<Discipline>();
	private static Disciplines instance;
	
	private Disciplines() {}	
	
	public void add(Discipline d) {
		discList.add(d);
	}
	
	public Discipline get(String name) {
		for (Discipline d : discList) {
			if (d.getName().equals(name)) return d;
		}		
		return null;
	}

	public List<Discipline> getAllSport(){
		List<Discipline> list = new ArrayList<Discipline>();
		for (Discipline d : discList) {
			if (d.getDiscType().equals("Спорт")) list.add(d);
		}
		
		return list;
	}
	
	public List<Discipline> getAllCybersport(){
		List<Discipline> list = new ArrayList<Discipline>();
		for (Discipline d : discList) {
			if (d.getDiscType().equals("Киберспорт")) list.add(d);
		}
		
		return list;
	}
	
	public List<Discipline> getAll(){
		return discList;
	}
	
	public boolean remove(Discipline d) {
		return discList.remove(d);
	}
	
	public void printAll() {
		int i = 0;
		for (Discipline d : discList) {
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
			result = st.executeQuery("SELECT * FROM disciplines");
			while (result.next()) {
				Discipline d = new Discipline(result.getInt("id"),result.getString("name"),result.getString("discType"));
				this.add(d);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Disciplines getInstance() {
        if(instance == null){	
            instance = new Disciplines();
        }
		return instance;
	}

	
}
