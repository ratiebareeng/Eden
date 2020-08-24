package com.example.eden;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Utils {
    public static Locale botswana = new Locale("en", "Bw");
    public static NumberFormat pulaCurrency = NumberFormat.getCurrencyInstance(botswana);
    public static SimpleDateFormat bwDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", botswana);


}
