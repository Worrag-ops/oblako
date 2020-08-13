package entities;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import h2.ConnectH2;
import h2.H2EntityIdExtractor;

public class Bookmaker {
	private int id = 0;
	private String name;
	private Wallet wallet = null;
	
	public Bookmaker(int id, String name) {
		setId(id);
		setName(name);
	}
	
	public Bookmaker(String name) {
		setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Wallet getWallet() {
		return wallet;
	}

	public void setWallet(BigDecimal money, MyCurrency currency) {
		Wallet wallet = new Wallet(money, currency);
		this.wallet = wallet;
	}
	
	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}
	
	public boolean update() {
		try {
			PreparedStatement pst = ConnectH2.getConnection().prepareStatement("UPDATE bookmakers SET name = ?, wallet_money = ?, wallet_currency_id = ?  WHERE id = ?");
			int currency_id = this.getWallet().getCurrency().getId();
			pst.setString(1, this.getName());
			pst.setBigDecimal(2, this.getWallet().getMoney());
			pst.setInt(3, currency_id);
			pst.setInt(4, this.getId());
			pst.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}			
	}
	
	public boolean save() {
		try {
			PreparedStatement pst = ConnectH2.getConnection().prepareStatement("insert into bookmakers(name, wallet_money, wallet_currency_id) values(?,?,?)");
			int currency_id = H2EntityIdExtractor.getIdForEntByName("currency", this.getWallet().getCurrency().getName());
			Wallet wallet = this.getWallet();
			pst.setString(1, this.getName());
			pst.setBigDecimal(2, wallet.getMoney());
			pst.setInt(3, currency_id);
			pst.execute();
			
			int book_id = H2EntityIdExtractor.getIdForEntByName("bookmaker", this.getName());
			PreparedStatement pst2 = ConnectH2.getConnection().prepareStatement("insert into bookmaker_deposit(date, bookmaker_id, money) values(?,?,?)");
			pst2.setObject(1, wallet.getDeposit().get(0).getDate());
			pst2.setInt(2, book_id);
			pst2.setBigDecimal(3, wallet.getMoney());
			pst2.execute();
			
			setId(H2EntityIdExtractor.getIdForEntByName("bookmaker", this.getName()));
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}	
	}
	
	public boolean delete() {
		try {
			int book_id = this.getId();
			PreparedStatement pst = ConnectH2.getConnection().prepareStatement("DELETE FROM bookmakers WHERE id = ?");
			pst.setInt(1, book_id);
			pst.execute();
			
			PreparedStatement pst2 = ConnectH2.getConnection().prepareStatement("DELETE FROM bookmaker_deposit WHERE bookmaker_id = ?");
			pst2.setInt(1, book_id);
			pst2.execute();
			
			PreparedStatement pst3 = ConnectH2.getConnection().prepareStatement("DELETE FROM bookmaker_withdraw WHERE bookmaker_id = ?");
			pst3.setInt(1, book_id);
			pst3.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}	
	}
	
    public class Wallet{
    	private BigDecimal money;
    	private MyCurrency currency;
    	private List<Operation> deposited = new ArrayList<Operation>();
    	private List<Operation> withdrawn = new ArrayList<Operation>();
    	
    	public Wallet(BigDecimal money, MyCurrency currency) {
    		this.setMoney(money);
    		this.setCurrency(currency);
    	}   
    	
    	public void setMoney(BigDecimal money) {
    		this.money = money;
    	}
    	
    	public BigDecimal getMoney() {
    		return money;
    	}
    	
    	public void deposit(LocalDate date, BigDecimal money) {
    		Operation op = new Operation(date, money);
    		deposited.add(op);
    		setMoney(this.getMoney().add(money));
    	}
    	
    	public boolean withdraw(LocalDate date, BigDecimal money) {
    		if (this.money.compareTo(money) < 0) {
    			return false;
    		} else {
    			this.money.subtract(money);
        		Operation op = new Operation(date, money);
        		withdrawn.add(op);
    			return true;
    		}
    	}

		public MyCurrency getCurrency() {
			return currency;
		}

		public void setCurrency(MyCurrency currency) {
			this.currency = currency;
		}
		
		public List<Operation> getDeposit() {
			return deposited;
		}
		
		public List<Operation> getWithdrawn() {
			return withdrawn;
		}
		
		public class Operation {
			private LocalDate date;
			private BigDecimal money;
			
			public Operation(LocalDate date, BigDecimal money) {
				this.date = date;
				this.money = money;
			}
			
			public LocalDate getDate() {
				return date;
			}
			
			public void setDate(LocalDate date) {
				this.date = date;
			}
			
			public BigDecimal getMoney() {
				return money;
			}
			
			public void setMoney(BigDecimal money) {
				this.money = money;
			}
		}
    }
    
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass())
            return false;
		Bookmaker book = (Bookmaker) obj;
		if (!this.name.equals(book.getName()) || !this.getWallet().getCurrency().equals(book.getWallet().getCurrency()))
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
