package me.rampo.trackingmypantry;

import androidx.room.TypeConverter;

import java.util.Date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Converter {
    // Set timezone value as GMT 존맛탱... to make time as reasonable
    static DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

    @TypeConverter
    public static Date timeToDate(String value) {
        if (value != null) {
            try {
                return df.parse(value);
            } catch (ParseException ignored) {

            }
            return null;
        } else {
            return null;
        }
    }

    @TypeConverter
    public static String dateToTime(Date value) {
        if (value != null) {
            return df.format(value);
        } else {
            return null;
        }
    }

}
