package com.meritamerica.assignment5.Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import com.meritamerica.exceptions.*;

public class MeritBank {
	private static long accIndex = 0;
	private static AccountHolder[] accountHolders = new AccountHolder[10];
	private static CDOffering[] CDOfferings = new CDOffering[0];

	private static int numbOfAccountHolder = 0;
	public static FraudQueue fraudQueue = new FraudQueue();

	public static void addAccountHolder(AccountHolder accountHolder) {
		MeritBank.numbOfAccountHolder++;

		if (MeritBank.numbOfAccountHolder >= MeritBank.accountHolders.length) {
			AccountHolder[] accounts = Arrays.copyOf(MeritBank.accountHolders, MeritBank.accountHolders.length * 2);
			MeritBank.accountHolders = accounts;
		}

		MeritBank.accountHolders[MeritBank.numbOfAccountHolder - 1] = accountHolder;

	}

	public static void addCDOffering(CDOffering offering) {
		CDOffering[] offerings = Arrays.copyOf(MeritBank.CDOfferings, MeritBank.CDOfferings.length + 1);
		offerings[offerings.length - 1] = offering;
		MeritBank.CDOfferings = offerings;
	}

	public static AccountHolder getAccountHolder(long id) {
		for (AccountHolder account : MeritBank.accountHolders) {
			if (account == null) {
				return null;
			}
			if (account.getId() == id) {
				return account;
			}
		}

		return null;

	}

	public static BankAccount findAccount(long ID) {
		if (accountHolders != null) {
			for (int i = 0; i < accountHolders.length; i++) {
				if (accountHolders[i] == null) {
					break;
				}
				BankAccount acc = accountHolders[i].findAccount(ID);
				if (acc != null) {
					return acc;
				}
			}
		}

		return null;
	}

