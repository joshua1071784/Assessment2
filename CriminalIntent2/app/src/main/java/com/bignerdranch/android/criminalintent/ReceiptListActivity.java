package com.bignerdranch.android.receipttracker;

import android.content.Intent;
import android.support.v4.app.Fragment;

public class ReceiptListActivity extends SingleFragmentActivity
    implements ReceiptListFragment.Callbacks, ReceiptFragment.Callbacks {



    @Override
    protected Fragment createFragment() {
        return new ReceiptListFragment();
    }
    @Override
    protected int getLayoutResId() { return R.layout.activity_masterdetail; }

    @Override
    public void onReceiptSelected(Receipt receipt) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = ReceiptPagerActivity.newIntent(this, receipt.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = ReceiptFragment.newInstance(receipt.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    public void onReceiptUpdated(Receipt receipt) {
        ReceiptListFragment listFragment = (ReceiptListFragment)
                getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
