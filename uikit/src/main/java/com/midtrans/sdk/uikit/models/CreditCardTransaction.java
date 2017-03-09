package com.midtrans.sdk.uikit.models;

import android.text.TextUtils;

import com.midtrans.sdk.corekit.models.BankType;
import com.midtrans.sdk.corekit.models.snap.BankBinsResponse;
import com.midtrans.sdk.corekit.models.snap.CreditCard;

import java.util.ArrayList;

/**
 * Created by ziahaqi on 1/19/17.
 */

public class CreditCardTransaction {

    private static final String BANK_OFFLINE = "offline";

    private CreditCardInstallment cardInstallment;
    private CreditCard creditCard;
    private boolean whiteListBinsAvailable;
    private ArrayList<BankBinsResponse> bankBins;
    private boolean bankBinsAvailable;

    public CreditCardTransaction() {
        bankBins = new ArrayList<>();
        cardInstallment = new CreditCardInstallment();
        creditCard = new CreditCard();
    }

    public void setProperties(CreditCard creditCard, ArrayList<BankBinsResponse> bankBins) {
        if (creditCard != null) {
            this.creditCard = creditCard;
            cardInstallment.setInstallment(creditCard.getInstallment());
        }

        if (bankBins != null && !bankBins.isEmpty()) {
            this.bankBins.clear();
            this.bankBins = bankBins;
        }
        init();
    }

    private void init() {
        ArrayList<String> whitleListBins = creditCard.getWhitelistBins();
        this.whiteListBinsAvailable = whitleListBins != null && !whitleListBins.isEmpty();
        this.bankBinsAvailable = bankBins != null && !bankBins.isEmpty();
    }


    public boolean isWhiteListBinsAvailable() {
        return whiteListBinsAvailable;
    }

    public boolean isInstallmentAvailable() {
        return cardInstallment.isInstallmentAvailable();
    }

    public boolean isBankBinsAvailable() {
        return bankBinsAvailable;
    }

    public void setBankBins(ArrayList<BankBinsResponse> bankBins) {
        this.bankBins.clear();
        this.bankBins.addAll(bankBins);
    }

    public boolean isInWhiteList(String cardBin) {
        return isWhiteListBinsAvailable() && creditCard.getWhitelistBins().contains(cardBin);
    }


    public ArrayList<Integer> getInstallmentTerms(String cardBin) {

        String bank = getBankByBin(cardBin);
        if (TextUtils.isEmpty(bank)) {
            bank = BANK_OFFLINE;
        }

        ArrayList<Integer> installmentTerms = cardInstallment.getTerms(bank);
        if (installmentTerms != null) {
            return installmentTerms;
        }

        return null;
    }

    /**
     * @param cardBin get bank by card bin
     * @return string Bank
     */
    public String getBankByBin(String cardBin) {
        for (BankBinsResponse savedBankBin : bankBins) {
            if (savedBankBin.getBins() != null && !savedBankBin.getBins().isEmpty()) {
                String bankBin = findBankByCardBin(savedBankBin, cardBin);
                if (bankBin != null) {
                    return bankBin;
                }
            }
        }
        return null;
    }

    public boolean isMandiriCardDebit(String cardBin) {
        if (getMandiriDebitResponse() != null) {
            String bankBin = findBankByCardBin(getMandiriDebitResponse(), cardBin);
            return bankBin != null;
        }
        return false;
    }

    private BankBinsResponse getMandiriDebitResponse() {
        for (BankBinsResponse bankBinsResponse : bankBins) {
            if (bankBinsResponse.getBank().equals(BankType.MANDIRI_DEBIT)) {
                return bankBinsResponse;
            }
        }
        return null;
    }

    private String findBankByCardBin(BankBinsResponse savedBankBin, String cardBin) {
        for (String savedBin : savedBankBin.getBins()) {
            if (savedBin.contains(cardBin)) {
                return savedBankBin.getBank();
            }
        }
        return null;
    }

    /**
     * @param currentPosition index position
     * @return term of installment
     */
    public Integer getInstallmentTerm(int currentPosition) {
        return cardInstallment.getTermByPosition(currentPosition);
    }

    public void setInstallment(int termPosition) {
        cardInstallment.setTermSelected(termPosition);
    }

    public boolean isInstallmentValid() {
        return cardInstallment.isInstallmentValid();
    }

    public void setInstallmentAvailableStatus(boolean installmentStatus) {
        cardInstallment.setAvailableStatus(installmentStatus);
    }

    public int getInstallmentTermSelected() {
        return cardInstallment.getTermSelected();
    }

    public String getInstallmentBankSelected() {
        return cardInstallment.getBankSelected();
    }
}
