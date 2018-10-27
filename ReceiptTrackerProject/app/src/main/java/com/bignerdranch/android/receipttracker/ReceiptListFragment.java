package com.bignerdranch.android.receipttracker;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class ReceiptListFragment extends Fragment {
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private RecyclerView mReceiptRecyclerView;
    private ReceiptAdapter mAdapter;
    private boolean mSubtitleVisible;
    private Callbacks mCallbacks;

    /**
     * required interface for hosting activities
     *
     */
    public interface Callbacks {
        void onReceiptSelected(Receipt receipt);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receipt_list, container, false);
        mReceiptRecyclerView = (RecyclerView) view.findViewById(R.id.receipt_receipt_view);
        mReceiptRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();
        return view;

    }
    @Override
    public void onResume() {
        super.onResume();
        updateUI();

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_receipt_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
        }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_receipt:
                Receipt receipt = new Receipt();
                ReceiptLab.get(getActivity()).addReceipt(receipt);
                updateUI();
                mCallbacks.onReceiptSelected(receipt);
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    private void updateSubtitle(){
        ReceiptLab receiptLab = ReceiptLab.get(getActivity());
        int receiptCount = receiptLab.getReceipts().size();
        String subtitle = getString(R.string.subtitle_format, receiptCount);
        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    public void updateUI() {
        ReceiptLab receiptLab = ReceiptLab.get(getActivity());
        List<Receipt> receipts = receiptLab.getReceipts();
        if (mAdapter == null) {
            mAdapter = new ReceiptAdapter(receipts);
            mReceiptRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setReceipts(receipts);
            mAdapter.notifyDataSetChanged();
        }
        updateSubtitle();

    }

    private class ReceiptHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{


        private Receipt mShop;
        private TextView mTitleTextView;
        private TextView mShopTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;

        public ReceiptHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);



            mTitleTextView = (TextView)
                    itemView.findViewById(R.id.list_item_receipt_title_text_view);
            mShopTextView = (TextView)
                    itemView.findViewById(R.id.list_item_receipt_shop_text_view);
            mDateTextView = (TextView)
                    itemView.findViewById(R.id.list_item_receipt_date_text_view);
            mSolvedCheckBox = (CheckBox)
                    itemView.findViewById(R.id.list_item_receipt_solved_check_box);

        }
        public void bindReceipt(Receipt receipt) {
            mShop = receipt;
            mTitleTextView.setText(mShop.getTitle());
            mShopTextView.setText(mShop.getShop());
            mDateTextView.setText(mShop.getDate().toString());
            mSolvedCheckBox.setChecked(mShop.isFiled());
        }

        @Override
        public void onClick(View v) {
            mCallbacks.onReceiptSelected(mShop);
        }
    }


    private class ReceiptAdapter extends RecyclerView.Adapter<ReceiptHolder> {
        private List<Receipt> mReceipts;

        public ReceiptAdapter(List<Receipt> receipts) {
            mReceipts = receipts;
        }

        @Override
        public ReceiptHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_receipt, parent, false);
            return new ReceiptHolder(view);
        }

        @Override
        public void onBindViewHolder(ReceiptHolder holder, int position) {
            Receipt receipt = mReceipts.get(position);
            holder.bindReceipt(receipt);
        }

        @Override
        public int getItemCount() {
            return mReceipts.size();
        }
        public void setReceipts(List<Receipt> receipts) {
            mReceipts = receipts;
        }

    }

}


