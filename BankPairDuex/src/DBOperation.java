import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBOperation {
	public static boolean hasAccount(String accNumber, Connection conn)
			throws SQLException {
		// judge if the account Number exists in the database
		String sql = "select * from account where accountNumber = '"
				+ accNumber + "'";

		PreparedStatement preStatement = conn.prepareStatement(sql);
		ResultSet result = preStatement.executeQuery();
		if (result.next()) {
			return true;
		} else {
			return false;
		}
	}

	public static void insertAccount(String accountNumber, String name,
			double balance, int accType, Connection conn) throws SQLException {
		String sql = "insert into Account (accountid, accountnumber, accountname, balance, accType) values (NULL, '"+ accountNumber+ "', '"+ name+ "', "+ balance+ ", "+ accType + ")";
		// creating PreparedStatement object to execute query
		PreparedStatement preStatement = conn.prepareStatement(sql);
		preStatement.executeQuery();
	}

	public static void insertTransaction(Transaction t, Connection conn) throws SQLException {
		String sql = "insert into transaction (transactionid, fromAccountnumber, toAccountNumber, amount, typeid, \"date\") values (NULL, '"+ t.getFromAccountNumber() + "', '" + t.getToAccountNumber()+ "', "+ t.getAmount()+ ", "+ t.getTypeID()+ ", to_date('"+ t.getDateString()+ "', 'DD Mon yyyy'))";
		// creating PreparedStatement object to execute query
		System.out.println(sql);
		PreparedStatement preStatement = conn.prepareStatement(sql);
		preStatement.executeQuery();
	}

	public static boolean duplicateAccount(String customer_name, int type,
			Connection conn) throws SQLException {
		String sql = "select * from Account where accountName = '"
				+ customer_name + "' and accType = " + type;
		PreparedStatement preStatement = conn.prepareStatement(sql);
		ResultSet result = preStatement.executeQuery();
		if (result.next()) {
			return true;
		} else {
			return false;
		}
	}

	public static double getBalance(String accountNumber, Connection conn)
			throws SQLException {
		String sql = "select balance from Account where accountNumber = "
				+ accountNumber;
		PreparedStatement preStatement = conn.prepareStatement(sql);
		ResultSet result = preStatement.executeQuery();
		if(result.next()){
			return Double.valueOf(result.getString("balance"));
		}else{
			return 0;
		}
		
	}

	public static int getAccountType(String accountNumber, Connection conn)
			throws SQLException {
		String sql = "select accType from Account where accountNumber = "
				+ accountNumber;
		PreparedStatement preStatement = conn.prepareStatement(sql);
		ResultSet result = preStatement.executeQuery();
		if(result.next()){
			return Integer.valueOf(result.getString("accType"));
		}else{
			return 0;
		}
	}

	public static void setBalance(String accountNumber, double balance,
			Connection conn) throws SQLException {
		String sql = "update Account set balance = " + balance
				+ " where accountNumber = " + accountNumber;
		PreparedStatement preStatement = conn.prepareStatement(sql);
		preStatement.executeQuery();
	}

	public static String getAccountNumberFromName(String name, Connection conn)
			throws SQLException {
		String sql = "select accountNumber from account where accountName = '"
				+ name + "' and accType = 2";
		PreparedStatement preStatement = conn.prepareStatement(sql);
		ResultSet result = preStatement.executeQuery();
		if (result.next()) {
			return result.getString("accountNumber");
		} else {
			return "no";
		}
	}

	public static String getNameFromAccountNumber(String accountNumber,
			Connection conn) throws SQLException {
		String sql = "select accountName from account where accountNumber = '"
				+ accountNumber + "' and accType = 1";
		PreparedStatement preStatement = conn.prepareStatement(sql);
		ResultSet result = preStatement.executeQuery();
		if (result.next()) {
			return result.getString("accountName");
		} else {
			return "no";
		}
	}

	public static void calculateBalance(String accountNumber, double amount, int transactionType, Connection conn) throws SQLException {
		int accountType = getAccountType(accountNumber, conn);
		double balance = getBalance(accountNumber, conn);

		if (transactionType == 1) {
			setBalance(accountNumber, balance + amount, conn);
		} else{
			if(accountType==1){
				if(balance-amount>=0){
					DBOperation.setBalance(accountNumber,balance-amount, conn);
				}else{
					String name = getNameFromAccountNumber(accountNumber, conn);
					String tmp = getAccountNumberFromName(name, conn);
					if (tmp.equals("no")) {
						setBalance(accountNumber, balance, conn);
					} else {
						setBalance(accountNumber, 0, conn);
						setBalance(tmp, (balance - amount - 15), conn);
					}
				}
			}else{
				DBOperation.setBalance(accountNumber,balance-amount, conn);
			}
		}
	}
			
	public static void countTransfer(String fromAccount, String toAccount,
			double amount, Connection conn) throws SQLException {
		calculateBalance(toAccount, amount, 1, conn);
		calculateBalance(fromAccount, amount, 5, conn);
	}

}
