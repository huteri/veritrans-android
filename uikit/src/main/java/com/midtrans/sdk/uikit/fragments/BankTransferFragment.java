package com.midtrans.sdk.uikit.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.midtrans.sdk.corekit.core.LocalDataHandler;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.models.UserDetail;
import com.midtrans.sdk.uikit.R;
import com.midtrans.sdk.uikit.adapters.InstructionFragmentPagerAdapter;
import com.midtrans.sdk.uikit.constants.AnalyticsEventName;

import java.lang.reflect.Field;

/**
 * It displays payment related instructions on the screen. Created by shivam on 10/27/15.
 */
public class BankTransferFragment extends Fragment {
    public static final String DOWNLOAD_URL = "url";
    public static final String BANK = "bank";
    public static final String TYPE_BCA = "bank.bca";
    public static final String TYPE_PERMATA = "bank.permata";
    public static final String TYPE_MANDIRI = "bank.mandiri";
    public static final String TYPE_MANDIRI_BILL = "bank.mandiri.bill";
    public static final String TYPE_ALL_BANK = "bank.others";
    public static final String PAGE = "page";
    public static final int KLIKBCA_PAGE = 1;
    private static final int PAGE_MARGIN = 20;
    private int POSITION = -1;

    private ViewPager instructionViewPager = null;
    private TabLayout instructionTab = null;
    private TextInputLayout mTextInputEmailId = null;
    private AppCompatEditText mEditTextEmailId = null;
    private UserDetail userDetail;

    public static BankTransferFragment newInstance(String bank, int position) {
        BankTransferFragment bankTransferFragment = new BankTransferFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PAGE, position);
        bundle.putString(BANK, bank);
        bankTransferFragment.setArguments(bundle);
        return bankTransferFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bank_transfer, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initializeViews(view);
        setUpViewPager();
        setUpTabLayout();
    }

    /**
     * initializes view and adds click listener for it.
     *
     * @param view view that needed to be initialized
     */
    private void initializeViews(View view) {
        instructionViewPager = (ViewPager) view.findViewById(R.id.tab_view_pager);
        instructionTab = (TabLayout) view.findViewById(R.id.tab_instructions);
        mEditTextEmailId = (AppCompatEditText) view.findViewById(R.id.et_email);
        mTextInputEmailId = (TextInputLayout) view.findViewById(R.id.email_til);
        try {
            userDetail = LocalDataHandler.readObject(getString(R.string.user_details), UserDetail.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mEditTextEmailId.setText(userDetail.getEmail());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        MidtransSDK midtransSDK = MidtransSDK.getInstance();
        if (midtransSDK != null && midtransSDK.getColorTheme() != null) {
            if (midtransSDK.getColorTheme().getSecondaryColor() != 0) {
                // Set color filter in edit text
                try {
                    Field fDefaultTextColor = TextInputLayout.class.getDeclaredField("mDefaultTextColor");
                    fDefaultTextColor.setAccessible(true);
                    fDefaultTextColor.set(mTextInputEmailId, new ColorStateList(new int[][]{{0}}, new int[]{midtransSDK.getColorTheme().getSecondaryColor()}));

                    Field fFocusedTextColor = TextInputLayout.class.getDeclaredField("mFocusedTextColor");
                    fFocusedTextColor.setAccessible(true);
                    fFocusedTextColor.set(mTextInputEmailId, new ColorStateList(new int[][]{{0}}, new int[]{midtransSDK.getColorTheme().getSecondaryColor()}));

                    mEditTextEmailId.setSupportBackgroundTintList(new ColorStateList(new int[][]{{0}}, new int[]{midtransSDK.getColorTheme().getSecondaryColor()}));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (midtransSDK.getColorTheme().getPrimaryColor() != 0) {
                instructionTab.setSelectedTabIndicatorColor(midtransSDK.getColorTheme().getPrimaryColor());
            }
        }
    }


    /**
     * created to give access to email id field from {
     * .BankTransferActivity}.
     *
     * @return email identifier
     */
    public String getEmailId() {
        if (mEditTextEmailId != null) {
            return mEditTextEmailId.getText().toString();
        } else {
            return null;
        }
    }

    private void setUpViewPager() {
        instructionViewPager.setPageMargin(PAGE_MARGIN);
        int pageNumber;
        String bankInstruction = getArguments().getString(BANK);
        switch (bankInstruction) {
            case TYPE_BCA:
                pageNumber = 3;
                POSITION = getArguments().getInt(PAGE, -1);

                if (POSITION == KLIKBCA_PAGE) {
                    //track page bca va overview
                    MidtransSDK.getInstance().trackEvent(AnalyticsEventName.PAGE_BCA_KLIKBCA_OVERVIEW);
                } else {
                    //track page bca va overview
                    MidtransSDK.getInstance().trackEvent(AnalyticsEventName.PAGE_BCA_VA_OVERVIEW);
                }
                break;
            case TYPE_PERMATA:
                pageNumber = 2;

                //track page permata va overview
                MidtransSDK.getInstance().trackEvent(AnalyticsEventName.PAGE_PERMATA_VA_OVERVIEW);
                break;
            case TYPE_MANDIRI:
                pageNumber = 2;
                break;
            case TYPE_MANDIRI_BILL:
                pageNumber = 2;

                //track page mandiri bill overview
                MidtransSDK.getInstance().trackEvent(AnalyticsEventName.PAGE_MANDIRI_BILL_OVERVIEW);
                break;
            case TYPE_ALL_BANK:
                pageNumber = 3;

                //track page other bank va overview
                MidtransSDK.getInstance().trackEvent(AnalyticsEventName.PAGE_OTHER_BANK_VA_OVERVIEW);
                break;
            default:
                pageNumber = 0;
                break;
        }
        InstructionFragmentPagerAdapter adapter = new InstructionFragmentPagerAdapter(getContext(), bankInstruction, getChildFragmentManager(), pageNumber);
        instructionViewPager.setAdapter(adapter);
        if (POSITION > -1) {
            instructionViewPager.setCurrentItem(POSITION);
        }
    }

    private void setUpTabLayout() {
        instructionTab.setupWithViewPager(instructionViewPager);
        instructionTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                instructionViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
}