package id.co.veritrans.sdk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;

import id.co.veritrans.sdk.R;
import id.co.veritrans.sdk.callbacks.TransactionCallback;
import id.co.veritrans.sdk.core.Constants;
import id.co.veritrans.sdk.core.Logger;
import id.co.veritrans.sdk.core.SdkUtil;
import id.co.veritrans.sdk.core.StorageDataHandler;
import id.co.veritrans.sdk.core.VeritransSDK;
import id.co.veritrans.sdk.fragments.BankTransactionStatusFragment;
import id.co.veritrans.sdk.fragments.BankTransferFragment;
import id.co.veritrans.sdk.fragments.BankTransferPaymentFragment;
import id.co.veritrans.sdk.fragments.MandiriBillPayFragment;
import id.co.veritrans.sdk.models.BankTransfer;
import id.co.veritrans.sdk.models.BillingAddress;
import id.co.veritrans.sdk.models.CustomerDetails;
import id.co.veritrans.sdk.models.MandiriBillPayTransferModel;
import id.co.veritrans.sdk.models.PermataBankTransfer;
import id.co.veritrans.sdk.models.ShippingAddress;
import id.co.veritrans.sdk.models.TransactionDetails;
import id.co.veritrans.sdk.models.TransactionResponse;
import id.co.veritrans.sdk.models.UserAddress;
import id.co.veritrans.sdk.models.UserDetail;
import id.co.veritrans.sdk.widgets.TextViewFont;

/**
 * Created by shivam on 10/26/15.
 */
