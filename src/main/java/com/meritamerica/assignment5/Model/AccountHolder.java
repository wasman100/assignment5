package com.meritamerica.assignment5.Model;

import java.util.Arrays;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class AccountHolder implements Comparable {
	private static long ID = 1;

	private long id;
	@NotNull(message = "First name can not be Null")
	@org.hibernate.validator.constraints.NotBlank(message = "First name must not be empty")
	private String firstName;
	private String middleName;
	@NotNull(message = "Last name can not be Null")
	@org.hibernate.validator.constraints.NotBlank(message = "Last name must not be empty")
	private String lastName;
	@NotNull
	@Size(min = 9, message = "SNN can not be less than 9 characters")
	private String ssn;
	private CheckingAccount[] checkingAccounts;
	private SavingsAccount[] savingsAccounts;
	private CDAccount[] CDAccounts;

	private int numberOfCheckings = 0;
	private int numberOfSavings = 0;
	private int numberOfCDAs = 0;

	public static AccountHolder readFromString(String accountHolderData) {
		String[] data = accountHolderData.split(",");
		String firstName = data[0];
		String middleName = data[1];
		String lastName = data[2];
		String ssn = data[3];

		return new AccountHolder(firstName, middleName, lastName, ssn);
	}

	public AccountHolder() {
		this.id = AccountHolder.ID;
		AccountHolder.ID++;
		checkingAccounts = new CheckingAccount[10];
		savingsAccounts = new SavingsAccount[10];
		CDAccounts = new CDAccount[10];
	}

	public AccountHolder(String firstName, String middleName, String lastName, String ssn) {
		this();

		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.ssn = ssn;
	}

	public void createCheckingArray(int numOfAccount) {
		this.checkingAccounts = new CheckingAccount[numOfAccount];
	}

	public void createSavingArray(int numOfAccount) {
		this.savingsAccounts = new SavingsAccount[numOfAccount];
	}

	public void createCDAccounts(int numOfAccount) {
		this.CDAccounts = new CDAccount[numOfAccount];
	}

	public CheckingAccount addCheckingAccount(double openingBalance) throws ExceedsCombinedBalanceLimitException {
		CheckingAccount acc = new CheckingAccount(openingBalance);

		return this.addCheckingAccount(acc);
	}


	public CheckingAccount addCheckingAccount(CheckingAccount checkingAccount)
			throws ExceedsCombinedBalanceLimitException {
		if (canOpen(checkingAccount.getBalance())) {
			double amount = checkingAccount.getBalance();

			DepositTransaction tran = new DepositTransaction(checkingAccount, amount, new Date());

			checkingAccount.addTransaction(tran);


			this.numberOfCheckings++;


			if (this.numberOfCheckings > this.checkingAccounts.length) {
				this.checkingAccounts = this.extendCheckingArray();
			}

			this.checkingAccounts[this.numberOfCheckings - 1] = checkingAccount;

			return this.checkingAccounts[this.numberOfCheckings - 1];
		} else {
			throw new ExceedsCombinedBalanceLimitException();
		}
	}

	public CheckingAccount[] getCheckingAccounts() {
		CheckingAccount[] checkings = Arrays.copyOf(this.checkingAccounts, this.numberOfCheckings);
		return checkings;
	}

	public int getNumberOfCheckingAccounts() {
		return this.numberOfCheckings;
	}

	public double getCheckingBalance() {
		double total = 0;
		for (int i = 0; i < this.numberOfCheckings; i++) {
			total += this.checkingAccounts[i].getBalance();
		}

		return total;
	}


	public SavingsAccount addSavingsAccount(double openingBalance) throws ExceedsCombinedBalanceLimitException {
		SavingsAccount sav = new SavingsAccount(openingBalance);
		return this.addSavingsAccount(sav);
	}


	public SavingsAccount addSavingsAccount(SavingsAccount savingsAccount) throws ExceedsCombinedBalanceLimitException {
		if (canOpen(savingsAccount.getBalance())) {
			double amount = savingsAccount.getBalance();

			DepositTransaction tran = new DepositTransaction(savingsAccount, savingsAccount.getBalance(), new Date());

			savingsAccount.addTransaction(tran);


			this.numberOfSavings++;

			if (this.numberOfSavings > this.savingsAccounts.length) {
				this.savingsAccounts = this.extendSavingArray();
			}

			this.savingsAccounts[this.numberOfSavings - 1] = savingsAccount;

			return this.savingsAccounts[this.numberOfSavings - 1];
		} else {
			throw new ExceedsCombinedBalanceLimitException();
		}
	}

	public SavingsAccount[] getSavingsAccounts() {
		SavingsAccount[] savings = Arrays.copyOf(this.savingsAccounts, this.numberOfSavings);
		return savings;
	}

	public int getNumberOfSavingsAccounts() {
		return this.numberOfSavings;
	}

	public double getSavingsBalance() {
		double total = 0;
		for (int i = 0; i < this.numberOfSavings; i++) {
			total += this.savingsAccounts[i].getBalance();
		}

		return total;
	}

	public CDAccount addCDAccount(CDOffering offering, double openingBalance)
			throws ExceedsFraudSuspicionLimitException {
		CDAccount acc = new CDAccount(offering, openingBalance);

		return this.addCDAccount(acc);

	}

	public CDAccount addCDAccount(CDAccount cdAccount) throws ExceedsFraudSuspicionLimitException {
		this.numberOfCDAs++;

		if (this.numberOfCDAs > this.CDAccounts.length) {
			this.CDAccounts = this.extendCDArray();
		}

		DepositTransaction tran = new DepositTransaction(cdAccount, cdAccount.getBalance(), new Date());

		cdAccount.addTransaction(tran);

		this.CDAccounts[this.numberOfCDAs - 1] = cdAccount;

		return this.CDAccounts[this.numberOfCDAs - 1];
	}

	public CDAccount[] getCDAccounts() {
		CDAccount[] cds = Arrays.copyOf(this.CDAccounts, this.numberOfCDAs);
		return cds;
	}

	public int getNumberOfCDAccounts() {
		return this.numberOfCDAs;
	}

	public double getCDBalance() {
		double total = 0;
		for (int i = 0; i < this.numberOfCDAs; i++) {
			total += this.CDAccounts[i].getBalance();
		}

		return total;
	}

	public double getCombinedBalance() {
		return this.getCDBalance() + this.getCheckingBalance() + this.getSavingsBalance();
	}


	private boolean canOpen(double deposit) throws ExceedsCombinedBalanceLimitException {
		if (this.getCombinedBalance() < 250000.00) {
			return true;
		} else {
			System.out.println("Total is over 250,000. Can not open a new account");
			throw new ExceedsCombinedBalanceLimitException();
		}
	}

	@Override
	public int compareTo(Object o) {
		AccountHolder acc = (AccountHolder) o;
		if (this.getCombinedBalance() < acc.getCombinedBalance())
			return -1;
		else if (this.getCombinedBalance() > acc.getCombinedBalance())
			return 1;
		else
			return 0;
	}


	public BankAccount findAccount(long ID) {
		for (int i = 0; i < this.numberOfCheckings; i++) {
			if (this.checkingAccounts[i].getAccountNumber() == ID) {
				return this.checkingAccounts[i];
			}
		}

		for (int j = 0; j < this.numberOfSavings; j++) {
			if (this.savingsAccounts[j].getAccountNumber() == ID) {
				return this.savingsAccounts[j];
			}
		}

		for (int j = 0; j < this.numberOfCDAs; j++) {
			if (this.CDAccounts[j].getAccountNumber() == ID) {
				return this.CDAccounts[j];
			}
		}

		return null;
	}

	public CheckingAccount[] extendCheckingArray() {
		CheckingAccount[] checkings = new CheckingAccount[this.checkingAccounts.length * 2];
		for (int i = 0; i < this.checkingAccounts.length; i++) {
			checkings[i] = this.checkingAccounts[i];
		}

		return checkings;
	}

	public SavingsAccount[] extendSavingArray() {
		SavingsAccount[] savings = new SavingsAccount[this.savingsAccounts.length * 2];

		for (int i = 0; i < this.savingsAccounts.length; i++) {
			savings[i] = this.savingsAccounts[i];
		}

		return savings;
	}

	public CDAccount[] extendCDArray() {
		CDAccount[] cds = new CDAccount[this.CDAccounts.length * 2];

		for (int i = 0; i < this.CDAccounts.length; i++) {
			cds[i] = this.CDAccounts[i];
		}

		return cds;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastname(String lastName) {
		this.lastName = lastName;
	}

	public String getSSN() {
		return ssn;
	}

	public void setSSN(String ssn) {
		this.ssn = ssn;
	}

	public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}