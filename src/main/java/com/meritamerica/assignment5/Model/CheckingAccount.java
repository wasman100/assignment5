package com.meritamerica.assignment5.Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CheckingAccount extends BankAccount {
	private static double INTEREST_RATE = 0.0001;

	public static CheckingAccount readFromString(String accountData) throws ParseException {
		String[] data = accountData.split(",");

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		int accNumb = Integer.parseInt(data[0]);
		double balance = Double.parseDouble(data[1]);
		double interestRate = Double.parseDouble(data[2]);
		Date openDate = formatter.parse(data[3]);

		return new CheckingAccount(accNumb, balance, interestRate, openDate);
	}

	public CheckingAccount(double balance, double interestRate) {
		super(balance, interestRate);
	}

	public CheckingAccount(int accNumb, double balance, double interestRate, Date openDate) {
		super(accNumb, balance, interestRate, openDate);
	}

	public CheckingAccount() {
		super(0, INTEREST_RATE);
	}

	public CheckingAccount(double balance) {
		super(balance, INTEREST_RATE);
	}
}