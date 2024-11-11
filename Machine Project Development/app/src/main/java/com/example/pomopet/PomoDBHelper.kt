package com.example.pomopet

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PomoDBHelper(context: Context) : SQLiteOpenHelper(context, DatabaseRef.DATABASE_NAME, null, DatabaseRef.DATABASE_VERSION ){

    companion object {
        private var instance: PomoDBHelper? = null

        @Synchronized
        fun getInstance(context: Context): PomoDBHelper? {
            if (instance == null){
                instance = PomoDBHelper(context.applicationContext)
            }
            return instance
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(DatabaseRef.CREATE_TABLE_STATEMENT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(DatabaseRef.DROP_TABLE_STATEMENT)
        onCreate(db)
    }

    // array list is used, this is so we can anticipate if there is no existing save file
    fun getPet(): ArrayList<PomoModel> {
        val database = this.readableDatabase

        val c : Cursor = database.query(
            DatabaseRef.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
        val pomoModelList: ArrayList<PomoModel> = ArrayList()

        if (c.moveToNext())
        {
            pomoModelList.add(PomoModel(
                c.getString(c.getColumnIndexOrThrow(DatabaseRef.COLUMN_NAME_USERNAME)),
                c.getString(c.getColumnIndexOrThrow(DatabaseRef.COLUMN_NAME_PET_NAME)),
                c.getInt(c.getColumnIndexOrThrow(DatabaseRef.COLUMN_NAME_PET_TYPE)),
                c.getInt(c.getColumnIndexOrThrow(DatabaseRef.COLUMN_NAME_PET_LEVEL)),
                c.getInt(c.getColumnIndexOrThrow(DatabaseRef.COLUMN_NAME_PET_EVOL)),
                c.getInt(c.getColumnIndexOrThrow(DatabaseRef.COLUMN_NAME_PET_EXP)),
                c.getInt(c.getColumnIndexOrThrow(DatabaseRef.COLUMN_NAME_PET_MAX_EXP)),
                c.getInt(c.getColumnIndexOrThrow(DatabaseRef.COLUMN_NAME_IS_LEVEL_UP)),
                c.getInt(c.getColumnIndexOrThrow(DatabaseRef.COLUMN_NAME_REMAINING_EXP)),
                c.getLong(c.getColumnIndexOrThrow(DatabaseRef._ID))
                ))
        }
        c.close()
        return pomoModelList
    }

    fun insertNewPet(pomoModel: PomoModel): Boolean {
        val database = this.writableDatabase

        val values = ContentValues()
        values.put(DatabaseRef.COLUMN_NAME_USERNAME, pomoModel.username)
        values.put(DatabaseRef.COLUMN_NAME_PET_NAME, pomoModel.pet_name)
        values.put(DatabaseRef.COLUMN_NAME_PET_LEVEL, pomoModel.pet_level)
        values.put(DatabaseRef.COLUMN_NAME_PET_EVOL, pomoModel.pet_evol)
        values.put(DatabaseRef.COLUMN_NAME_PET_EXP, pomoModel.pet_exp)
        values.put(DatabaseRef.COLUMN_NAME_PET_MAX_EXP, pomoModel.pet_max_exp)
        values.put(DatabaseRef.COLUMN_NAME_PET_TYPE, pomoModel.pet_type)

        val id: Long = database.insert(DatabaseRef.TABLE_NAME, null, values)
        database.close()

        return id != -1L
    }
    // this only manipulates select variables, username/pet_name/pet_type will never be changed
    fun savePetExp(pomoModel: PomoModel): Boolean{
        val database = this.writableDatabase

        val values = ContentValues()

        values.put(DatabaseRef.COLUMN_NAME_PET_LEVEL, pomoModel.pet_level)
        values.put(DatabaseRef.COLUMN_NAME_PET_EVOL, pomoModel.pet_evol)
        values.put(DatabaseRef.COLUMN_NAME_PET_EXP, pomoModel.pet_exp)
        values.put(DatabaseRef.COLUMN_NAME_PET_MAX_EXP, pomoModel.pet_max_exp)

        val rowsUpdated = database.update(DatabaseRef.TABLE_NAME, values, DatabaseRef._ID + "= ?", arrayOf(pomoModel.id.toString()))
        database.close()

        return rowsUpdated < 1


    }


    // save if pet is going to level up
    fun setPetToLevelUp(pomoModel: PomoModel): Boolean{
        val database = this.writableDatabase

        val values = ContentValues()

        values.put(DatabaseRef.COLUMN_NAME_IS_LEVEL_UP, pomoModel.is_level_up)

        val rowsUpdated = database.update(DatabaseRef.TABLE_NAME, values, DatabaseRef._ID + "= ?", arrayOf(pomoModel.id.toString()))
        database.close()

        return rowsUpdated < 1


    }


    private object DatabaseRef {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "pomopet.db"

        const val TABLE_NAME = "pomo_table"
        const val _ID = "id"
        const val COLUMN_NAME_USERNAME = "username"
        const val COLUMN_NAME_PET_NAME = "pet_name"
        const val COLUMN_NAME_PET_LEVEL = "pet_level"
        const val COLUMN_NAME_PET_EVOL = "pet_evol"
        const val COLUMN_NAME_PET_EXP = "pet_exp"
        const val COLUMN_NAME_PET_MAX_EXP = "pet_max_exp"
        const val COLUMN_NAME_PET_TYPE = "pet_type"
        const val COLUMN_NAME_IS_LEVEL_UP = "level_up"
        const val COLUMN_NAME_REMAINING_EXP = "remaining_exp"


        const val CREATE_TABLE_STATEMENT =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME_USERNAME + " TEXT, " +
                    COLUMN_NAME_PET_NAME + " TEXT, " +
                    COLUMN_NAME_PET_LEVEL + " INT, " +
                    COLUMN_NAME_PET_EVOL + " INT, " +
                    COLUMN_NAME_PET_EXP + " INT, " +
                    COLUMN_NAME_PET_MAX_EXP + " INT, " +
                    COLUMN_NAME_PET_TYPE + " INT, " +
                    COLUMN_NAME_IS_LEVEL_UP + " INT, " +
                    COLUMN_NAME_REMAINING_EXP + " INT)"

        const val DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME
    }

}