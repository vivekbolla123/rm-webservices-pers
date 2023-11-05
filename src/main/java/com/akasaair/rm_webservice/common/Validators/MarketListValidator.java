package com.akasaair.rm_webservice.common.Validators;

import static com.akasaair.rm_webservice.common.Constants.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class MarketListValidator {

    public static boolean isLengthEqualTo(String value, int length) {
        return value.length() == length;
    }

    public static boolean checkDOWDigits(String value) {
        for (char digit : value.toCharArray()) {
            if (!(digit == DIGIT_1 || digit == DIGIT_9)) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkStrategyFlag(String value) {
        return value.equalsIgnoreCase(STRATEGY_FLAG_0) || value.equalsIgnoreCase(STRATEGY_FLAG_1);
    }

    public static boolean isValidTime(String time) {
        if (time == null) {
            return false;
        }
        try {
            LocalTime.parse(time, TIME_OUTPUT_FORMATTER);
        } catch (DateTimeParseException nfe) {
            return false;
        }
        return true;
    }

    public static boolean isValidDate(String date) {
        if (date == null) {
            return false;
        }
        try {
            LocalDate parseDate = LocalDate.parse(date, DATE_OUTPUT_FORMATTER);
            LocalDate currentDateUtc = LocalDate.now();
            return parseDate.isAfter(currentDateUtc) || parseDate.isEqual(currentDateUtc);
        } catch (DateTimeParseException nfe) {
            return false;
        }
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Integer.parseInt(strNum);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

    public static boolean isDouble(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

    public static boolean isPositive(String strNum) {
        return Integer.parseInt(strNum) >= 0;
    }

}
