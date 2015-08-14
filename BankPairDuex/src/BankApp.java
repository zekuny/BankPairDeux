import java.text.SimpleDateFormat;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class BankApp {

	public static void main(String[] args) throws SQLException {
		// Connect to DB
		Connection conn = DBConnection.connectDB();
		String sql = "";

		PreparedStatement preStatement;
		ResultSet result;

		Scanner sc = new Scanner(System.in);

		System.out.println("Welcome to Evil Banking System!");
		System.out
				.println("*****************************************************");

		String regex = "\\d+";
		String regex2 = "[a-zA-Z ]+";
		String accountName = Validator.getString(sc, "Enter Customer Name or -1 to stop creating accounts : ");
		while (!accountName.equalsIgnoreCase("-1") && !accountName.matches(regex2)) {
			accountName = Validator.getString(sc, "Enter a valid name: ");
		}

		while (!accountName.equalsIgnoreCase("-1")) {
			// enter type
			int accType = Validator.getInt(sc, "Enter account type: ");
			while (accType != 1 && accType != 2) {
				accType = Validator.getInt(sc, "Enter a valid account type: ");
			}
			while (!accountName.equalsIgnoreCase("-1") && DBOperation.duplicateAccount(accountName, accType, conn)) {
				System.out.println("Account already exists!!!!!!!");
				// enter name
				accountName = Validator.getString(sc,"Enter Customer Name or -1 to stop creating accounts : ");
				while (!accountName.matches(regex2)) {
					accountName = Validator.getString(sc,
							"Enter a valid name: ");
				}

				// enter type
				accType = Validator.getInt(sc, "Enter account type: ");
				while (accType != 1 && accType != 2) {
					accType = Validator.getInt(sc,
							"Enter a valid account type: ");
				}
			}
			
			if (accountName.equalsIgnoreCase("-1")) {
				break;
			}

			String accountNumber = Validator.getString(sc,"Enter a valid number for acct #: ");
			while (!accountNumber.matches(regex)) {
				accountNumber = Validator.getString(sc,"Enter a valid number for acct #: ");
			}

			boolean hasAccount = DBOperation.hasAccount(accountNumber, conn);
			if (!hasAccount) {
				double balance = Validator.getDouble(sc, "Enter the balance for acct # " + accountNumber + " : ");
				DBOperation.insertAccount(accountNumber, accountName, balance, accType, conn);
			} else {
				System.out.println("Account already exist!");
				accountNumber = Validator.getString(sc,"Enter a valid number for acct #: ");
			}
			System.out.println("------------------------------------------------------");
			accountName = Validator.getString(sc, "Enter Customer Name or -1 to stop entering accounts : ");
			while (!accountName.equals("-1") && !accountName.matches(regex2)) {
				accountName = Validator.getString(sc, "Enter a valid name for acct #: ");
			}
		}
		
		
		// Create transaction
		System.out.println("*****************************************************");
		List<Transaction> transactionList = new ArrayList<Transaction>();
		System.out.println("Enter transaction information: ");
		int typeID = Validator.getInt(sc,"Enter a transaction type (1 - Deposit 2 - Check 3 - Withdrawal 4 - Debit Card 5 - Transfer) or -1 to finish : ");

		while (typeID != -1 && typeID != 1 && typeID != 2 && typeID != 3 && typeID != 4 && typeID != 5) {
			typeID = Validator.getInt(sc,"Enter a transaction type (1 - Deposit 2 - Check 3 - Withdrawal 4 - Debit Card 5 - Transfer) or -1 to finish : ");
		}
		while (typeID != -1) {
			String fromAccountNumber = "", toAccountNumber = "";
			boolean valid = true;
			if (typeID == 5) {
				fromAccountNumber = Validator.getString(sc,
						"You want to transfer money from account#: ");
				while (!fromAccountNumber.matches(regex)) {
					fromAccountNumber = Validator.getString(sc,
							"Enter a valid number for acct#: ");
				}
				toAccountNumber = Validator.getString(sc,
						"You want to transfer money to accout#: ");
				while (!toAccountNumber.matches(regex)) {
					toAccountNumber = Validator.getString(sc,
							"Enter a valid number for acct#: ");
				}
				valid = DBOperation.hasAccount(fromAccountNumber, conn)
						&& DBOperation.hasAccount(toAccountNumber, conn);

			} else {
				fromAccountNumber = Validator.getString(sc,
						"Please enter the account#: ");
				while (!fromAccountNumber.matches(regex)) {
					fromAccountNumber = Validator.getString(sc,
							"Enter a valid number for acct#: ");
				}
				valid = DBOperation.hasAccount(fromAccountNumber, conn);
			}
			if (valid) {
				double amount = Validator.getDouble(sc,
						"Enter the amount of the check:", 0,
						1000000000000000000.0);
				String tmp = Validator
						.getString(sc,
								"Enter the date of the transaction: (please enter in dd-MMM-yyyy format): ");
				String regexDate = "^([012]?\\d|3[01])-([Jj][Aa][Nn]|[Ff][Ee][bB]|[Mm][Aa][Rr]|[Aa][Pp][Rr]|[Mm][Aa][Yy]|[Jj][Uu][Nn]|[Jj][u]l|[aA][Uu][gG]|[Ss][eE][pP]|[oO][Cc]|[Nn][oO][Vv]|[Dd][Ee][Cc])-(19|20)\\d\\d$";
				while (!tmp.matches(regexDate)) {
					tmp = Validator
							.getString(sc,
									"Enter a valid date of the transaction: (please enter in dd-MMM-yyyy format): ");
				}

				System.out.println("1 " + tmp);
				SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy");
				Date date = new Date();

				try {
					date = format.parse(tmp);
				} catch (Exception e) {

				}
				Transaction t = new Transaction(typeID, fromAccountNumber,
						toAccountNumber, amount, date);
				transactionList.add(t);
				DBOperation.insertTransaction(t, conn);
			}

			System.out.println("------------------------------------------------------");
			typeID = Validator.getInt(sc,"Enter a transaction type (1 - Deposit 2 - Check 3 - Withdrawal 4 - Debit Card) or -1 to finish : ");
			while (typeID != -1 && typeID != 1 && typeID != 2 && typeID != 3 && typeID != 4 && typeID != 5) {
				typeID = Validator.getInt(sc,"Enter a transaction type (1 - Deposit 2 - Check 3 - Withdrawal 4 - Debit Card) or -1 to finish : ");
			}
		}

		Comparator<Transaction> dateComparator = new Comparator<Transaction>() {
			public int compare(Transaction t1, Transaction t2) {
				return t1.getDate().compareTo(t2.getDate());
			}
		};

		Collections.sort(transactionList, dateComparator);

		for (Transaction t : transactionList) {
			String fromAccountNumber = t.getFromAccountNumber();
			String toAccountNumber = t.getToAccountNumber();

			System.out.println("**************************");

			// update
			if (t.getTypeID() == 5) {
				DBOperation.countTransfer(fromAccountNumber, toAccountNumber, t.getAmount(), conn);
			} else {
				DBOperation.calculateBalance(fromAccountNumber, t.getAmount(), t.getTypeID(), conn);
			}
		}
		System.out.println("*****************************************************");
		System.out.println("Print account infomation");
// get all account information
		sql = "select * from Account";
		preStatement = conn.prepareStatement(sql);
		result = preStatement.executeQuery();
		System.out.printf("\n%-12s %-10s %-20s %-8s \n", "Account", "Type" , "Name", "Balance");
		while (result.next()) {
			// System.out.println( result.getString("amount"));

			System.out.printf("\n%-12s %-10s %-20s %-8s \n",
					result.getString("accountnumber"),
					result.getString("acctype"),
					result.getString("accountname"),
					result.getString("balance"));
			// System.out.println("Current Date from Oracle : " +
			// result.getString("current_day"));
		}
		System.out.println("*****************************************************");
		System.out.println("Print transaction infomation");
		sql = "select transactionID, fromAccountNumber,toAccountNumber, amount, typeid, TO_CHAR(\"date\",\'YYYY-MM-DD\') as tDate from transaction order by \"date\"";
		preStatement = conn.prepareStatement(sql);
		result = preStatement.executeQuery();
		System.out.printf("\n %-12s %-12s %-12s %-5s %-15s\n", "FromAmount","toAmount","Amount",
				"type", "Date");
		while (result.next()) {
			// System.out.println( result.getString("amount"));
			System.out.printf("\n %-12s %-12s %-12s %-5s %-15s\n",
					result.getString("fromAccountNumber"), 
					result.getString("toAccountNumber"),
					result.getString("amount"),
					result.getString("typeid"),
					result.getString("tDate"));
			// System.out.println("Current Date from Oracle : " +
			// result.getString("current_day"));
		}
	}
}
