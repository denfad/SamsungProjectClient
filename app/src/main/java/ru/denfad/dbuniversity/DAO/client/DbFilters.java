package ru.denfad.dbuniversity.DAO.client;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ru.denfad.dbuniversity.model.Student;

public class DbFilters extends DbStructure {

    private DbWorker dbWorker;
    private SQLiteDatabase mDataBase;

    public DbFilters(DbWorker dbWorker){
        this.dbWorker=dbWorker;
        mDataBase=this.dbWorker.getDataBase();
    }

    public List<Student> selectStudentsByGroup(int group_id){
        Cursor mCursor = mDataBase.query(TABLE_STUDENTS, null, STUDENT_GROUP_ID + " = ?", new String[]{String.valueOf(group_id)}, null, null, null);

        List<Student> arr = new ArrayList<>();
        mCursor.moveToFirst();

        if (!mCursor.isAfterLast()) {
            do {
                int studentId = mCursor.getInt(0); //column id
                String name = mCursor.getString(1); //column name
                String secondName = mCursor.getString(2); //column second name
                String middleName = mCursor.getString(3); //column middle name
                String birthDate = mCursor.getString(4); //column birth date
                int id = mCursor.getInt(5); //column group_id
                arr.add(new Student(studentId,name,secondName,middleName,birthDate,group_id));
            } while (mCursor.moveToNext());
        }
        return arr;
    }

    public boolean checkIsEmptyGroup(int group_id){
        if(selectStudentsByGroup(group_id).isEmpty()){
            return true; //return true if group empty
        }
        else return false;
    }

}
