package com.example.weather

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.weather.city.City


private const val DATABASE_NAME = "weather.db"
private const val DATABASE_VERSION = 1

private const val CITY_TABLE_NAME = "city"
private const val CITY_KEY_ID = "id"
private const val CITY_KEY_NAME = "name"

private const val CITY_TABLE_CREATE = """
    CREATE TABLE $CITY_TABLE_NAME(
    $CITY_KEY_ID INTEGER PRIMARY KEY,
    $CITY_KEY_NAME TEXT
)
"""

private const val CITY_QUERY_SELECT_ALL = "SELECT * FROM $CITY_TABLE_NAME"

class Database(context: Context)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    val TAG = Database::class.java.simpleName

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CITY_TABLE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun createCity(city: City) : Boolean {
        val values = ContentValues()
        values.put(CITY_KEY_NAME, city.name)
        Log.d(TAG, "Creating city: $values")
        val id = writableDatabase.insert(CITY_TABLE_NAME, null, values)
        city.id = id
        return  id > 0
    }

    fun getAllCities(): MutableList<City> {
        val cities = mutableListOf<City>()

        readableDatabase.rawQuery(CITY_QUERY_SELECT_ALL, null).use{ cursor ->
            while (cursor.moveToNext()) {
                    val city = City(
                        cursor.getLong(cursor.getColumnIndexOrThrow(CITY_KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CITY_KEY_NAME))
                    )
                cities.add(city)
            }
        }
        return cities
    }

    fun deleteCity(city: City): Boolean {
        Log.d(TAG,"DeleteCity $city")
        val deleteCount = writableDatabase.delete(
            CITY_TABLE_NAME,
            "$CITY_KEY_ID = ?",
            arrayOf("${city.id}")
        )
        return deleteCount == 1
    }

    private fun updateCity(id: Long, values: ContentValues): Boolean{
        Log.d(TAG, "Updating city: $values")
        val updateCount = writableDatabase.update(CITY_TABLE_NAME,
        values,
        "$CITY_KEY_ID = ?",
        arrayOf("$id")
        )
        return updateCount > 0
    }

}