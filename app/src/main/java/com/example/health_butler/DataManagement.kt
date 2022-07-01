package com.example.health_butler

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.time.Clock
import java.time.LocalDate
import java.util.*
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

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("ALTER TABLE drink_records ADD COLUMN goal int not null default 2000")
    }

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
    val dateBaseHelper: DataBaseHelper = DataBaseHelper(MyApplication.context, "healthButler", null, 3, null)
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

//查找指定种类义食品
fun queryFoodsByType(type: FOODTYPE):LinkedList<Food>{
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val foodsList: LinkedList<Food> = LinkedList<Food>()
    val result = dataBase.query("food", null, "type = ?", arrayOf(type.value.toString()), null, null, null)
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
fun queryFood(foodName: String):LinkedList<Food>{
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val foodsList: LinkedList<Food> = LinkedList<Food>()
    val result = dataBase.query("food", null, "food_name like '%" + foodName + "%'", null, null, null, null)
    result.moveToFirst()
    while (!result.isAfterLast){
        foodsList.add(Food(result.getString(1),result.getString(3),result.getInt(4),result.getInt(5),result.getInt(6), result.getInt(7), FOODTYPE.USERDEFINED))
        result.moveToNext()
    }
    result.close()
    dataBase.close()
    return foodsList
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
        contentValues.put("food_name", dietRecord.foodName)
        contentValues.put("type", dietRecord.type.value)
        val state = dataBase.insert("diet_food", null, contentValues)
        val i = state
    }
    else{
        result.moveToFirst()
        contentValues.put("quantity", result.getInt(4)+dietRecord.quantity)
        val state = dataBase.update("diet_food", contentValues, "food_name = ? and date = ?", arrayOf(food.name, dietRecord.date.toString()))
        val i = state
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
        val state = dataBase.insert("diet_record", null, contentValues)
        val i = state
    }
    else{
        result.moveToFirst()
        contentValues.put("calorie", result.getInt(1) + food.calorie * dietRecord.quantity)
        contentValues.put("carbohydrate", result.getInt(2) + food.carbohydrate * dietRecord.quantity)
        contentValues.put("protein", result.getInt(3) + food.protein * dietRecord.quantity)
        contentValues.put("fat", result.getInt(4) + food.fat * dietRecord.quantity)
        val state = dataBase.update("diet_record", contentValues, "date = ?", arrayOf(dietRecord.date.toString()))
        val i = state
    }
    result.close()
    dataBase.close()
}

//查询饮食食品记录
fun queryDiet(date: Int):LinkedList<DietShow>{
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val dietFoods = LinkedList<DietShow>()
    val result =  dataBase.query("diet_food", arrayOf("food_name", "quantity"), "date = ?", arrayOf(date.toString()), null, null, null)
    Log.v("ccc","${result.count}")
    while (!result.isAfterLast){
        var food = dataBase.query("food", arrayOf("unit"), "food_name = ?", arrayOf(result.getString(0)), null, null, null)
        food.moveToFirst()
        dietFoods.add(DietShow(result.getString(0), result.getInt(1), food.getString(0)))
        food.close()
    }
    result.close()
    dataBase.close()
    return dietFoods
}

fun queryDietRecord(date:Int): RecordShow?{
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    var dietRecord: RecordShow? = null
    val result =  dataBase.query("diet_record", null, "date = ?", arrayOf(date.toString()), null, null, null)
    if (result.count != 0){
        result.moveToFirst()
        dietRecord = RecordShow(result.getInt(1), result.getInt(2), result.getInt(3), result.getInt(4))
    }
    result.close()
    dataBase.close()
    return dietRecord
}

//更新运动完成状态
fun upSportData(name: String, state: Boolean){
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val contentValues = ContentValues()
    if(state){
        val result = dataBase.query("sport", null, "sport_name = ?", arrayOf(name), null, null, null)
        result.moveToFirst()
        contentValues.put("state", 1)
        dataBase.update("sport_record", contentValues, "sport_name = ? and date = ?", arrayOf(name, getDate().toString()))
    }
    else{
        contentValues.put("state", 0)
        dataBase.update("sport_record", contentValues, "sport_name = ? and date = ?", arrayOf(name, getDate().toString()))
    }
}

//新增运动记录
fun insertSportRecord(sportRecord: SportRecord){
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val contentValues = ContentValues()

    contentValues.put("date", sportRecord.date)
    contentValues.put("sport_name", sportRecord.sportName)
    contentValues.put("time", sportRecord.time)
    if (sportRecord.state)
        contentValues.put("state", 1)
    else
        contentValues.put("state", 0)
    dataBase.insert("sport_record", null, contentValues)

    dataBase.close()
}

//删除运动
fun delSport(name:String){
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    dataBase.delete("sport","sport_name = ?", arrayOf(name))
    dataBase.close()
}

//更新运动目标时间（当天）
fun upDateSportTime(name: String, time: Int){
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val contentValues = ContentValues()

    contentValues.put("time", time)

    dataBase.update("sport", contentValues, "sport_name = ?", arrayOf(name))

}