public class BankTransferActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String HOME_FRAGMENT = "home";
    public static final String PAYMENT_FRAGMENT = "payment";
    public static final String STATUS_FRAGMENT = "transaction_status";
    public static final String SOMETHING_WENT_WRONG = "Something went wrong";
    public String currentFragment = "home";

    private TextViewFont mTextViewOrderId = null;
    private TextViewFont mTextViewAmount = null;
    private Button mButtonConfirmPayment = null;
    private AppBarLayout mAppBarLayout = null;
    private TextViewFont mTextViewTitle = null;

    private VeritransSDK mVeritransSDK = null;
    private Toolbar mToolbar = null;

    private ArrayList<BillingAddress> mBillingAddressArrayList = new ArrayList<>();
    private ArrayList<ShippingAddress> mShippingAddressArrayList = new ArrayList<>();
    private CustomerDetails mCustomerDetails = null;
    private BankTransferFragment bankTransferFragment = null;
    private TransactionResponse mTransactionResponse = null;


    private int position = Constants.PAYMENT_METHOD_MANDIRI_BILL_PAYMENT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_transfer);

        mVeritransSDK = VeritransSDK.getVeritransSDK();


        Intent data = getIntent();
        if( data != null) {
            position = data.getIntExtra(Constants.POSITION, Constants.PAYMENT_METHOD_MANDIRI_BILL_PAYMENT);
        }else {
            SdkUtil.showSnackbar(BankTransferActivity.this, Constants.ERROR_SOMETHING_WENT_WRONG);
            finish();
        }

        initializeView();
        bindDataToView();

        setUpHomeFragment();
        getUserDetails();

    }

    private void setUpHomeFragment() {
        // setup home fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        bankTransferFragment = new BankTransferFragment();

        fragmentTransaction.add(R.id.bank_transfer_container,
                bankTransferFragment, HOME_FRAGMENT);
        fragmentTransaction.commit();

        currentFragment = HOME_FRAGMENT;
    }

    @Override
    public void onBackPressed() {

        //enable this code if wants to travers backward in fragment back stack

        /*FragmentManager fragmentManager = getSupportFragmentManager();
        int count = fragmentManager.getBackStackEntryCount();
        if (count > 1) {
            fragmentManager.popBackStack();
        } else {
            finish();
        }*/

        finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return false;
    }


    private void initializeView() {

        mTextViewOrderId = (TextViewFont) findViewById(R.id.text_order_id);
        mTextViewAmount = (TextViewFont) findViewById(R.id.text_amount);
        mTextViewTitle = (TextViewFont) findViewById(R.id.text_title);
        mButtonConfirmPayment = (Button) findViewById(R.id.btn_confirm_payment);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.main_appbar);
        //setup tool bar
        mToolbar.setTitle(""); // disable default Text
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void bindDataToView() {

        if (mVeritransSDK != null) {

            mTextViewAmount.setText(Constants.CURRENCY_PREFIX + " " + mVeritransSDK.getAmount());
            mTextViewOrderId.setText(" " + mVeritransSDK.getOrderId());
            mButtonConfirmPayment.setTypeface(mVeritransSDK.getTypefaceOpenSansSemiBold());
            mButtonConfirmPayment.setOnClickListener(this);

            if ( position == Constants.PAYMENT_METHOD_MANDIRI_BILL_PAYMENT){
                mTextViewTitle.setText(getResources().getString(R.string.mandiri_bill_payment));
            }else {
                mTextViewTitle.setText(getResources().getString(R.string.bank_transfer));
            }

        }else{
            SdkUtil.showSnackbar(BankTransferActivity.this, Constants.ERROR_SOMETHING_WENT_WRONG);
            Logger.e(BankTransferActivity.class.getSimpleName(), Constants.ERROR_SDK_IS_NOT_INITIALIZED);
            finish();
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_confirm_payment) {

            if (currentFragment.equalsIgnoreCase(HOME_FRAGMENT)) {

                performTrsansaction();

            } else if (currentFragment.equalsIgnoreCase(PAYMENT_FRAGMENT)) {

                mAppBarLayout.setExpanded(true);

                if (mTransactionResponse != null) {
                    setUpTransactionStatusFragment(mTransactionResponse);
                } else {
                    SdkUtil.showSnackbar(BankTransferActivity.this, SOMETHING_WENT_WRONG);
                    onBackPressed();
                }
            } else {
                onBackPressed();
            }

        }
    }


    private void setUpTransactionStatusFragment(final TransactionResponse
                                                        permataBankTransferResponse) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            BankTransactionStatusFragment bankTransactionStatusFragment =
                    BankTransactionStatusFragment.newInstance(permataBankTransferResponse);


            // setup transaction status fragment
            fragmentTransaction.replace(R.id.bank_transfer_container,
                    bankTransactionStatusFragment, STATUS_FRAGMENT);

            fragmentTransaction.addToBackStack(STATUS_FRAGMENT);
            fragmentTransaction.commit();

            currentFragment = STATUS_FRAGMENT;
            mButtonConfirmPayment.setText(R.string.done);

    }


    private void setUpTransactionFragment(final TransactionResponse
                                                  transferResponse) {
        if( transferResponse != null ) {
            // setup transaction fragment
            FragmentManager fragmentManager = getSupportFragmentManager();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            if(position == Constants.PAYMENT_METHOD_MANDIRI_BILL_PAYMENT){

                MandiriBillPayFragment bankTransferPaymentFragment =
                        MandiriBillPayFragment.newInstance(transferResponse);

                fragmentTransaction.replace(R.id.bank_transfer_container,
                        bankTransferPaymentFragment , PAYMENT_FRAGMENT);
            }else {

                BankTransferPaymentFragment bankTransferPaymentFragment =
                        BankTransferPaymentFragment.newInstance(transferResponse);
                fragmentTransaction.replace(R.id.bank_transfer_container,
                        bankTransferPaymentFragment , PAYMENT_FRAGMENT);
            }

            fragmentTransaction.addToBackStack(PAYMENT_FRAGMENT);
            fragmentTransaction.commit();


            currentFragment = PAYMENT_FRAGMENT;
            mButtonConfirmPayment.setText(R.string.complete_payment_at_atm);

        }else {
            SdkUtil.showSnackbar(BankTransferActivity.this, SOMETHING_WENT_WRONG);
            onBackPressed();
        }
    }


    private void getUserDetails() {

        UserDetail userDetail = null;

        try {
            userDetail = (UserDetail) StorageDataHandler.readObject(getApplicationContext(),
                    Constants.USER_DETAILS);

            if (userDetail != null && !TextUtils.isEmpty(userDetail.getUserFullName())) {

                ArrayList<UserAddress> userAddresses = userDetail.getUserAddresses();
                if (userAddresses != null && !userAddresses.isEmpty()) {

                    Logger.i("Found " + userAddresses.size() + " user addresses.");

                    mCustomerDetails = new CustomerDetails();
                    mCustomerDetails.setPhone(userDetail.getPhoneNumber());
                    mCustomerDetails.setFirstName(userDetail.getUserFullName());
                    mCustomerDetails.setLastName(" ");
                    mCustomerDetails.setEmail("");

                    //added email in performTransaction()

                    extractUserAddress(userDetail, userAddresses);
                }

            } else {
                SdkUtil.showSnackbar(BankTransferActivity.this, "User details not available.");
                finish();
            }
        } catch (ClassNotFoundException ex) {
            Logger.e("Error while fetching user details : " + ex.getMessage());
        } catch (IOException ex) {
            Logger.e("Error while fetching user details : " + ex.getMessage());
        }

    }


    private void extractUserAddress(UserDetail userDetail, ArrayList<UserAddress> userAddresses) {

        for (int i = 0; i < userAddresses.size(); i++) {

            UserAddress userAddress = userAddresses.get(i);

            if (userAddress.getAddressType() == Constants.ADDRESS_TYPE_BILLING) {
                BillingAddress billingAddress = new BillingAddress();
                billingAddress.setCity(userAddress.getCity());
                billingAddress.setFirstName(userDetail.getUserFullName());
                billingAddress.setLastName("");
                billingAddress.setPhone(userDetail.getPhoneNumber());
                billingAddress.setCountryCode(userAddress.getCountry());
                billingAddress.setPostalCode(userAddress.getZipcode());
                mBillingAddressArrayList.add(billingAddress);
            } else {

                ShippingAddress shippingAddress = new ShippingAddress();
                shippingAddress.setCity(userAddress.getCity());
                shippingAddress.setFirstName(userDetail.getUserFullName());
                shippingAddress.setLastName("");
                shippingAddress.setPhone(userDetail.getPhoneNumber());
                shippingAddress.setCountryCode(userAddress.getCountry());
                shippingAddress.setPostalCode(userAddress.getZipcode());
                mShippingAddressArrayList.add(shippingAddress);

            }

        }
    }


    private void performTrsansaction() {


        // for sending instruction on email only if email-Id is entered.
        if ( bankTransferFragment != null && !bankTransferFragment.isDetached()) {
            String emailId = bankTransferFragment.getEmailId();
            if (!TextUtils.isEmpty(emailId) && SdkUtil.isEmailValid(emailId)) {
                mCustomerDetails.setEmail(emailId.trim());
            } else if (!TextUtils.isEmpty(emailId) && emailId.trim().length() > 0) {
                SdkUtil.showSnackbar(BankTransferActivity.this, Constants
                        .ERROR_INVALID_EMAIL_ID);
                return;
            }
        }



        final VeritransSDK veritransSDK = VeritransSDK.getVeritransSDK();

        if (veritransSDK != null) {

            //transaction details
            TransactionDetails transactionDetails =
                    new TransactionDetails("" + mVeritransSDK.getAmount(), mVeritransSDK.getOrderId());

            SdkUtil.showProgressDialog(BankTransferActivity.this, false);

            if(position == Constants.PAYMENT_METHOD_PERMATA_VA_BANK_TRANSFER) {
                bankTransferTransaction(veritransSDK, transactionDetails);
            }else {
                mandiriBillPayTransaction(veritransSDK, transactionDetails);
            }


        } else {
            Logger.e(Constants.ERROR_SDK_IS_NOT_INITIALIZED);
            finish();
        }
    }

    private void bankTransferTransaction(VeritransSDK veritransSDK, TransactionDetails
            transactionDetails) {
        // bank name
        BankTransfer bankTransfer = new BankTransfer();
        bankTransfer.setBank("permata");

        final PermataBankTransfer permataBankTransfer =
                new PermataBankTransfer(bankTransfer, transactionDetails, null,
                        mBillingAddressArrayList, mShippingAddressArrayList,
                        mCustomerDetails);

        veritransSDK.paymentUsingPermataBank(BankTransferActivity.this,
                permataBankTransfer, new TransactionCallback() {


                    @Override
                    public void onSuccess(TransactionResponse
                                                  permataBankTransferResponse) {

                        SdkUtil.hideProgressDialog();

                        if (permataBankTransferResponse != null) {
                            mTransactionResponse = permataBankTransferResponse;
                            mAppBarLayout.setExpanded(true);
                            setUpTransactionFragment(permataBankTransferResponse);
                        } else {
                            onBackPressed();
                        }

                    }

                    @Override
                    public void onFailure(String errorMessage) {

                        try {

                            SdkUtil.hideProgressDialog();
                            SdkUtil.showSnackbar(BankTransferActivity.this, "" + errorMessage);

                        } catch (NullPointerException ex) {
                            Logger.e("transaction error is " + errorMessage);
                        }
                    }
                });
    }



    private void mandiriBillPayTransaction(VeritransSDK veritransSDK, TransactionDetails
            transactionDetails) {
        // bank name


        final MandiriBillPayTransferModel mandiriBillPayTransferModel =
                new MandiriBillPayTransferModel(veritransSDK.getBillInfoModel(), transactionDetails, veritransSDK.getItemDetails(),
                        mBillingAddressArrayList, mShippingAddressArrayList,
                        mCustomerDetails);


        veritransSDK.paymentUsingMandiriBillPay(BankTransferActivity.this,
                mandiriBillPayTransferModel, new TransactionCallback() {

                    @Override
                    public void onSuccess(TransactionResponse
                                                  mandiriBillPayTransferResponse) {

                        SdkUtil.hideProgressDialog();

                        if (mandiriBillPayTransferResponse != null) {
                            mTransactionResponse = mandiriBillPayTransferResponse;
                            mAppBarLayout.setExpanded(true);
                            setUpTransactionFragment(mandiriBillPayTransferResponse);
                        } else {
                            onBackPressed();
                        }

                    }

                    @Override
                    public void onFailure(String errorMessage) {

                        try {

                            SdkUtil.hideProgressDialog();
                            SdkUtil.showSnackbar(BankTransferActivity.this, "" + errorMessage);

                        } catch (NullPointerException ex) {
                            Logger.e("transaction error is " + errorMessage);
                        }
                    }
                });
    }

    public int getPosition() {
        return position;
    }
}