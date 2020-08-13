package entities;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import h2.ConnectH2;
import h2.H2EntityIdExtractor;
import storage.Currencies;

public class MyCurrency {
	private int id = 0;
	private String name;
	private boolean main = false;
	private double equalsToMain = 1;
	
	public MyCurrency(int id, String name, boolean main, double equals) {
		this(name, main, equals);
		this.setId(id);
	}
	
	public MyCurrency(String name, boolean main, double equals) {
		this.name = name;
		this.main = main;
		this.equalsToMain = equals;
	}
	
	public void setMain(boolean m) {
		if (m == true && main == false) 
			Currencies.getInstance().setNewDefault(this);
		setEqualsToMain(1);
		main = m;
	}
	
	public boolean isMain() {
		return main;
	}

	public double getEqualsToMain() {
		return equalsToMain;
	}

	public void setEqualsToMain(double equalsToMain) {
		this.equalsToMain = equalsToMain;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean save() {
		try {
			if (this.isMain()) {
				Statement st = ConnectH2.getConnection().createStatement();
				st.execute("UPDATE currency SET isMain = false WHERE isMain = true");
			}
			PreparedStatement pst = ConnectH2.getConnection().prepareStatement("insert into currency(name, isMain, equalsToMain) values(?,?,?)");
			pst.setString(1, this.getName());
			pst.setBoolean(2, this.isMain());
			pst.setDouble(3, this.getEqualsToMain());
			pst.execute();
			
			this.setId(H2EntityIdExtractor.getIdForEntByName("currency", this.getName()));
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	public boolean update() {
		try {
			PreparedStatement pst = ConnectH2.getConnection().prepareStatement("UPDATE currency SET name = ?, isMain = ?, equalsToMain = ? WHERE id = ?");
			pst.setString(1, this.getName());
			pst.setBoolean(2, this.isMain());
			pst.setDouble(3, this.getEqualsToMain());
			pst.setInt(4, this.id);
			pst.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}			
	}
	
	public boolean delete() {
		try {
			PreparedStatement pst = ConnectH2.getConnection().prepareStatement("DELETE FROM currency WHERE id = ?");
			pst.setInt(1, this.getId());
			pst.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass())
            return false;
		MyCurrency c = (MyCurrency) obj;
		if (!this.name.equals(c.getName()))
            return false;			
		return true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
