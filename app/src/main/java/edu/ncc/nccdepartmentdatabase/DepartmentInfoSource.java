package edu.ncc.nccdepartmentdatabase;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.SQLException;

import java.util.ArrayList;
import java.util.List;

public class DepartmentInfoSource {

    private SQLiteDatabase database;
    private DepartmentInfoHelper deptHelper;

    private String[] allColumns = { DepartmentInfoHelper._ID, DepartmentInfoHelper.NAME, DepartmentInfoHelper.LOCATION,
            DepartmentInfoHelper.PHONE, };

    public DepartmentInfoSource(Context context) {
        deptHelper = new DepartmentInfoHelper(context);
    }

    public void open() throws SQLException {
        database = deptHelper.getWritableDatabase();
    }

    public void close() {
        deptHelper.close();
    }

    public DepartmentEntry addDept(String name, String location, String phone) {
        ContentValues values = new ContentValues();
        values.put(DepartmentInfoHelper.NAME, name);
        values.put(DepartmentInfoHelper.LOCATION, location);
        values.put(DepartmentInfoHelper.PHONE, phone);
        long insertId = database.insert(DepartmentInfoHelper.TABLE_NAME, null, values);
        Cursor cursor = database.query(DepartmentInfoHelper.TABLE_NAME, allColumns, DepartmentInfoHelper._ID + " = " +insertId, null, null, null, null);
        cursor.moveToFirst();
        DepartmentEntry entry = cursorToEntry(cursor);
        cursor.close();
        return entry;
    }

    public void deleteDept(DepartmentEntry dept) {
        long id = dept.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(DepartmentInfoHelper.TABLE_NAME, DepartmentInfoHelper._ID
                + " = " + id, null);
    }

    public List<DepartmentEntry> getAllDepartments() {
        List<DepartmentEntry> dpts = new ArrayList<>();
        DepartmentEntry entry;
        Cursor cursor = database.query(DepartmentInfoHelper.TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            entry = cursorToEntry(cursor);
            dpts.add(entry);
            cursor.moveToNext();
        }
        cursor.close();
        return dpts;
    }
    public List<DepartmentEntry> queryBy(String[] query){
        List<DepartmentEntry> dpts = new ArrayList<>();
        DepartmentEntry entry;
        Cursor cursor = null;
        if(query[0] == "%A Cluster%") {
             cursor = database.query(DepartmentInfoHelper.TABLE_NAME,
                    allColumns, DepartmentInfoHelper.LOCATION + " LIKE ? OR " + DepartmentInfoHelper.LOCATION + " LIKE ? OR " + DepartmentInfoHelper.LOCATION + " LIKE ?  OR " +
                            DepartmentInfoHelper.LOCATION + " LIKE ? OR " + DepartmentInfoHelper.LOCATION + " LIKE ? OR " + DepartmentInfoHelper.LOCATION + " LIKE ? OR " + DepartmentInfoHelper.LOCATION + " LIKE ? OR " +
                            DepartmentInfoHelper.LOCATION + " LIKE ? OR "+ DepartmentInfoHelper.LOCATION + " LIKE ? OR " + DepartmentInfoHelper.LOCATION + " LIKE ? OR " + DepartmentInfoHelper.LOCATION + " LIKE ?  OR " +
                             DepartmentInfoHelper.LOCATION + " LIKE ? OR " + DepartmentInfoHelper.LOCATION + " LIKE ? OR " + DepartmentInfoHelper.LOCATION + " LIKE ? OR " + DepartmentInfoHelper.LOCATION + " LIKE ? OR " +
                             DepartmentInfoHelper.LOCATION + " LIKE ? ", query, null, null, null);
        }
        if(query[0] == "%Dean%" || query[0] == "%Center%") {
             cursor = database.query(DepartmentInfoHelper.TABLE_NAME,
                    allColumns, DepartmentInfoHelper.NAME + " LIKE ? ", query, null, null, null);
        }

        if(query[0] == "%Tower%"){
            cursor = database.query(DepartmentInfoHelper.TABLE_NAME,
                    allColumns, DepartmentInfoHelper.LOCATION + " LIKE ? ", query, null, null, DepartmentInfoHelper.LOCATION + " ASC ");
        }

        if(query[0] == "Result"){
            cursor = database.query(DepartmentInfoHelper.TABLE_NAME,
                    allColumns, DepartmentInfoHelper.LOCATION + " LIKE ? ", new String[] {query[1]}, null, null, null);
        }

//        Cursor cursor = database.query(DepartmentInfoHelper.TABLE_NAME,
//               allColumns, DepartmentInfoHelper.LOCATION +  " LIKE ?" , new String[] {"%Rice Circle%"},null, null,DepartmentInfoHelper.LOCATION)
                /*"OR " + DepartmentInfoHelper.LOCATION + " LIKE ?",  new String[] { "%Cluster%" },
               new String[]{"%Cluster F%", "%F Cluster%"}*/;
//        Cursor cursor = database.query(DepartmentInfoHelper.TABLE_NAME,
//               allColumns, DepartmentInfoHelper.LOCATION +  " LIKE ?" , new String[] {"%Rice Circle%"},null, null,DepartmentInfoHelper.LOCATION);


        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            entry = cursorToEntry(cursor);
            dpts.add(entry);
            cursor.moveToNext();
        }
        cursor.close();
        return dpts;

    }

    private DepartmentEntry cursorToEntry(Cursor cursor) {
        DepartmentEntry entry = new DepartmentEntry();
        entry.setId(cursor.getLong(0));
        entry.setName(cursor.getString(1));
        entry.setLocation(cursor.getString(2));
        entry.setPhone(cursor.getString(3));
        return entry;
    }
}