package com.example.health_butler

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.LinkedList
import java.util.Calendar
import kotlin.math.abs

class DataBaseHelper(
    context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?,
    version: Int, errorHandler: DatabaseErrorHandler?
) : SQLiteOpenHelper(context, name, factory, version, errorHandler) {

    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL("create table food(food_id INTEGER PRIMARY KEY autoincrement," +
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

        db?.execSQL("create table diet_food(record_id INTEGER PRIMARY KEY autoincrement," +
                "date int constraint d_df_date references diet_records(date)," +
                "type int not null default''," +
                "food_name INT CONSTRAINT f_fr_name REFERENCES food(food_name), " +
                "quantity double not null default 0)")

        db?.execSQL("create table sport(sport_name varchar(40) primary key," +
                "time int not null default 0)")

        db?.execSQL("create table sport_record(record_id INTEGER PRIMARY KEY autoincrement," +
                "date int," +
                "sport_name varchar(40), " +
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

        db?.execSQL("create table clock(time String primary key," +
                "state boolean not null default 0)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

}

class MyApplication : Application() {
    //静态变量
    companion object {
        lateinit var context: Context
    }
    override fun onCreate() {
        super.onCreate()
        context = baseContext
    }
}

//数据库对象以单例模式运行
class SingleDataBase private constructor() {
    val dateBaseHelper: DataBaseHelper = DataBaseHelper(MyApplication.context, "calorie", null, 1, null)
    companion object {
        private var instance: SingleDataBase? = null
            get() {
                if (field == null) {
                    field = SingleDataBase()
                }
                return field
            }
        fun get(): SingleDataBase{
            return instance!!
        }
    }
}

//查找所有食品
fun queryAllFoods(): LinkedList<Food> {
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val foodsList: LinkedList<Food> = LinkedList<Food>()
    val result = dataBase.query("food", null, null, null, null, null, null)
    result.moveToFirst()
    while (!result.isAfterLast){
        foodsList.add(Food(result.getString(1),result.getString(3),result.getInt(4),result.getInt(5),result.getInt(6), result.getInt(7), FOODTYPE.fromInt(result.getInt(2))))
        result.moveToNext()
    }
    result.close()
    dataBase.close()
    return foodsList
}

//查找用户自定义食品
fun queryUserDefinedFoods():LinkedList<Food>{
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val foodsList: LinkedList<Food> = LinkedList<Food>()
    val result = dataBase.query("food", null, "type = ?", arrayOf("userDefined "), null, null, null)
    result.moveToFirst()
    while (!result.isAfterLast){
        foodsList.add(Food(result.getString(1),result.getString(3),result.getInt(4),result.getInt(5),result.getInt(6), result.getInt(7), FOODTYPE.USERDEFINED))
        result.moveToNext()
    }
    result.close()
    dataBase.close()
    return foodsList
}

//查找指定食品
fun queryFood(foodName: String):Food{
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val food: Food
    val result = dataBase.query("food", null, "food_name = ?", arrayOf(foodName), null, null, null)
    result.moveToFirst()
    food = Food(result.getString(1),result.getString(3),result.getInt(4),result.getInt(5),result.getInt(6), result.getInt(7), FOODTYPE.USERDEFINED)
    result.close()
    dataBase.close()
    return food
}

//添加食品，成功返回1，若食品名已存在返回0
fun insertFood(food: Food): Int{
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val result = dataBase.query("food", null, "food_name = ?", arrayOf(food.name), null, null, null)
    if (result.getCount() == 0){
        val contentValues = ContentValues()
        contentValues.put("food_name", food.name)
        contentValues.put("type", food.type.value)
        contentValues.put("unit", food.unit)
        contentValues.put("calorie", food.calorie)
        contentValues.put("carbohydrate", food.carbohydrate)
        contentValues.put("protein", food.protein)
        contentValues.put("fat", food.fat)
        val code = dataBase.insert("food", null, contentValues)
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
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    dataBase.delete("food","food_name = ?", arrayOf(food.name))
    dataBase.close()
}

//新增饮食食品记录(自动更新饮食记录)
fun insertDiet(dietRecord: DietRecord){
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val contentValues = ContentValues()
    var result = dataBase.query("diet_food", null, "food_name = ?", arrayOf(dietRecord.foodName), null, null, null)
    var foodData = dataBase.query("food", null, "food_name = ?", arrayOf(dietRecord.foodName), null, null, null)
    foodData.moveToFirst()
    val food = Food(foodData.getString(1),foodData.getString(3),foodData.getInt(4),foodData.getInt(5),foodData.getInt(6), foodData.getInt(7), FOODTYPE.fromInt(foodData.getInt(2)))

    //如果饮食食品记录不存在则新增，否则更新数据
    if (result.getCount() < 1){
        contentValues.put("date", dietRecord.date)
        contentValues.put("quantity", dietRecord.quantity)
        contentValues.put("foodName", dietRecord.foodName)
        contentValues.put("type", dietRecord.type.value)
        dataBase.insert("diet_food", null, contentValues)
    }
    else{
        contentValues.put("quantity", result.getInt(4)+dietRecord.quantity)
        dataBase.update("diet_food", contentValues, "food_name = ? and date = ?", arrayOf(food.name, dietRecord.date.toString()))
    }
    result.close()
    contentValues.clear()
    result = dataBase.query("diet_record", null, "date = ?", arrayOf(dietRecord.date.toString()), null, null, null)

    //如果饮食记录不存在则新增，否则更新数据
    if (result.getCount() < 1){
        contentValues.put("date", dietRecord.date)
        contentValues.put("calorie", (food.calorie * dietRecord.quantity).toInt())
        contentValues.put("carbohydrate", (food.carbohydrate * dietRecord.quantity).toInt())
        contentValues.put("protein", (food.protein * dietRecord.quantity).toInt())
        contentValues.put("fat", (food.fat * dietRecord.quantity).toInt())
        dataBase.insert("diet_food", null, contentValues)
    }
    else{
        contentValues.put("calorie", result.getInt(1) + food.calorie * dietRecord.quantity)
        contentValues.put("carbohydrate", result.getInt(2) + food.carbohydrate * dietRecord.quantity)
        contentValues.put("protein", result.getInt(3) + food.protein * dietRecord.quantity)
        contentValues.put("fat", result.getInt(4) + food.fat * dietRecord.quantity)
        dataBase.update("diet_food", contentValues, "food_name = ? and date = ?", arrayOf(dietRecord.foodName, dietRecord.date.toString()))
    }
    result.close()
    dataBase.close()
}

//查询饮食食品记录
fun queryDiet(date: Int, type: TYPE):LinkedList<DietRecord>{
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val dietFoods = LinkedList<DietRecord>()
    val result =  dataBase.query("diet_food", arrayOf("food_name", "quantity"), "date = ? and type = ?", arrayOf(date.toString(), type.ordinal.toString()), null, null, null)
    dataBase.close()
    while (!result.isAfterLast){
        dietFoods.add(DietRecord(date, type, result.getString(1),result.getDouble(2)))
    }
    return dietFoods
}

//查询运动记录
fun queryAllSportRecords(model: Int): LinkedList<SportRecord>{
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val sportRecords = LinkedList<SportRecord>()
    val now = LocalDate.now()
    val calendar = Calendar.getInstance()
    calendar.set(now.year, now.monthValue, now.dayOfMonth)
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
        sportRecords.add(SportRecord(result.getInt(0), result.getString(1), result.getInt(3)))
        result.moveToNext()
    }
    result.close()
    return sportRecords
}

//新增运动记录
fun insertSportRecord(sportRecord: SportRecord){
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val contentValues = ContentValues()

    contentValues.put("date", sportRecord.date)
    contentValues.put("sport_name", sportRecord.sportName)
    contentValues.put("time", sportRecord.time)
    dataBase.insert("sport_record", null, contentValues)

    dataBase.close()
}

//查询用户当天运动情况
fun querySport(): List<SportShow>{
    val sportList = LinkedList<SportShow>()
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val result = dataBase.query("sport", null, null, null, null, null, null)
    result.moveToFirst()
    while (!result.isAfterLast) {
        var record = dataBase.query("sport_record", null, "date = ? and name = ?", arrayOf(getData().toString(), result.getString(0)), null, null, null)
        if(record.count == 0)
            sportList.add(SportShow(result.getString(0), result.getInt(1), false))
        else
            sportList.add(SportShow(result.getString(0), result.getInt(1), true))
        result.moveToNext()
        record.close()
    }
    result.close()
    dataBase.close()
    return sportList
}

fun querySportRecordByDate(date: Int): List<SportRecord>{
    val sportRecordList = LinkedList<SportRecord>()
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val result = dataBase.query("sport_record", null, "date = ?", arrayOf(date.toString()), null, null, null)
    result.moveToFirst()
    while (!result.isAfterLast) {
        sportRecordList.add(SportRecord(result.getInt(0), result.getString(1), result.getInt(2)))
        result.moveToNext()
    }
    result.close()
    dataBase.close()
    return sportRecordList
}


//新增运动项目,成功返回1，运动名已存在返回0
fun insertSport(sport: Sport):Int{
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val result = dataBase.query("sport", null, "name = ?", arrayOf(sport.name), null, null, null)
    if(result.getCount() != 0){
        val contentValues = ContentValues()
        contentValues.put("sport_name", sport.name)
        contentValues.put("time", sport.time)
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
        val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("date", weight.date)
        contentValues.put("weight", weight.weight)
        contentValues.put("rate", ((waist * 0.74) - (weight.weight * 0.082))/weight.weight)
        dataBase.insert("weight", null, contentValues)
        dataBase.close()
        return 1
    }
    else {
        return 0
    }
}

//按时间段查询体重体脂记录
fun queryFatRate(period: PERIOD): LinkedList<Weight>{
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val fatRates = LinkedList<Weight>()
    val now = LocalDate.now()
    val calendar = Calendar.getInstance()
    calendar.set(now.year, now.monthValue, now.dayOfMonth)
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
    result.moveToFirst()
    while (!result.isAfterLast) {
        fatRates.add(Weight(result.getInt(0), result.getDouble(1), result.getDouble(2)))
        result.moveToNext()
    }
    result.close()
    dataBase.close()
    return fatRates
}

//查询已记录日期
fun queryRecordDates(Table: TABLE):LinkedList<Int>{
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val dates = LinkedList<Int>()
    var table: String = ""
    when(Table){
        TABLE.DIET -> table = "diet_records"
        TABLE.SPORT -> table = "sport_records"
        TABLE.DRINK -> table = "drink_records"
    }
    val result = dataBase.query(table, arrayOf("date"), null, null, null, null, null)
    result.moveToFirst()
    while (!result.isAfterLast){
        dates.add(result.getInt(0))
        result.moveToNext()
    }
    result.close()
    dataBase.close()
    return dates
}

//查询饮水记录
fun queryDrinkRecords(period: PERIOD):LinkedList<DrinkRecord>{
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val drinkRecords = LinkedList<DrinkRecord>()
    val now = LocalDate.now()
    val calendar = Calendar.getInstance()
    calendar.set(now.year, now.monthValue, now.dayOfMonth)
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
    result.moveToFirst()
    while (!result.isAfterLast) {
        drinkRecords.add(DrinkRecord(result.getInt(0), result.getInt(1)))
        result.moveToNext()
    }
    result.close()
    dataBase.close()
    return drinkRecords
}

//新增饮水记录
fun insertDrinkRecord(drinkRecord: DrinkRecord){
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val contentValues = ContentValues()
    contentValues.put("date", drinkRecord.date)
    contentValues.put("volume", drinkRecord.volume)
    dataBase.insert("drink_records", null, contentValues)
    dataBase.close()
}

//查询三围记录
fun queryBodySize(period: PERIOD): LinkedList<BodySize>{
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val bodySize = LinkedList<BodySize>()
    val now = LocalDate.now()
    val calendar = Calendar.getInstance()
    calendar.set(now.year, now.monthValue, now.dayOfMonth)
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
    result.moveToFirst()
    while (!result.isAfterLast) {
        bodySize.add(BodySize(result.getInt(0), result.getInt(1), result.getInt(2), result.getInt(3)))
        result.moveToNext()
    }
    result.close()
    dataBase.close()
    return bodySize
}

//新增三围记录
fun insertBodySize(bodySize: BodySize){
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
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
    val sharedPreferences = MyApplication.context.getSharedPreferences("tempData",0)
    val lastWeight = sharedPreferences.getString("lastWeight", "")!!.toDouble()
    if(abs(weight-lastWeight) >= 2)
        return 0.0
    else
        return sharedPreferences.getString("waist", "")!!.toDouble()
}

//更新腰围
fun updataWaist(waist:Double, weight: Double){
    val sharedPreferences = MyApplication.context.getSharedPreferences("tempData",0)
    val dataEdit = sharedPreferences.edit()
    dataEdit.putString("waist", waist.toString())
    dataEdit.putString("lastWeight", weight.toString())
}

//获取当前日期unix
fun getData(): Int{
    val now = LocalDate.now()
    val calendar = Calendar.getInstance()
    calendar.set(now.year, now.monthValue, now.dayOfMonth)
    val nowUNIX = calendar.timeInMillis/1000
    return nowUNIX.toInt()
}

fun getDateFormat(): String{
    return SimpleDateFormat("YYYY年MM月DD日").format((getData()*1000) as Long)
}

fun setWaterGoal(waterGoal: Int){
    val sharedPreferences = MyApplication.context.getSharedPreferences("tempData",0)
    val dataEdit = sharedPreferences.edit()
    dataEdit.putString("waterGoal", waterGoal.toString())
}

fun getWaterGoal(): Int{
    val sharedPreferences = MyApplication.context.getSharedPreferences("tempData",0)
    val waterGoal = sharedPreferences.getString("waterGoal", "")!!.toInt()
    return waterGoal
}