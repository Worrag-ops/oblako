package storage;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import entities.Bookmaker;
import entities.MyCurrency;
import h2.ConnectH2;
import h2.H2EntityIdExtractor;

public class Bookmakers {

	private static Bookmakers instance;
	private List<Bookmaker> bookList = new ArrayList<Bookmaker>();
	
	private Bookmakers() {}

	public void add(Bookmaker b) {
		bookList.add(b);
	}
	
	public Bookmaker get(String name) {
		for (Bookmaker b : bookList){
			if (b.getName().equals(name)) return b;
		}
		return null;
	}
	
	public List<Bookmaker> getAll(){
		return bookList;
	}
	
	public boolean remove(Bookmaker book) {
		return bookList.remove(book);
	}
	
	public static Bookmakers getInstance() {
        if(instance == null){	
            instance = new Bookmakers();
        }
		return instance;
	}
	
	public void restore() {
		Connection conn = ConnectH2.getConnection();
		Statement st;
		ResultSet result;
		try {
			st = conn.createStatement();
			result = st.executeQuery("SELECT * FROM bookmakers");
			while (result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				BigDecimal money = result.getBigDecimal("wallet_money");
				MyCurrency currency = Currencies.getInstance().get(H2EntityIdExtractor.getNameForEntById("currency", result.getInt("wallet_currency_id")));
				Bookmaker book = new Bookmaker(id, name);
				book.setWallet(money, currency);
				
				int bookmaker_id = H2EntityIdExtractor.getIdForEntByName("bookmaker", name);
				PreparedStatement pst_deposit = conn.prepareStatement("SELECT * FROM bookmaker_deposit WHERE bookmaker_id = ?");
				pst_deposit.setInt(1, bookmaker_id);
				ResultSet resultDeposits = pst_deposit.executeQuery();
				while (resultDeposits.next()) {
					LocalDate depositDate = resultDeposits.getDate("date").toLocalDate();
					BigDecimal depositMoney = resultDeposits.getBigDecimal("money");
					book.getWallet().deposit(depositDate, depositMoney);
				}
				
				PreparedStatement pst_withdraw = conn.prepareStatement("SELECT * FROM bookmaker_withdraw WHERE bookmaker_id = ?");
				pst_withdraw.setInt(1, bookmaker_id);
				ResultSet resultWithdraw = pst_withdraw.executeQuery();
				while (resultWithdraw.next()) {
					LocalDate withdrawDate = resultDeposits.getDate("date").toLocalDate();
					BigDecimal withdrawMoney = resultDeposits.getBigDecimal("money");
					book.getWallet().withdraw(withdrawDate, withdrawMoney);
				}
				book.getWallet().setMoney(money);
				this.add(book);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
