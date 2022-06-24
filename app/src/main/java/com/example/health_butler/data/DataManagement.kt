package com.example.health_butler.data

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDate
import java.util.LinkedList
import java.util.Calendar
import kotlin.math.abs

class DataManagement (context: Context){

    private val context = context

    private class DataBaseHelper(
        context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?,
        version: Int, errorHandler: DatabaseErrorHandler?
    ) : SQLiteOpenHelper(context, name, factory, version, errorHandler) {

        override fun onCreate(db: SQLiteDatabase?) {

            db?.execSQL("create table food(food_id int not null primary key autoincrement," +
                            "food_name varchar(100) not null unique default''," +
                            "type int not null default''," +
                            "unit varchar(10) not null default'g', " +
                            "calorie int not null default 0, " +
                            "carbohydrate int not null default 0," +
                            "protein int not null default 0," +
                            "fat int not null default 0)")

            db?.execSQL("create table diet_record(date int primary key," +
                            "calorie int not null default 0, " +
                            "carbohydrate int not null default 0," +
                            "protein int not null default 0," +
                            "fat int not null default 0)")

            db?.execSQL("create table diet_food(record_id int not null primary key," +
                            "date int constraint d_df_date references diet_records(date)," +
                            "type int not null default''," +
                            "food_name INT CONSTRAINT f_fr_name REFERENCES food(food_name), " +
                            "quantity double not null default 0)")

            db?.execSQL("create table sport(sport_name varchar(40) primary key," +
                            "calorie int not null default 0)")

            db?.execSQL("create table sport_record(date int primary key," +
                            "sport_name varchar(40) CONSTRAINT s_sr_name REFERENCES sports(sport_name), " +
                            "time int not null default 0)")

            db?.execSQL("create table weight(date int primary key," +
                            "weight double not null default 0, " +
                            "rate double not null default 0)")

            db?.execSQL("create table body_size(date int primary key," +
                            "chest double not null default 0, " +
                            "waist double not null default 0, " +
                            "hip double not null default 0)")

            db?.execSQL("create table drink_records(date int primary key," +
                    "volume int not null default 0)")
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    }

    //查找所有食品
    fun queryAllFoods(): LinkedList<Food> {
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val foodsList: LinkedList<Food> = LinkedList<Food>()
        val result = dataBase.query("food", null, null, null, null, null, "date")
        dataBase.close()
        result.moveToFirst()
        while (!result.isAfterLast){
            foodsList.add(Food(result.getString(1),result.getString(3),result.getInt(4),result.getInt(5),result.getInt(6), result.getInt(7), FOODTYPE.fromInt(result.getInt(2))))
            result.moveToNext()
        }
        result.close()
        return foodsList
    }

    //查找用户自定义食品
    fun queryUserDefinedFoods():LinkedList<Food>{
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val foodsList: LinkedList<Food> = LinkedList<Food>()
        val result = dataBase.query("food", null, "type = ?", arrayOf("userDefined "), null, null, null)
        dataBase.close()
        result.moveToFirst()
        while (!result.isAfterLast){
            foodsList.add(Food(result.getString(1),result.getString(3),result.getInt(4),result.getInt(5),result.getInt(6), result.getInt(7), FOODTYPE.USERDEFINED))
            result.moveToNext()
        }
        result.close()
        return foodsList
    }

    //添加食品，成功返回1，若食品名已存在返回0
    fun insertFoods(food: Food): Int{
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val result = dataBase.query("food", null, "food_name = ?", arrayOf(food.name), null, null, null)
        if (result.getCount() != 0){
            val contentValues = ContentValues()
            contentValues.put("name", food.name)
            contentValues.put("type", food.type.value)
            contentValues.put("unit", food.unit)
            contentValues.put("calorie", food.calorie)
            contentValues.put("carbohydrate", food.carbohydrate)
            contentValues.put("protein", food.protein)
            contentValues.put("fat", food.fat)
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
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        dataBase.delete("food","food_name = ?", arrayOf(food.name))
        dataBase.close()
    }

    //新增饮食食品记录(自动更新饮食记录)
    fun insertDiet(dietRecord: DietRecord){
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val contentValues = ContentValues()
        var result = dataBase.query("diet_food", null, "food_name = ?", arrayOf(dietRecord.food.name), null, null, null)
        if (result.getCount() < 1){
            contentValues.put("date", dietRecord.date)
            contentValues.put("quantity", dietRecord.quantity)
            contentValues.put("foodName", dietRecord.food.name)
            contentValues.put("type", dietRecord.type.value)
            dataBase.insert("diet_food", null, contentValues)
        }
        else{
            contentValues.put("quantity", result.getInt(4)+dietRecord.quantity)
            dataBase.update("diet_food", contentValues, "food_name = ? and date = ?", arrayOf(dietRecord.food.name, dietRecord.date.toString()))
        }
        result.close()
        contentValues.clear()
        result = dataBase.query("diet_record", null, "date = ?", arrayOf(dietRecord.date.toString()), null, null, null)
        if (result.getCount() < 1){
            contentValues.put("date", dietRecord.date)
            contentValues.put("calorie", (dietRecord.food.calorie * dietRecord.quantity).toInt())
            contentValues.put("carbohydrate", (dietRecord.food.carbohydrate * dietRecord.quantity).toInt())
            contentValues.put("protein", (dietRecord.food.protein * dietRecord.quantity).toInt())
            contentValues.put("fat", (dietRecord.food.fat * dietRecord.quantity).toInt())
            dataBase.insert("diet_food", null, contentValues)
        }
        else{
            contentValues.put("calorie", result.getInt(1) + dietRecord.food.calorie * dietRecord.quantity)
            contentValues.put("carbohydrate", result.getInt(2) + dietRecord.food.carbohydrate * dietRecord.quantity)
            contentValues.put("protein", result.getInt(3) + dietRecord.food.protein * dietRecord.quantity)
            contentValues.put("fat", result.getInt(4) + dietRecord.food.fat * dietRecord.quantity)
            dataBase.update("diet_food", contentValues, "food_name = ? and date = ?", arrayOf(dietRecord.food.name, dietRecord.date.toString()))
        }
        result.close()
        dataBase.close()
    }

    //查询饮食食品记录
    fun queryDiet(date: Int, type: TYPE):LinkedList<DietRecord>{
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val dietFoods = LinkedList<DietRecord>()
        val result =  dataBase.query("diet_food ad df inner join food as f", arrayOf("name", "unit", "calorie", "carbohydrate", "protein", "fat", "f.type", "quantity"), "df.food_name = f.name and date = ? and type = ?", arrayOf(date.toString(), type.ordinal.toString()), null, null, null)
        dataBase.close()
        while (!result.isAfterLast){
            dietFoods.add(DietRecord(date, type, Food(result.getString(0), result.getString(1), result.getInt(2), result.getInt(3), result.getInt(4), result.getInt(5), FOODTYPE.fromInt(result.getInt(6))),result.getDouble(7)))
        }
        return dietFoods
    }

    //查询运动记录
    fun queryAllSportRecords(model: Int): LinkedList<SportRecord>{
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val sportRecords = LinkedList<SportRecord>()
        val now = LocalDate.now()
        val calendar = Calendar.getInstance()
        calendar.set(now.year, now.monthValue-3, now.dayOfMonth)
        var nowUNIX: Int = (calendar.timeInMillis/1000) as Int
        var begin = 0
        var selection: Array<String> = arrayOf()
        when(model){
            0 -> {
                begin = nowUNIX - 31536000
                selection = arrayOf(begin.toString(), nowUNIX.toString())
            }
            1 -> {
                begin = nowUNIX - 2592000
                selection = arrayOf(begin.toString(), nowUNIX.toString())
            }
            2 -> {
                begin = nowUNIX - 7776000
                selection = arrayOf(begin.toString(), nowUNIX.toString())
            }
            3 -> {
                selection = arrayOf("0", "2147483647")
            }
        }
        val result = dataBase.query("sport_record as sr inner join sport as s", arrayOf("date", "sport_name", "calorie", "time"), "sr.sport_name = s.sport_name and date >= ? and date <= ?", selection, null, null, "date")
        dataBase.close()
        result.moveToFirst()
        while (!result.isAfterLast){
            sportRecords.add(SportRecord(result.getInt(0), Sport(result.getString(1), result.getInt(2)), result.getInt(3)))
            result.moveToNext()
        }
        result.close()
        return sportRecords
    }

    //新增运动记录
    fun insertSportRecord(sportRecord: SportRecord){
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val contentValues = ContentValues()

        contentValues.put("date", sportRecord.date)
        contentValues.put("sport_name", sportRecord.sport.name)
        contentValues.put("time", sportRecord.time)
        dataBase.insert("sport_record", null, contentValues)

        dataBase.close()
    }

    //新增运动项目,成功返回1，运动名已存在返回2
    fun insertSport(sport: Sport):Int{
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val result = dataBase.query("sport", null, "name = ?", arrayOf(sport.name), null, null, null)
        if(result.getCount() != 0){
            val contentValues = ContentValues()
            contentValues.put("sport_name", sport.name)
            contentValues.put("calorie", sport.calorie)
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

    //新增体重记录（自动更新体脂）
    fun insertWeight(weight: Weight): Int{
        val waist = queryWaist(weight.weight)
        if (waist != 0.0){
            val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
            val contentValues = ContentValues()
            contentValues.put("date", weight.date)
            contentValues.put("weight", weight.weight)
            contentValues.put("rate", ((waist * 0.74) - (weight.weight * 0.082))/weight.weight)
            dataBase.insert("weight", null, contentValues)
            dataBase.close()
            return 1
        }
        else
            return 0
    }

    //按时间段查询体重体脂记录
    fun queryFatRate(period: PERIOD): LinkedList<Weight>{
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val fatRates = LinkedList<Weight>()
        val now = LocalDate.now()
        val calendar = Calendar.getInstance()
        calendar.set(now.year, now.monthValue-3, now.dayOfMonth)
        var nowUNIX: Int = (calendar.timeInMillis/1000) as Int
        var begin = 0
        var selection: Array<String> = arrayOf()
        when(period){
            PERIOD.YEAR -> {
                begin = nowUNIX - 31536000
                selection = arrayOf(begin.toString(), nowUNIX.toString())
            }
            PERIOD.MONTH -> {
                begin = nowUNIX - 2592000
                selection = arrayOf(begin.toString(), nowUNIX.toString())
            }
            PERIOD.THREEMONTH -> {
                begin = nowUNIX - 7776000
                selection = arrayOf(begin.toString(), nowUNIX.toString())
            }
            PERIOD.ALL -> {
                selection = arrayOf("0", "2147483647")
            }
        }
        val result = dataBase.query("weight", null, "date >= ? and date <= ?", selection, null, null, null)
        dataBase.close()
        result.moveToFirst()
        while (!result.isAfterLast) {
            fatRates.add(Weight(result.getInt(0), result.getDouble(1), result.getDouble(2)))
            result.moveToNext()
        }
        result.close()
        return fatRates
    }

    //查询已记录日期
    fun queryRecordDates(Table: TABLE):LinkedList<Int>{
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val dates = LinkedList<Int>()
        var table: String = ""
        when(Table){
            TABLE.DIET -> table = "diet_records"
            TABLE.SPORT -> table = "sport_records"
            TABLE.DRINK -> table = "drink_records"
        }
        val result = dataBase.query(table, arrayOf("date"), null, null, null, null, null)
        dataBase.close()
        result.moveToFirst()
        while (!result.isAfterLast){
            dates.add(result.getInt(0))
            result.moveToNext()
        }
        result.close()
        return dates
    }

    //查询饮水记录
    fun queryDrinkRecords(period: PERIOD):LinkedList<DrinkRecord>{
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val drinkRecords = LinkedList<DrinkRecord>()
        val now = LocalDate.now()
        val calendar = Calendar.getInstance()
        calendar.set(now.year, now.monthValue-3, now.dayOfMonth)
        var nowUNIX: Int = (calendar.timeInMillis/1000) as Int
        var begin = 0
        var selection: Array<String> = arrayOf()
        when(period){
            PERIOD.YEAR -> {
                begin = nowUNIX - 31536000
                selection = arrayOf(begin.toString(), nowUNIX.toString())
            }
            PERIOD.MONTH -> {
                begin = nowUNIX - 2592000
                selection = arrayOf(begin.toString(), nowUNIX.toString())
            }
            PERIOD.THREEMONTH -> {
                begin = nowUNIX - 7776000
                selection = arrayOf(begin.toString(), nowUNIX.toString())
            }
            PERIOD.ALL -> {
                selection = arrayOf("0", "2147483647")
            }
        }
        val result = dataBase.query("drink_records", null, "date >= ? and date <= ?", selection, null, null, null)
        dataBase.close()
        result.moveToFirst()
        while (!result.isAfterLast) {
            drinkRecords.add(DrinkRecord(result.getInt(0), result.getInt(1)))
            result.moveToNext()
        }
        result.close()
        return drinkRecords
    }

    //新增饮水记录
    fun insertDrinkRecord(drinkRecord: DrinkRecord){
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val contentValues = ContentValues()
        contentValues.put("date", drinkRecord.date)
        contentValues.put("volume", drinkRecord.volume)
        dataBase.insert("drink_records", null, contentValues)
        dataBase.close()
    }

    //查询三围记录
    fun queryBodySize(period: PERIOD): LinkedList<BodySize>{
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val bodySize = LinkedList<BodySize>()
        val now = LocalDate.now()
        val calendar = Calendar.getInstance()
        calendar.set(now.year, now.monthValue-3, now.dayOfMonth)
        var nowUNIX: Int = (calendar.timeInMillis/1000) as Int
        var begin = 0
        var selection: Array<String> = arrayOf()
        when(period){
            PERIOD.YEAR -> {
                begin = nowUNIX - 31536000
                selection = arrayOf(begin.toString(), nowUNIX.toString())
            }
            PERIOD.MONTH -> {
                begin = nowUNIX - 2592000
                selection = arrayOf(begin.toString(), nowUNIX.toString())
            }
            PERIOD.THREEMONTH -> {
                begin = nowUNIX - 7776000
                selection = arrayOf(begin.toString(), nowUNIX.toString())
            }
            PERIOD.ALL -> {
                selection = arrayOf("0", "2147483647")
            }
        }
        val result = dataBase.query("body_size", null, "date >= ? and date <= ?", selection, null, null, null)
        dataBase.close()
        result.moveToFirst()
        while (!result.isAfterLast) {
            bodySize.add(BodySize(result.getInt(0), result.getInt(1), result.getInt(2), result.getInt(3)))
            result.moveToNext()
        }
        result.close()
        return bodySize
    }

    //新增三围记录
    fun insertBodySize(bodySize: BodySize){
        val dataBase = DataBaseHelper(context, "HealthButler", null, 1, null).writableDatabase
        val contentValues = ContentValues()
        contentValues.put("date", bodySize.date)
        contentValues.put("chest", bodySize.chest)
        contentValues.put("waist", bodySize.waist)
        contentValues.put("hip", bodySize.hip)
        dataBase.insert("body_size", null, contentValues)
        dataBase.close()
    }

    //查询腰围
    fun queryWaist(weight: Double): Double{
        val sharedPreferences = context.getSharedPreferences("tempData",0)
        val lastWeight = sharedPreferences.getString("lastWeight", "")!!.toDouble()
        if(abs(weight-lastWeight) >= 2)
            return 0.0
        else
            return sharedPreferences.getString("waist", "")!!.toDouble()
    }

    //更新腰围
    fun updataWaist(waist:Double, weight: Double){
        val sharedPreferences = context.getSharedPreferences("tempData",0)
        val dataEdit = sharedPreferences.edit()
        dataEdit.putString("waist", waist.toString())
        dataEdit.putString("lastWeight", weight.toString())
    }
}