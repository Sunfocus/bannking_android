package com.bannking.app.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

public class RupeesToWordsConverter {

    private static final String[] units = {"", "Thousand", "Lakh", "Crore"};

    private static String convertLessThanOneThousand(int number) {
        String current;
        if (number % 100 < 20) {
            current = RupeesToWordsConverter.units[number % 100];
            number /= 100;
        } else {
            current = RupeesToWordsConverter.units[number % 10];
            number /= 10;
            current = RupeesToWordsConverter.units[number % 10] + current;
            number /= 10;
        }
        if (number == 0) return current;
        return RupeesToWordsConverter.units[number] + " Hundred" + (current.isEmpty() ? "" : " and " + current);
    }

    public static String convert(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return "Zero Rupees";
        }

        String words = Words.convert(amount.longValue());

        if (amount.scale() > 0) {
            words += " Rupees and " + Words.convert(amount.remainder(BigDecimal.ONE).movePointRight(amount.scale()).longValue()) + " Paise";
        } else {
            words += " Rupees";
        }



        /*NumberFormat numberFormat = new DecimalFormat("#,##0.00");
        String[] parts = numberFormat.format(amount).split("\\.");
        BigDecimal integerPart1 = convertToBigDecimal(parts[0]);
        int integerPart = convertToInteger(integerPart1);
//        int integerPart = Integer.parseInt(parts[0]);
        int decimalPart = Integer.parseInt(parts[1]);

        String words = convertToWords(integerPart);
        if (decimalPart > 0) {
            words += " Rupees and " + convertToWords(decimalPart) + " Paise";
        } else {
            words += " Rupees";
        }
*/
        return words;
    }

    private static String convertToWords(int number) {
        int crore = number / 10000000;
        int lakh = (number / 100000) % 100;
        int thousand = (number / 1000) % 100;
        int remaining = number % 1000;

        String result = "";
        if (crore > 0) {
            result += convertLessThanOneThousand(crore) + " Crore";
        }
        if (lakh > 0) {
            result += (result.isEmpty() ? "" : " ") + convertLessThanOneThousand(lakh) + " Lakh";
        }
        if (thousand > 0) {
            result += (result.isEmpty() ? "" : " ") + convertLessThanOneThousand(thousand) + " Thousand";
        }
        if (remaining > 0) {
            result += (result.isEmpty() ? "" : " ") + convertLessThanOneThousand(remaining);
        }

        return result;
    }

    private static BigDecimal convertToBigDecimal(String numericString) {
        try {
            // Remove commas and parse the string to BigDecimal
            NumberFormat numberFormat = NumberFormat.getInstance();
            return new BigDecimal(numberFormat.parse(numericString).toString());
        } catch (ParseException e) {
            // Handle the case where the string is not a valid number
            e.printStackTrace();
            return BigDecimal.ZERO; // or handle it differently based on your use case
        }
    }

    private static Integer convertToInteger(BigDecimal bigDecimalValue) {
        // Round the BigDecimal to the nearest whole number
        BigDecimal roundedValue = bigDecimalValue.setScale(0, BigDecimal.ROUND_HALF_UP);

        // Convert the rounded BigDecimal to Integer
        return roundedValue.intValue();
    }


}