	public static String formatDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.format(date);
	}

	public static String decimalFormat(double numb) {
		DecimalFormat df = new DecimalFormat("#.####");
		return df.format(numb);
	}

	public static String formatNumber(double d) {
		if (d == (int) d)
			return String.format("%d", (int) d);
		else
			return String.format("%s", d);
	}


	public static boolean writeToFile(String fileName) {
		StringBuilder data = new StringBuilder();

		data.append(Long.toString(MeritBank.getNextAccountNumber()) + "\n");

		data.append(Integer.toString(MeritBank.CDOfferings.length) + "\n");

		for (CDOffering offering : CDOfferings) {
			data.append(offering.getTerm() + "," + offering.getInterestRate() + "\n");
		}

		data.append(Integer.toString(MeritBank.accountHolders.length) + "\n");

		for (AccountHolder accountHolder : MeritBank.accountHolders) {
			data.append(accountHolder.getFirstName() + "," + accountHolder.getMiddleName() + ","
					+ accountHolder.getLastName() + "," + accountHolder.getSSN() + "\n");
			data.append(MeritBank.addCheckingData(accountHolder));
			data.append(MeritBank.addSavingData(accountHolder));
			data.append(MeritBank.addCDData(accountHolder));
		}

		try {
			FileWriter writer = new FileWriter(fileName);
			writer.write(data.toString());
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public static boolean readFromFile(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line = "";
			int numOfCDOfferings;
			int numOfAccountHolders;
			int numOfCheckings;
			int numbOfSavings;
			int numbOfCDAccounts;
			line = reader.readLine();
			MeritBank.setNextAccountNumber(Integer.parseInt(line));

			line = reader.readLine();
			numOfCDOfferings = Integer.parseInt(line);

			CDOfferings = new CDOffering[numOfCDOfferings];

			for (int i = 0; i < numOfCDOfferings; i++) {
				line = reader.readLine().trim(); 
				CDOfferings[i] = CDOffering.readFromString(line);
			}

			
			line = reader.readLine();
			numOfAccountHolders = Integer.parseInt(line);

			accountHolders = new AccountHolder[numOfAccountHolders];

			for (int j = 0; j < numOfAccountHolders; j++) {
				try {
					line = reader.readLine();
					
					AccountHolder acc = AccountHolder.readFromString(line);
					
					line = reader.readLine();
					numOfCheckings = Integer.parseInt(line);

					acc.createCheckingArray(numOfCheckings);

					for (int x = 0; x < numOfCheckings; x++) {
						CheckingAccount checkAcc = CheckingAccount.readFromString(reader.readLine());
						
						MeritBank.readTransactions(reader, checkAcc);

						acc.addCheckingAccount(checkAcc);

					}
					
					line = reader.readLine();
					numbOfSavings = Integer.parseInt(line);

					acc.createSavingArray(numbOfSavings);

					for (int y = 0; y < numbOfSavings; y++) {
						SavingsAccount savingAcc = SavingsAccount.readFromString(reader.readLine());

						
						MeritBank.readTransactions(reader, savingAcc);

						acc.addSavingsAccount(savingAcc);
					}

					numbOfCDAccounts = Integer.parseInt(reader.readLine());

					acc.createCDAccounts(numbOfCDAccounts);

					for (int z = 0; z < numbOfCDAccounts; z++) {
						CDAccount CDOAcc = CDAccount.readFromString(reader.readLine());

						
						MeritBank.readTransactions(reader, CDOAcc);

						acc.addCDAccount(CDOAcc);
					}

					
					accountHolders[j] = acc;

				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("something worng");
					return false;
				}
			}

			
			MeritBank.readFraudQueue(reader);

			reader.close();
		} catch (Exception e) {
			System.out.println("Exception");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private static void readFraudQueue(BufferedReader reader) throws IOException, ParseException {
		int pendingNum = Integer.parseInt(reader.readLine());

		for (int i = 0; i < pendingNum; i++) {
			MeritBank.fraudQueue.addTransaction(readTransactionType(reader.readLine()));
		}
	}

	private static Transaction readTransactionType(String line) throws ParseException {
		String[] datas = line.split(",");

		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		int sourceID = Integer.parseInt(datas[0]);
		int targetID = Integer.parseInt(datas[1]);
		BankAccount targetAcc = MeritBank.findAccount(targetID);
		double amount = Integer.parseInt(datas[2]);
		Date date = formatter.parse(datas[3]);


		if (sourceID != -1) {
			if (amount >= 0) {
				return new DepositTransaction(targetAcc, amount, date);
			} else {
				return new WithdrawTransaction(targetAcc, amount, date);
			}
		} else {

			BankAccount sourceAcc = MeritBank.findAccount(sourceID);
			return new TransferTransaction(sourceAcc, targetAcc, amount, date);
		}
	}

	private static void readTransactions(BufferedReader reader, BankAccount acc) throws IOException, ParseException,
			ExceedsFraudSuspicionLimitException, NegativeAmountException, ExceedsAvailableBalanceException {
		int numOfTransaction = Integer.valueOf(reader.readLine()); // number of transactions

		for (int i = 0; i < numOfTransaction; i++) {
			String line = reader.readLine();
			String[] datas = line.split(",");


			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

			int sourceID = Integer.parseInt(datas[0]);
			int targetID = Integer.parseInt(datas[1]);
			double amount = Double.parseDouble(datas[2]);
			Date date = formatter.parse(datas[3]);


			if (sourceID != -1) {
				if (amount >= 0) {
					acc.addTransaction(new DepositTransaction(acc, amount, date));
				} else {
					acc.addTransaction(new WithdrawTransaction(acc, amount, date));
				}
			} else {

				BankAccount sourceAcc = MeritBank.findAccount(sourceID);
				acc.addTransaction(new TransferTransaction(sourceAcc, acc, amount, date));
			}
		}
	}

	private static String addSavingData(AccountHolder acc) {
		StringBuilder data = new StringBuilder();
		int numbOfSavings = 0;
		SavingsAccount[] savings = acc.getSavingsAccounts();

		for (int i = 0; i < savings.length; i++) {
			if (savings[i] == null) {
				break;
			}


			numbOfSavings++;

			data.append(savings[i].writeToString() + "\n");
		}

		return numbOfSavings + "\n" + data.toString();
	}


	public static AccountHolder[] sortAccountHolders() {
		AccountHolder[] accountHolder = MeritBank.accountHolders;

		int n = accountHolder.length;
		for (int i = 0; i < n - 1; i++) {
			for (int j = 0; j < n - i - 1; j++)
				if (accountHolder[j].compareTo(accountHolder[j + 1]) > 0) {
					AccountHolder temp = accountHolder[j];
					accountHolder[j] = accountHolder[j + 1];
					accountHolder[j + 1] = temp;
				}
		}

		return accountHolder;
	}

	private static String addCDData(AccountHolder acc) {
		StringBuilder data = new StringBuilder();
		int numbOfCDs = 0;
		CDAccount[] cds = acc.getCDAccounts();

		for (int i = 0; i < cds.length; i++) {
			if (cds[i] == null) {
				break;
			}


			numbOfCDs++;

			data.append(cds[i].writeToString() + "\n");
		}

		return numbOfCDs + "\n" + data.toString();
	}


	private static String addCheckingData(AccountHolder acc) {
		StringBuilder data = new StringBuilder();
		int numbOfCheckings = 0;
		CheckingAccount[] checkings = acc.getCheckingAccounts();

		for (int i = 0; i < checkings.length; i++) {
			if (checkings[i] == null) {
				break;
			}

			numbOfCheckings++;

			data.append(checkings[i].writeToString() + "\n");
		}

		return numbOfCheckings + "\n" + data.toString();
	}

	public static AccountHolder[] getAccountHolders() {
		AccountHolder[] accounts = Arrays.copyOf(MeritBank.accountHolders, MeritBank.numbOfAccountHolder);
		return accounts;
	}

	public static CDOffering[] getCDOfferings() {
		return CDOfferings;
	}

	public static CDOffering getBestCDOffering(double depositAmount) {
		double highestYield = 0;
		double tempYield = 0;
		int bestIndex = 0; 
		if (MeritBank.CDOfferings != null) {
			for (int i = 0; i < MeritBank.CDOfferings.length; i++) {
				tempYield = MeritBank.futureValue(depositAmount, CDOfferings[i].getInterestRate(),
						CDOfferings[i].getTerm());
				if (tempYield > highestYield) {
					highestYield = tempYield;
					bestIndex = i;
				}
			}

			return CDOfferings[bestIndex];
		} else {
			return null;
		}
	}

	public static CDOffering getSecondBestCDOffering(double depositAmount) {

		double highestYield = 0;
		int secondBestI = 0; 
		int bestI = 0;
		double secondBestYield = 0;
		double tempYield = 0;

		if (MeritBank.CDOfferings != null) {
			for (int i = 0; i < MeritBank.CDOfferings.length; i++) {
				tempYield = MeritBank.futureValue(depositAmount, CDOfferings[i].getInterestRate(),
						CDOfferings[i].getTerm());
				if (tempYield > highestYield) {

					secondBestI = bestI;
					secondBestYield = highestYield;

					highestYield = tempYield;
					bestI = i;

				}
			}

			return CDOfferings[secondBestI];
		} else {
			return null;
		}
	}

	public static void clearCDOfferings() {
		MeritBank.CDOfferings = null;
	}

	public static void setCDOfferings(CDOffering[] offerings) {
		CDOfferings = offerings;
	}

	public static long getNextAccountNumber() {
		// get back later
		MeritBank.accIndex++;
		return accIndex;
	}

	public static void setNextAccountNumber(long nextAccountNumb) {
		MeritBank.accIndex = nextAccountNumb - 1;
	}

	public static double totalBalances() {
		double total = 0.0;

		for (int i = 0; i < MeritBank.numbOfAccountHolder; i++) {
			total += MeritBank.accountHolders[i].getCheckingBalance()
					+ MeritBank.accountHolders[i].getCheckingBalance();
		}

		return total;
	}

	public static double recursionFutureValue(double amount, int years, double interestRate) {
		if (years == 0) {
			return amount;
		} else {
			return amount * (1 + interestRate) * recursionFutureValue(1, years - 1, interestRate);
		}

	}

	public static double futureValue(double presentValue, double interestRate, int term) {
		double futureVal = presentValue * Math.pow(1 + interestRate, term);

		return futureVal;
	}


	public static boolean processTransaction(Transaction transaction)
			throws NegativeAmountException, ExceedsFraudSuspicionLimitException, ExceedsAvailableBalanceException {
		double amount = transaction.getAmount();
		BankAccount source = transaction.getSourceAccount();
		BankAccount target = transaction.getTargetAccount();

		if (Math.abs(transaction.getAmount()) > 1000) {
			MeritBank.fraudQueue.addTransaction(transaction);
			throw new ExceedsFraudSuspicionLimitException();
		}


		if (transaction.getAmount() < 0) {
			throw new NegativeAmountException();
		}

		if (transaction instanceof DepositTransaction) {


			target.deposit(amount);

			target.addTransaction(transaction);
		} else if (transaction instanceof WithdrawTransaction) {
			if (transaction.getAmount() + transaction.getTargetAccount().getBalance() < 0) {
				throw new ExceedsAvailableBalanceException();
			}

			target.withdraw(amount);

			transaction.getTargetAccount().addTransaction(transaction);
		} else if (transaction instanceof TransferTransaction) {

			if (source.getBalance() - amount < 0) {
				throw new ExceedsAvailableBalanceException();
			}


			source.withdraw(amount);

			target.deposit(amount);

			transaction.getSourceAccount().addTransaction(transaction);
			transaction.getTargetAccount().addTransaction(transaction);
		}

		return true;
	}
}
