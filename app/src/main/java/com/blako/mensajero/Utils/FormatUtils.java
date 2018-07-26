package com.blako.mensajero.Utils;

import java.text.DecimalFormat;

public class FormatUtils {

    public static String moneyFormat(double value){
        return "$ "+new DecimalFormat("#.##").format(value);
    }
}
