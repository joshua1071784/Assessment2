package com.bignerdranch.android.receipttracker.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.receipttracker.Receipt;
import com.bignerdranch.android.receipttracker.database.ReceiptDbSchema.ReceiptTable;

import java.util.Date;
import java.util.UUID;

public class ReceiptCursorWrapper extends CursorWrapper{
    public ReceiptCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Receipt getReceipt() {
        String uuidString = getString(getColumnIndex(ReceiptTable.Cols.UUID));
        String title = getString(getColumnIndex(ReceiptTable.Cols.TITLE));
        String shop = getString(getColumnIndex(ReceiptTable.Cols.SHOP));
        String comments = getString(getColumnIndex(ReceiptTable.Cols.COMMENTS));
        long date = getLong(getColumnIndex(ReceiptTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(ReceiptTable.Cols.FILED));
        String suspect = getString(getColumnIndex(ReceiptTable.Cols.SUSPECT));

        Receipt receipt = new Receipt(UUID.fromString(uuidString));
        receipt.setTitle(title);
        receipt.setShop(shop);
        receipt.setComments(comments);
        receipt.setDate(new Date(date));
        receipt.setFiled(isSolved !=0);
        receipt.setSuspect(suspect);

        return receipt;
    }


}
