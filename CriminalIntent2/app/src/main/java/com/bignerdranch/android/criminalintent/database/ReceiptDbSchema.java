package com.bignerdranch.android.receipttracker.database;

public class ReceiptDbSchema {
    public static final class ReceiptTable {
        public static final String NAME = "receipts";
        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String SHOP = "shop";
            public static final String COMMENTS = "comments";
            public static final String DATE = "date";
        public static final String FILED = "filed";
        public static final String SUSPECT = "suspect";
        }
    }
}

