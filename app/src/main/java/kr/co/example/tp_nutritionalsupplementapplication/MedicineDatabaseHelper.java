package kr.co.example.tp_nutritionalsupplementapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MedicineDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "medicine_db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_MEDICINE = "medicines";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_DATE = "date";

    public MedicineDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MEDICINE_TABLE = "CREATE TABLE " + TABLE_MEDICINE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_TIME + " TEXT,"
                + COLUMN_DATE + " TEXT" + ")";
        db.execSQL(CREATE_MEDICINE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICINE);
        onCreate(db);
    }

    public String getColumnName() {
        return COLUMN_NAME;
    }

    public String getColumnTime() {
        return COLUMN_TIME;
    }

    public String getColumnDate() {
        return COLUMN_DATE;
    }

    public String getTableMedicine() {
        return TABLE_MEDICINE;
    }

    // DB에 저장된 모든 약 정보 데이터를 가져와 리스트 형태로 반환
    public List<Medicine> getAllMedicines() {
        List<Medicine> medicines = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MEDICINE, null);

        if (cursor.moveToFirst()) {
            do {
                Medicine medicine = new Medicine();
                medicine.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                medicine.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                medicine.setTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)));
                medicine.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                medicines.add(medicine);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return medicines;
    }
}

