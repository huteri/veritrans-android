package com.midtrans.sdk.ui.utils;

import android.content.Context;

import com.midtrans.sdk.ui.R;
import com.midtrans.sdk.ui.models.PaymentMethodModel;

import static com.midtrans.sdk.ui.constants.PaymentType.BANK_TRANSFER;
import static com.midtrans.sdk.ui.constants.PaymentType.BCA_KLIKPAY;
import static com.midtrans.sdk.ui.constants.PaymentType.BCA_VA;
import static com.midtrans.sdk.ui.constants.PaymentType.BRI_EPAY;
import static com.midtrans.sdk.ui.constants.PaymentType.CIMB_CLICKS;
import static com.midtrans.sdk.ui.constants.PaymentType.CREDIT_CARD;
import static com.midtrans.sdk.ui.constants.PaymentType.E_CHANNEL;
import static com.midtrans.sdk.ui.constants.PaymentType.GCI;
import static com.midtrans.sdk.ui.constants.PaymentType.INDOMARET;
import static com.midtrans.sdk.ui.constants.PaymentType.INDOSAT_DOMPETKU;
import static com.midtrans.sdk.ui.constants.PaymentType.KIOSON;
import static com.midtrans.sdk.ui.constants.PaymentType.KLIK_BCA;
import static com.midtrans.sdk.ui.constants.PaymentType.MANDIRI_CLICKPAY;
import static com.midtrans.sdk.ui.constants.PaymentType.MANDIRI_ECASH;
import static com.midtrans.sdk.ui.constants.PaymentType.OTHER_VA;
import static com.midtrans.sdk.ui.constants.PaymentType.PERMATA_VA;
import static com.midtrans.sdk.ui.constants.PaymentType.TELKOMSEL_CASH;
import static com.midtrans.sdk.ui.constants.PaymentType.XL_TUNAI;

/**
 * Created by ziahaqi on 2/21/17.
 */

public class PaymentMethodHelper {


    public static PaymentMethodModel createPaymentMethodModel(Context context, String paymentType) {
        PaymentMethodModel paymentMethodModel = null;
        switch (paymentType) {
            case CREDIT_CARD:
                paymentMethodModel = new PaymentMethodModel(context.getString(R.string.payment_method_credit_card), context.getString(R.string.payment_method_description_credit_card), R.mipmap.ic_credit, 1);
                break;
            case BANK_TRANSFER:
                paymentMethodModel = new PaymentMethodModel(context.getString(R.string.payment_method_bank_transfer), context.getString(R.string.payment_method_description_bank_transfer), R.mipmap.ic_atm, 2);
                break;
            case BCA_KLIKPAY:
                paymentMethodModel = new PaymentMethodModel(context.getString(R.string.payment_method_bca_klikpay), context.getString(R.string.payment_method_description_bca_klikpay), R.mipmap.ic_klikpay, 3);
                break;
            case KLIK_BCA:
                paymentMethodModel = new PaymentMethodModel(context.getString(R.string.payment_method_klik_bca), context.getString(R.string.payment_method_description_klik_bca), R.mipmap.ic_klikpay, 4);
                break;
            case BRI_EPAY:
                paymentMethodModel = new PaymentMethodModel(context.getString(R.string.payment_method_bri_epay), context.getString(R.string.payment_method_description_epay_bri), R.mipmap.ic_epay, 5);
                break;
            case CIMB_CLICKS:
                paymentMethodModel = new PaymentMethodModel(context.getString(R.string.payment_method_cimb_clicks), context.getString(R.string.payment_method_description_cimb_clicks), R.mipmap.ic_cimb, 6);
                break;
            case MANDIRI_CLICKPAY:
                paymentMethodModel = new PaymentMethodModel(context.getString(R.string.payment_method_mandiri_clickpay), context.getString(R.string.payment_method_description_mandiri_clickpay), R.mipmap.ic_mandiri_clickpay, 7);
                break;
            case INDOMARET:
                paymentMethodModel = new PaymentMethodModel(context.getString(R.string.payment_method_indomaret), context.getString(R.string.payment_method_description_indomaret), R.mipmap.ic_indomaret, 8);
                break;
            case KIOSON:
                paymentMethodModel = new PaymentMethodModel(context.getString(R.string.payment_method_kioson), context.getString(R.string.payment_method_description_kioson), R.mipmap.ic_kioson, 9);
                break;
            case TELKOMSEL_CASH:
                paymentMethodModel = new PaymentMethodModel(context.getString(R.string.payment_method_telkomsel_cash), context.getString(R.string.payment_method_description_telkomsel_cash), R.mipmap.ic_tcash, 10);
                break;
            case MANDIRI_ECASH:
                paymentMethodModel = new PaymentMethodModel(context.getString(R.string.payment_method_mandiri_ecash), context.getString(R.string.payment_method_description_mandiri_ecash), R.mipmap.ic_mandiri_ecash, 11);
                break;
            case INDOSAT_DOMPETKU:
                paymentMethodModel = new PaymentMethodModel(context.getString(R.string.payment_method_indosat_dompetku), context.getString(R.string.payment_method_description_indosat_dompetku), R.mipmap.ic_indosat_dompetku, 12);
                break;
            case XL_TUNAI:
                paymentMethodModel = new PaymentMethodModel(context.getString(R.string.payment_method_xl_tunai), context.getString(R.string.payment_method_description_xl_tunai), R.mipmap.ic_xl_tunai, 13);
                break;
            case GCI:
                paymentMethodModel = new PaymentMethodModel(context.getString(R.string.payment_method_gci), context.getString(R.string.payment_method_description_gci), R.mipmap.ic_gci, 14);
                break;
        }

        return paymentMethodModel;
    }

    public static PaymentMethodModel createBankTransferPaymentMethod(Context context, String type) {
        PaymentMethodModel paymentMethodModel = null;
        switch (type) {
            case PERMATA_VA:
                break;
            case BCA_VA:

                break;
            case E_CHANNEL:

                break;
            case OTHER_VA:

                break;
        }
        return null;
    }
}