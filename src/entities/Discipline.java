package entities;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import h2.ConnectH2;
import h2.H2EntityIdExtractor;

public class Discipline {
	private int id = 0;
	private String name;
	private String discType;
	
	public Discipline(int id, String name, String discType) {
		this(name, discType);
		this.id = id;
	}
	
	public Discipline(String name, String discType) {
		this.setName(name);
		this.setDiscType(discType);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDiscType() {
		return discType;
	}

	public void setDiscType(String discType) {
		this.discType = discType;
	}
	
	public boolean save() {
		try {
			PreparedStatement pst = ConnectH2.getConnection().prepareStatement("insert into disciplines(name, discType) values(?,?)");
			pst.setString(1, this.getName());
			pst.setString(2, this.getDiscType());
			pst.execute();
			
			this.setId(H2EntityIdExtractor.getIdForEntByName("discipline", this.getName()));
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	public boolean update() {
		try {
			PreparedStatement pst = ConnectH2.getConnection().prepareStatement("UPDATE disciplines SET name = ?, discType = ? WHERE id = ?");
			pst.setString(1, this.getName());
			pst.setString(2, this.getDiscType());
			pst.setInt(3, this.id);
			pst.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}			
	}
	
	public boolean delete() {
		try {
			PreparedStatement pst = ConnectH2.getConnection().prepareStatement("DELETE FROM disciplines WHERE id = ?");
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
		Discipline d = (Discipline) obj;
		if (!this.name.equals(d.getName()) || !this.discType.equals(d.getDiscType()))
            return false;			
		return true;
	}
	
	@Override
	public String toString() {
		return (name + " " + discType);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}