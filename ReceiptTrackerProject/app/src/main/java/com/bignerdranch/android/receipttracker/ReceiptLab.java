package com.bignerdranch.android.receipttracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.bignerdranch.android.receipttracker.database.ReceiptBaseHelper;
import com.bignerdranch.android.receipttracker.database.ReceiptCursorWrapper;
import com.bignerdranch.android.receipttracker.database.ReceiptDbSchema.ReceiptTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReceiptLab {
    private static ReceiptLab sReceiptLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static ReceiptLab get (Context context) {
        if (sReceiptLab == null) {
            sReceiptLab = new ReceiptLab(context);
        }
        return sReceiptLab;
    }
    private ReceiptLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ReceiptBaseHelper(mContext)
                .getWritableDatabase();

    }

    public void addReceipt(Receipt c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(ReceiptTable.NAME, null, values);

    }
    public List<Receipt> getReceipts() {
        List<Receipt> receipts = new ArrayList<>();

        ReceiptCursorWrapper cursor = queryReceipts(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                receipts.add(cursor.getReceipt());
                cursor.moveToNext();
        }
        } finally {
            cursor.close();
        }
        return receipts;
    }

        public Receipt getReceipt(UUID id) {
            ReceiptCursorWrapper cursor = queryReceipts(
                    ReceiptTable.Cols.UUID + " = ?",
                    new String[] { id.toString() }
                    );
            try {
                if (cursor.getCount() == 0) {
                    return null;
                }
                    cursor.moveToFirst();
                    return cursor.getReceipt();
                } finally {
                    cursor.close();
                }
    }
    public File getPhotoFile(Receipt receipt) {
        File externalFilesDir = mContext
                .getExternalFilesDir (Environment.DIRECTORY_PICTURES);
        if (externalFilesDir == null) {
            return null;
        }
        return new File(externalFilesDir, receipt.getPhotoFilename());
    }


    public void updateReceipt(Receipt receipt) {
        String uuidString = receipt.getId().toString();
        ContentValues values = getContentValues(receipt);
        mDatabase.update(ReceiptTable.NAME, values,
            ReceiptTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }



    private static ContentValues getContentValues(Receipt receipt) {
        ContentValues values = new ContentValues();
        values.put(ReceiptTable.Cols.UUID, receipt.getId().toString());
        values.put(ReceiptTable.Cols.TITLE, receipt.getTitle());
        values.put(ReceiptTable.Cols.SHOP, receipt.getShop());
        values.put(ReceiptTable.Cols.COMMENTS, receipt.getComments());
        values.put(ReceiptTable.Cols.DATE, receipt.getDate().getTime());
    values.put(ReceiptTable.Cols.FILED, receipt.isFiled() ? 1 : 0);
    values.put(ReceiptTable.Cols.SUSPECT, receipt.getSuspect());
    return values;    }


    private ReceiptCursorWrapper queryReceipts(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                ReceiptTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
            null, // having
            null  // orderBy
            );
        return new ReceiptCursorWrapper(cursor);


    }
    public void deleteReceipt(Receipt c) {
        mDatabase.delete(ReceiptTable.NAME, ReceiptTable.Cols.UUID + " = ?",
                new String[]{c.getId().toString()});
    }

    }

