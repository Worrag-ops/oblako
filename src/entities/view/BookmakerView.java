package entities.view;

import entities.Bookmaker;
import entities.Bookmaker.Wallet.Operation;

public class BookmakerView {
	private String name;
	private String currency;
	private double balance;
	private double deposit = 0;
	private double withdraw = 0;
	
	
	public BookmakerView(Bookmaker book) {
		name = book.getName();
		balance = book.getWallet().getMoney().doubleValue();
		setCurrency(book.getWallet().getCurrency().getName());
		for (Operation o : book.getWallet().getDeposit()) {
			deposit += o.getMoney().doubleValue();
		}
		for (Operation o : book.getWallet().getWithdrawn()) {
			withdraw += o.getMoney().doubleValue();
		}		
	}
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public double getBalance() { return balance; }
	public void setBalance(double balance) { this.balance = balance; }
	public double getDeposit() { return deposit; }
	public void setDeposit(double deposit) { this.deposit = deposit; }
	public double getWithdraw() { return withdraw; }
	public void setWithdraw(double withdraw) { this.withdraw = withdraw; }
	public String getCurrency() { return currency; }
	public void setCurrency(String currency) { this.currency = currency; }
	
	
	
	
}
