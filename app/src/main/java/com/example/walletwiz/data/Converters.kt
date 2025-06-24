package com.example.walletwiz.data

import androidx.room.TypeConverter
import java.util.Date
import com.example.walletwiz.data.entity.PaymentMethod
import com.example.walletwiz.utils.Frequency

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    fun fromPaymentMethod(value: String?): PaymentMethod? {
        return value?.let { PaymentMethod.valueOf(it) }
    }

    @TypeConverter
    fun paymentMethodToString(paymentMethod: PaymentMethod?): String {
        return paymentMethod?.name.toString()
    }

    @TypeConverter
    fun frequencyToString(frequency: Frequency?): String {
        return frequency?.name.toString()
    }
}