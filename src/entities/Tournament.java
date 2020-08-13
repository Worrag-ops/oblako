package entities;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import h2.ConnectH2;
import h2.H2EntityIdExtractor;

public class Tournament {
	private int id = 0;
	private String name;
	private BigDecimal profit;
	private List<Discipline> disciplines = new ArrayList<Discipline>();
	
	public Tournament (int id, String name, List<Discipline> list, BigDecimal profit) {
		this(name, list, profit);
		this.id = id;
	}	
	
	public Tournament (String name, List<Discipline> list, BigDecimal profit) {
		setName(name);
		setDisciplines(list);
		setProfit(profit);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public BigDecimal getProfit() {
		return profit;
	}
	
	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public List<Discipline> getDisciplines() {
		return disciplines;
	}

	public void setDisciplines(List<Discipline> disciplines) {
		this.disciplines = disciplines;
	}
	
	public boolean hasDiscipline(Discipline d) {
		for (Discipline disc : disciplines) {
			if (d.equals(disc)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean save() {
		Connection conn = ConnectH2.getConnection();
		try {
			PreparedStatement pst = conn.prepareStatement("insert into tournaments(name, profit) values(?,?)");
			pst.setString(1, name);
			pst.setBigDecimal(2, profit);			
			pst.execute();	
			
			this.setId(H2EntityIdExtractor.getIdForEntByName("tournament", this.getName()));
			for (Discipline d : disciplines) {
				int d_id = d.getId();			
				PreparedStatement pst2 = conn.prepareStatement("insert into tournament_discipline(tournament_id, discipline_id) values(?,?)");
				pst2.setInt(1, this.getId());
				pst2.setInt(2, d_id);
				pst2.execute();
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
		return false;
	}
	
	public boolean update() {
		try {
			PreparedStatement pst = ConnectH2.getConnection().prepareStatement("UPDATE tournaments SET name = ?, profit = ? WHERE id = ?");
			pst.setString(1, this.getName());
			pst.setBigDecimal(2, this.profit);
			pst.setInt(3, this.getId());
			pst.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	public boolean addDiscipline(Discipline d) {
		if(this.getDisciplines().add(d)) {
			try {
				int d_id = d.getId();	
				PreparedStatement pst = ConnectH2.getConnection().prepareStatement("INSERT INTO tournament_discipline(tournament_id, discipline_id) values(?,?)");
				pst.setInt(1, this.getId());
				pst.setInt(2, d_id);
				pst.execute();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();

			}				
		}
		return false;			
	}
	
	public boolean removeDiscipline(Discipline d) {
		if(this.getDisciplines().remove(d)) {
			try {
				int d_id = d.getId();	
				PreparedStatement pst = ConnectH2.getConnection().prepareStatement("DELETE FROM tournament_discipline WHERE tournament_id = ? AND discipline_id = ?");
				pst.setInt(1, this.getId());
				pst.setInt(2, d_id);
				pst.execute();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();

			}				
		}
		return false;			
	}
	
	public boolean delete() {
		try {
			PreparedStatement pst = ConnectH2.getConnection().prepareStatement("DELETE FROM tournaments WHERE id = ?");
			pst.setInt(1, this.getId());
			pst.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}			
	}

	
	@Override
	public String toString() {
		return (getName() + "; " + disciplines.toString());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
