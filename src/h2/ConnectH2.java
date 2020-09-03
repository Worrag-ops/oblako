package h2;

import java.sql.*;

public class ConnectH2 {

	private static final String URL = "jdbc:h2:~/testdb";
	private static final String LOG = "sa";
	private static final String PAS = "";
	private static Connection conn = null;
	
	public static void connect(){
		try {
			Class.forName("org.h2.Driver").newInstance();
			conn = DriverManager.getConnection(URL, LOG, PAS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public static void createTables() throws SQLException {
		Statement st = conn.createStatement();
		st.execute("CREATE TABLE IF NOT EXISTS disciplines (id int NOT NULL AUTO_INCREMENT, name char(50) NOT NULL, discType char(50) NOT NULL, UNIQUE(name), CONSTRAINT disciplines_pk PRIMARY KEY (id))");
		st.execute("CREATE TABLE IF NOT EXISTS teams (id int NOT NULL AUTO_INCREMENT, name char(50) NOT NULL, discipline_id int NOT NULL REFERENCES disciplines(id) ON DELETE CASCADE, profit decimal NOT NULL DEFAULT 0, UNIQUE(name, discipline_id), CONSTRAINT teams_pk PRIMARY KEY (id))");
		st.execute("CREATE TABLE IF NOT EXISTS tournaments (id int NOT NULL AUTO_INCREMENT, name char(80) NOT NULL, profit decimal NOT NULL DEFAULT 0, UNIQUE(name), CONSTRAINT tournaments_pk PRIMARY KEY (id))");
		st.execute("CREATE TABLE IF NOT EXISTS tournament_discipline (tournament_id int NOT NULL REFERENCES tournaments(id) ON DELETE CASCADE, discipline_id int NOT NULL REFERENCES disciplines(id) ON DELETE CASCADE, CONSTRAINT tour_disc_pk PRIMARY KEY (tournament_id, discipline_id))");
		st.execute("CREATE TABLE IF NOT EXISTS currency (id int NOT NULL AUTO_INCREMENT, name char(50) NOT NULL, isMain bool, equalsToMain float NOT NULL, CONSTRAINT currency_pk PRIMARY KEY (id))");
		st.execute("CREATE TABLE IF NOT EXISTS bookmakers (id int NOT NULL AUTO_INCREMENT, name char(50) NOT NULL, wallet_money decimal NOT NULL, wallet_currency_id int NOT NULL REFERENCES currency(id) ON DELETE CASCADE, wallet_deposit decimal, wallet_withdraw decimal, UNIQUE(name), CONSTRAINT bookmakers_pk PRIMARY KEY (id))");
		st.execute("CREATE TABLE IF NOT EXISTS bookmaker_deposit (deposit_id int NOT NULL AUTO_INCREMENT, date date NOT NULL, bookmaker_id int NOT NULL REFERENCES bookmakers(id) ON DELETE CASCADE, money decimal NOT NULL, CONSTRAINT book_deposit_pk PRIMARY KEY (deposit_id))");
		st.execute("CREATE TABLE IF NOT EXISTS bookmaker_withdraw (withdraw_id int NOT NULL AUTO_INCREMENT, date date NOT NULL, bookmaker_id int NOT NULL REFERENCES bookmakers(id) ON DELETE CASCADE, money decimal NOT NULL, CONSTRAINT book_withdraw_pk PRIMARY KEY (withdraw_id))");
		st.execute("CREATE TABLE IF NOT EXISTS bets (bet_id int NOT NULL AUTO_INCREMENT, date date NOT NULL, discipline_id int REFERENCES disciplines(id) ON DELETE CASCADE, team1_id int REFERENCES teams(id) ON DELETE CASCADE, team2_id int REFERENCES teams(id) ON DELETE CASCADE, tournament_id int REFERENCES tournaments(id) ON DELETE CASCADE, betEvent char(25), coef float NOT NULL, moneyBet decimal NOT NULL, result char(10) NOT NULL, profit decimal NOT NULL, book_id int REFERENCES bookmakers(id) ON DELETE CASCADE, isTotal boolean, isCashOut boolean DEFAULT false, CONSTRAINT bets_pk PRIMARY KEY (bet_id))");	
	}
	
	public static void dropTables() throws SQLException {
			Statement st = ConnectH2.getConnection().createStatement();
			st.execute("DROP TABLE IF EXISTS bets");
			st.execute("DROP TABLE IF EXISTS tournament_discipline");
			st.execute("DROP TABLE IF EXISTS tournaments");
			st.execute("DROP TABLE IF EXISTS teams");
			st.execute("DROP TABLE IF EXISTS disciplines");
			st.execute("DROP TABLE IF EXISTS bookmaker_deposit");
			st.execute("DROP TABLE IF EXISTS bookmaker_withdraw");
			st.execute("DROP TABLE IF EXISTS bookmakers");
			st.execute("DROP TABLE IF EXISTS currency");
	}
	
	public static Connection getConnection() {
		return conn;
	}
	
	}

