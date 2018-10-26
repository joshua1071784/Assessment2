package com.bignerdranch.android.receipttracker;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class ReceiptFragment extends Fragment {

    private static final String ARG_RECEIPT_ID = "receipt_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO= 2;

    private com.bignerdranch.android.receipttracker.Receipt mReceipt;
    private File mPhotoFile;
    private EditText mTitleField;
    private EditText mShop;
    private EditText mComments;
    private Button mDateButton;
    private CheckBox mFiledCheckBox;
    private Button mSuspectButton;
    private Button mReportButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private ReceiptFragment.Callbacks mCallbacks;
    private GoogleApiClient mClient;

    /**
     * requiredinterface for hosting activities
     */
    public interface Callbacks {
        void onReceiptUpdated(com.bignerdranch.android.receipttracker.Receipt receipt);
    }

    public static ReceiptFragment newInstance(UUID ReceiptId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_RECEIPT_ID, ReceiptId);

        ReceiptFragment fragment = new ReceiptFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID receiptId = (UUID) getArguments().getSerializable(ARG_RECEIPT_ID);

        mReceipt = com.bignerdranch.android.receipttracker.ReceiptLab.get(getActivity()).getReceipt(receiptId);
        mPhotoFile = com.bignerdranch.android.receipttracker.ReceiptLab.get(getActivity()).getPhotoFile(mReceipt);


    }




    @Override
    public void onPause() {
        super.onPause();

        com.bignerdranch.android.receipttracker.ReceiptLab.get(getActivity())
                .updateReceipt(mReceipt);
        }

        @Override
        public void onDetach() {
            super.onDetach();
            mCallbacks = null;

        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_receipt, container, false);

        mTitleField = (EditText) v.findViewById(R.id.receipt_title);
        mTitleField.setText(mReceipt.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }


            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mReceipt.setTitle(s.toString());
                updateReceipt();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //this one too
            }

        });

        mShop = (EditText) v.findViewById(R.id.shop_name);
        mShop.setText(mReceipt.getShop());
        mShop.addTextChangedListener (new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                //left blank as well
            }
            @Override
            public void onTextChanged (
                    CharSequence s, int start, int before, int count) {
                    mReceipt.setShop (s.toString());
                    updateReceipt();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //this one as well

            }
        });


        mComments = (EditText) v.findViewById(R.id.shop_comments);
        mComments.setText(mReceipt.getComments());
        mComments.addTextChangedListener (new TextWatcher() {
            @Override
            public void beforeTextChanged (
                    CharSequence s, int start, int count, int after) {
                //Left Blank here
            }
            @Override
            public void onTextChanged (
                    CharSequence s, int start, int before, int count) {
                    mReceipt.setComments (s.toString());
                    updateReceipt();
            }
            @Override
                    public void afterTextChanged(Editable s){
                //Also Blank
            }

        });

        mDateButton = (Button) v.findViewById(R.id.receipt_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mReceipt.getDate());
                dialog.setTargetFragment(ReceiptFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });


        mFiledCheckBox = (CheckBox) v.findViewById(R.id.receipt_solved);
        mFiledCheckBox.setChecked(mReceipt.isFiled());
        mFiledCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // set the receipts field property
                mReceipt.setFiled(isChecked);
                updateReceipt();
            }
        });

        mReportButton = (Button) v.findViewById(R.id.receipt_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getReceiptReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.receipt_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });
        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);

        mSuspectButton = (Button) v.findViewById(R.id.receipt_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if (mReceipt.getSuspect() != null) {
            mSuspectButton.setText(mReceipt.getSuspect());
        }
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.receipt_camera);
            final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            boolean canTakePhoto = mPhotoFile != null &&
                    captureImage.resolveActivity(packageManager) != null;
            mPhotoButton.setEnabled(canTakePhoto);

            if (canTakePhoto) {
                Uri uri = Uri.fromFile(mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }
            mPhotoButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
                    }
            });


        mPhotoView = (ImageView) v.findViewById(R.id.receipt_photo);
        updatePhotoView();

        return v;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_receipt, menu);
        MenuItem deleteItem = menu.findItem(R.id.delete_button);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_button:
                com.bignerdranch.android.receipttracker.ReceiptLab.get(getActivity()).deleteReceipt(mReceipt);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(com.bignerdranch.android.receipttracker.DatePickerFragment.EXTRA_DATE);
            mReceipt.setDate(date);
            updateReceipt();
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            // Specify which fields you want the query to return
            //values for.
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME};
            //perform your query - the contractUri is like a "where"
            //clause here
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);
            try {
                // double-check that you actually got results
                if (c.getCount() == 0) {
                    return;
                }

                // Pull out the first column of the first row of data -
                //that is your suspects name
                c.moveToFirst();
                String suspect = c.getString(0);

                //Check this here for error missing data

                updateReceipt();
                mSuspectButton.setText(suspect);
                updateReceipt();
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            updateReceipt();
            updatePhotoView();
        }
    }

    private void updateReceipt() {
        com.bignerdranch.android.receipttracker.ReceiptLab.get(getActivity()).updateReceipt(mReceipt);
        mCallbacks.onReceiptUpdated(mReceipt);

    }


    private void updateDate() {
        mDateButton.setText(mReceipt.getDate().toString());
    }

    private String getReceiptReport() {
        String solvedString = null;
        if (mReceipt.isFiled()) {
            solvedString = getString(R.string.receipt_report_solved);
        } else {
            solvedString = getString(R.string.receipt_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mReceipt.getDate()).toString();
        String suspect = mReceipt.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.receipt_report_no_suspect);
        } else {
            suspect = getString(R.string.receipt_report_suspect, suspect);
        }
        String report = getString(R.string.receipt_report,
                mReceipt.getTitle(), dateString, solvedString, suspect);
        return report;
    }

    private void updatePhotoView() {
        if(mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);

        }
    }

}
