package com.meritamerica.assignment5.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;

public class MeritAmericaBankApp {
	public static void main(String[] args) {
		try {

			AccountHolder accountHolder = new AccountHolder("Sadiq", "", "Manji", "123456789");
			CDAccount cda = new CDAccount();
			cda.setBalance(50000);
			cda.setInterestRate(3.0);
			accountHolder.addCDAccount(cda);
			System.out.println("Terms :" + cda.getTerm());

			CDAccount acc = new CDAccount();
			acc.setBalance(1000);
			acc.setInterestRate(2.5);
			acc.setTerm(3);
			System.out.println(acc.getTerm() + " " + acc.getBalance() + " " + acc.getInterestRate());

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

}