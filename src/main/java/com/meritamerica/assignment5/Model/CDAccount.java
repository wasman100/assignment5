package com.meritamerica.assignment5.Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.constraints.NotNull;

public class CDAccount extends BankAccount {
	private CDOffering offering;
	@NotNull
	private int terms;

	CDAccount(CDOffering offering, double openingBalance) {
		super(openingBalance, offering.getInterestRate());
		this.offering = offering;
	}

	CDAccount(int term, double openingBalance, double interestRate) {
		this(new CDOffering(term, interestRate), openingBalance);
	}

	CDAccount(int accNumb, double openingBalance, double interestRate, Date openDate, int term) {
		super(accNumb, openingBalance, interestRate, openDate);
		this.offering = new CDOffering(term, interestRate);
	}

	public CDAccount() {
		super();
		this.offering = new CDOffering();
	}

	public static CDAccount readFromString(String accountData) throws ParseException {
		String[] data = accountData.split(",");

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		int accNumb = Integer.parseInt(data[0]);
		double balance = Double.parseDouble(data[1]);
		double interestRate = Double.parseDouble(data[2]);
		Date openDate = formatter.parse(data[3]);
		int term = Integer.parseInt(data[4]);

		return new CDAccount(accNumb, balance, interestRate, openDate, term);
	}

	public double futureValue() {
		double futureVal = this.getBalance() * Math.pow(1 + this.getInterestRate(), offering.getTerm());
		return futureVal;
	}

	public int getTerm() {
		return offering.getTerm();
	}

	public void setTerm(int years) {
		this.terms = years;
		offering.setTerm(years);
	}

	public void setInterestRate(double interestRate) {
		offering.setInterestRate(interestRate);
	}

	public double getInterestRate() {
		return offering.getInterestRate();
	}

	@Override
	public String writeToString() {
		StringBuilder str = new StringBuilder();

		str.append(super.writeToString());
		str.append("," + offering.getTerm());
		return str.toString();
	}

	@Override
	public boolean withdraw(double amount) {
		return false;
	}

	@Override
	public boolean deposit(double amount) {
		return false;
	}
}
