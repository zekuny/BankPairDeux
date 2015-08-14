import java.util.Date;

public class Transaction {
	int typeID;
	String fromAccountNumber;
	String toAccountNumber;
	double amount;
	Date date;

	public Transaction() { 
		typeID = 0;
		fromAccountNumber = "0000000";
		toAccountNumber = "0000000";
		amount = 0.0;
		date = new Date();
	}

	public Transaction(int t, String a, String ta, double am, Date d) {
		typeID = t;
		fromAccountNumber = a;
		toAccountNumber = ta;
		amount = am;
		date = d;
	}

	public int getTypeID() {
		return typeID;
	}

	public void setType(int typeID) {
		this.typeID = typeID;
	}

	public String getFromAccountNumber() {
		return fromAccountNumber;
	}

	public void setFromAccountNumber(String accountNumber) {
		this.fromAccountNumber = accountNumber;
	}
	
	public String getToAccountNumber() {
		return toAccountNumber;
	}

	public void setToAccountNumber(String accountNumber) {
		this.toAccountNumber = accountNumber;
	}

	public double getAmount() {
		return amount;
	}
 
	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getDate(){
		return date;
	}
	
	public String getDateString() {
		String s = date.toString();
		String[] tmp = s.split(" ");
		return tmp[2] + " " + tmp[1] + " " + tmp[5];
	}

	public void setDate(Date date) {
		this.date = date;
	}
}