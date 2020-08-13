package storage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entities.MyCurrency;
import h2.ConnectH2;

public class Currencies {
	
	private static Currencies instance;
	private List<MyCurrency> currList = new ArrayList<MyCurrency>();
	private int defaultCurrIndex = 0;
	
	private Currencies() {}

	public void add(MyCurrency c) {
		currList.add(c);
	}
	
	public MyCurrency get(String name) {
		for (MyCurrency c : currList){
			if (c.getName().equals(name)) return c;
		}
		return null;
	}
	
	public List<MyCurrency> getAll(){
		return currList;
	}
	
	public void setNewDefault(MyCurrency curr) {
		currList.get(defaultCurrIndex).setMain(false);
		defaultCurrIndex = currList.indexOf(curr);
	}
	
	public static Currencies getInstance() {
        if(instance == null){	
            instance = new Currencies();
        }
		return instance;
	}
	
	public boolean remove(MyCurrency curr) {
		return currList.remove(curr);
	}
	
	public void restore() {
		Connection conn = ConnectH2.getConnection();
		Statement st;
		ResultSet result;
		try {
			st = conn.createStatement();
			result = st.executeQuery("SELECT * FROM currency");
			while (result.next()) {
				MyCurrency curr = new MyCurrency(result.getInt("id"), result.getString("name"), result.getBoolean("isMain"), result.getDouble("equalsToMain"));
				this.add(curr);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