//查询用户当天运动情况
fun querySport(): LinkedList<SportShow>{
    val sportList = LinkedList<SportShow>()
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val result = dataBase.query("sport", null, null, null, null, null, null)
    result.moveToFirst()
    //循环查询每一个运动完成状态
    Log.v("aaa","ccc")
    while (!result.isAfterLast) {
        var record = dataBase.query("sport_record", null, "date = ? and sport_name = ?", arrayOf(getDate().toString(), result.getString(0)), null, null, null)
        //如果运动记录不存在，运动状态为未完成且新增记录
        if(record.count == 0) {
            sportList.add(SportShow(result.getString(0), result.getInt(1), false))
            insertSportRecord(SportRecord(getDate(), result.getString(0), 0, false))
        }
        //如果运动记录时间为0则运动状态为未完成
        else{
            record.moveToFirst()
            if(record.getInt(4) == 0) {
                sportList.add(SportShow(result.getString(0), result.getInt(1), false))
            }
            else
                sportList.add(SportShow(result.getString(0), result.getInt(1), true))
        }
        //否则为完成
        result.moveToNext()
        record.close()
    }
    result.close()
    dataBase.close()
    return sportList
}

//按日期查询运动记录
fun querySportRecordByDate(date: Int): LinkedList<SportShow>{
    val sportShowList = LinkedList<SportShow>()
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val result = dataBase.query("sport_record", null, "date = ?", arrayOf(date.toString()), null, null, null)
    result.moveToFirst()
    var state: Boolean
    while (!result.isAfterLast) {
        if (result.getInt(4) == 0)
            state = false
        else
            state = true
        sportShowList.add(SportShow(result.getString(2), result.getInt(3), state))
        result.moveToNext()
    }
    result.close()
    dataBase.close()
    return sportShowList
}


//新增运动项目,成功返回1，运动名已存在返回0
fun insertSport(sport: Sport):Int{
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val result = dataBase.query("sport", null, "sport_name = ?", arrayOf(sport.name), null, null, null)
    val size = result.getCount()
    if(result.getCount() == 0){
        val contentValues = ContentValues()
        contentValues.put("sport_name", sport.name)
        contentValues.put("time", sport.time)
        val state = dataBase.insert("sport", null, contentValues)
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

//查询饮水记录,无饮水记录返回null
fun queryDrinkRecords(date: Int): DrinkRecord?{
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val drinkRecords: DrinkRecord
    val result = dataBase.query("drink_records", null, "date = ?", arrayOf(date.toString()), null, null, null)
    if (result.count != 0) {
        result.moveToFirst()
        drinkRecords = DrinkRecord(result.getInt(0), result.getInt(1), result.getInt(2))
        return drinkRecords
    }
    result.close()
    dataBase.close()
    return null
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

//更新饮水记录
fun upDataDrinkRecord(volume: Int){
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val contentValues = ContentValues()
    val result = dataBase.query("drink_records", null, "date = ?", arrayOf(getDate().toString()), null, null, null)
    if(result.count == 0)
        insertDrinkRecord(DrinkRecord(getDate(), volume, getWaterGoal()))
    else {
        result.moveToFirst()
        contentValues.put("volume", volume + result.getInt(1))
        contentValues.put("goal", getWaterGoal())
        dataBase.update("drink_records", contentValues, "date = ?", arrayOf(getDate().toString()))
    }
    result.close()
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

//新增闹钟
fun insertClock(clock: AlarmClock){
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val contentValues = ContentValues()
    contentValues.put("time", clock.time)
    contentValues.put("state", clock.state)
    dataBase.insert("clock", null, contentValues)
    dataBase.close()
}

//查询闹钟
fun queryClock(): LinkedList<AlarmClock>{
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val clockList = LinkedList<AlarmClock>()
    val result = dataBase.query("clock", null, null, null, null, null, null)
    result.moveToFirst()
    while (!result.isAfterLast){
        if (result.getInt(1) == 1)
            clockList.add(AlarmClock(result.getString(0), true))
        else
            clockList.add(AlarmClock(result.getString(0), false))
        result.moveToNext()
    }
    result.close()
    dataBase.close()
    return clockList
}

//更新闹钟状态
fun upDataClock(clock: AlarmClock){
    val dataBase = SingleDataBase.get().dateBaseHelper.writableDatabase
    val contentValues = ContentValues()
    if (clock.state)
        contentValues.put("state", 1)
    else
        contentValues.put("state", 0)
    dataBase.update("clock", contentValues, "time = ?", arrayOf(clock.time))
    dataBase.close()
}

//获取当前日期unix
fun getDate(): Int{
    val now = LocalDate.now()
    val calendar = Calendar.getInstance()
    calendar.set(now.year, now.monthValue-1, now.dayOfMonth, 0, 0, 0)
    val nowUNIX = calendar.timeInMillis/1000
    return nowUNIX.toInt()
}

//返回格式化日期
fun getDateFormat(date: Int): String{
    return  SimpleDateFormat("YYYY年MM月dd日", Locale.getDefault()).format(date.toLong() * 1000)
}

fun setWaterGoal(waterGoal: Int){
    val sharedPreferences = MyApplication.context.getSharedPreferences("tempData",0)
    val dataEdit = sharedPreferences.edit()
    dataEdit.putString("waterGoal", waterGoal.toString())
    dataEdit.commit()
}

fun getWaterGoal(): Int{
    val sharedPreferences = MyApplication.context.getSharedPreferences("tempData",0)
    val waterGoal = sharedPreferences.getString("waterGoal", "2000")!!.toInt()
    return waterGoal
}