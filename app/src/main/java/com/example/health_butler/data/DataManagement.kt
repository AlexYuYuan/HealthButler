package com.example.health_butler.data

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.LinkedList


class DataManagement (context: Context){

    private val dateBaseHelper: DataBaseHelper

    init {
        dateBaseHelper = DataBaseHelper(context, "BealthButler", null, 1, null)
    }

    private class DataBaseHelper(
        context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?,
        version: Int, errorHandler: DatabaseErrorHandler?
    ) : SQLiteOpenHelper(context, name, factory, version, errorHandler) {

        override fun onCreate(db: SQLiteDatabase?) {

            db?.execSQL("create table foods(food_id int not null primary key," +
                            "food_name varchar(100) not null unique default''," +
                            "type varchar(10) not null unique default''," +
                            "unit varchar(10) not null default'g', " +
                            "calorie int not null default 0, " +
                            "carbohydrate int not null default 0," +
                            "protein int not null default 0," +
                            "fat int not null default 0)")

            db?.execSQL("create table diet_records(recods_id int not null primary key," +
                            "date int not null default 0," +
                            "type varchar(10) not null unique default''," +
                            "food_name INT CONSTRAINT f_fr_name REFERENCES foods(food_name), " +
                            "weight double not null default 0)")

            db?.execSQL("create table sports(sport_name varchar(40) primary key," +
                            "calorie int not null default 0)")

            db?.execSQL("create table sport_records(date int primary key," +
                            "sport_name varchar(40) CONSTRAINT s_sr_name REFERENCES sports(sport_name), " +
                            "time int not null default 0)")

            db?.execSQL("create table weight(date int primary key," +
                            "weight double not null default 0)")

            db?.execSQL("create table fat_fat(date int primary key," +
                            "rate double not null default 0)")

            db?.execSQL("create table body_size(date int primary key," +
                            "chest double not null default 0, " +
                            "waist double not null default 0, " +
                            "hip double not null default 0)")

            db?.execSQL("create table fat_fat(date int primary key," +
                    "volume int not null default 0)")
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    }

    //查找所有食品
    fun queryAllFoods(): LinkedList<Food> {
        val dataBase = dateBaseHelper.writableDatabase
        val foodsList: LinkedList<Food> = LinkedList<Food>()
        val result = dataBase.query("foods", null, null, null, null, null, "date")
        result.moveToFirst()
        while (!result.isAfterLast){
            foodsList.add(Food(result.getString(1),result.getString(3),result.getInt(4),result.getInt(5),result.getInt(6), result.getInt(7)))
            result.moveToNext()
        }
        result.close()
        dataBase.close()
        return foodsList
    }

    //查找用户自定义食品
    fun queryUserDefinedFoods(type: String):LinkedList<Food>{
        val dataBase = dateBaseHelper.writableDatabase
        val foodsList: LinkedList<Food> = LinkedList<Food>()
        val result = dataBase.query("foods", null, "type = ?", arrayOf("userDefined "), null, null, null)
        result.moveToFirst()
        while (!result.isAfterLast){
            foodsList.add(Food(result.getString(1),result.getString(3),result.getInt(4),result.getInt(5),result.getInt(6), result.getInt(7)))
            result.moveToNext()
        }
        result.close()
        dataBase.close()
        return foodsList
    }

    //添加食品，成功返回1，若食品名已存在返回0
    fun insertFoods(food: Food): Int{
        val dataBase = dateBaseHelper.writableDatabase
        val result = dataBase.query("foods", null, "food_name = ?", arrayOf(food.getName()), null, null, null)
        if (result.getCount() != 0){
            val contentValues = ContentValues()
            contentValues.put("name", food.getName())
            contentValues.put("type", food.getType())
            contentValues.put("unit", food.getUnit())
            contentValues.put("calorie", food.getCalorie())
            contentValues.put("carbohydrate", food.getCarbohydrate())
            contentValues.put("protein", food.getProtein())
            contentValues.put("fat", food.getFat())
            dataBase.insert("foods", null, contentValues)
            result.close()
            dataBase.close()
            return 1
        }
        else {
            result.close()
            dataBase.close()
            return 0
        }
    }

    //删除指定用户自定义食品
    fun delUserDefinedFood(food: Food){
        val dataBase = dateBaseHelper.writableDatabase
        dataBase.delete("foods","food_name = ?", arrayOf(food.getName()))
        dataBase.close()
    }

    //查询所有运动记录
    fun queryAllSportRecords(): LinkedList<SportRecord>{
        val dataBase = dateBaseHelper.writableDatabase
        val sportRecords = LinkedList<SportRecord>()
        val result = dataBase.query("sport_rescords as sr inner join sport as s", arrayOf("date", "sport_name", "calorie", "time"), "sr.sport_name = s.sport_name", null, null, null, "date")
        result.moveToFirst()
        while (!result.isAfterLast){
            sportRecords.add(SportRecord(result.getInt(0), Sport(result.getString(1), result.getInt(2)), result.getInt(3)))
            result.moveToNext()
        }
        result.close()
        dataBase.close()
        return sportRecords
    }

    //新增运动记录
    fun insertSportRecord(sportRecord: SportRecord){
        val dataBase = dateBaseHelper.writableDatabase
        val contentValues = ContentValues()

        contentValues.put("date", sportRecord.getDate())
        contentValues.put("sport_name", sportRecord.getSport().getName())
        contentValues.put("time", sportRecord.getTime())
        dataBase.insert("sport_records", null, contentValues)

        dataBase.close()
    }

    //新增运动项目
    fun insertSport(sport: Sport):Int{
        val dataBase = dateBaseHelper.writableDatabase
        val result = dataBase.query("sport", null, "name = ?", arrayOf(sport.getName()), null, null, null)
        if(result.getCount() != 0){
            val contentValues = ContentValues()
            contentValues.put("sport_name", sport.getName())
            contentValues.put("calorie", sport.getCalorie())
            dataBase.insert("sport", null, contentValues)
            result.close()
            dataBase.close()
            return 1
        }
        else {
            result.close()
            dataBase.close()
            return 0
        }
    }
}