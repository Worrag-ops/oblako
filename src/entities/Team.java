package entities;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import h2.ConnectH2;
import h2.H2EntityIdExtractor;

public class Team {
	private int id = 0;
	private String name;
	private Discipline discipline;
	private BigDecimal profit;

	public Team (int id, String name, Discipline discipline, BigDecimal profit) {
		this(name, discipline, profit);
		this.id = id;
	}
	
	public Team (String name, Discipline discipline) {
		setName(name);
		setDiscipline(discipline);
		setProfit(BigDecimal.ZERO);
	}
	
	public Team (String name, Discipline discipline, BigDecimal profit) {
		setName(name);
		setDiscipline(discipline);
		setProfit(profit);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Discipline getDiscipline() {
		return discipline;
	}

	public void setDiscipline(Discipline discipline) {
		this.discipline = discipline;
	}
	
	public BigDecimal getProfit() {
		return profit;
	}
	
	public void setProfit(BigDecimal p) {
		this.profit = p;
	}
	
	public boolean save() {
		try {
			int discId = H2EntityIdExtractor.getIdForEntByName("discipline", this.getDiscipline().getName());
			if (discId == 0) return false;
			
			PreparedStatement pst = ConnectH2.getConnection().prepareStatement("insert into teams(name, discipline_id, profit) values(?,?,?)");
			pst.setString(1, this.getName());
			pst.setInt(2, discId);
			pst.setBigDecimal(3, this.getProfit());
			pst.execute();
			
			this.setId(H2EntityIdExtractor.getTeamId(this.getName(), this.getDiscipline().getName()));
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	public boolean delete() {
		try {
			PreparedStatement pst = ConnectH2.getConnection().prepareStatement("DELETE FROM teams WHERE id = ?");
			pst.setInt(1, this.getId());
			pst.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}			
	}
	
	public boolean update() {
		try {
			int discipline_id = this.getDiscipline().getId();
			PreparedStatement pst = ConnectH2.getConnection().prepareStatement("UPDATE teams SET name = ?, discipline_id = ?, profit = ? WHERE id = ?");
			pst.setString(1, this.getName());
			pst.setInt(2, discipline_id);
			pst.setBigDecimal(3, this.getProfit());
			pst.setInt(4, this.getId());
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
		Team t = (Team) obj;
		if (!this.name.equals(t.getName()) || !this.getDiscipline().equals(t.getDiscipline()))
            return false;			
		return true;
	}
	
	@Override
	public String toString() {
		return (name + " " + discipline.getName() + " " + String.valueOf(profit));
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
