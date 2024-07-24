package com.bannking.app.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class Constant {
        static String numericAmount;
    public static String convertNumericToSpokenWords(String numericAmount1, String currency) {
        try {
            if (numericAmount1.endsWith(".00")) {
                numericAmount = numericAmount1.replace(".00", "");
            } else {
                numericAmount = numericAmount1;
            }

            /*NumberFormat numberFormat = NumberFormat.getInstance();
            BigDecimal numericValue = new BigDecimal(numberFormat.parse(numericAmount).toString());

            // Separate the whole number and decimal parts
            long wholeNumber = numericValue.longValue();
            int cents = numericValue.remainder(BigDecimal.ONE).movePointRight(2).abs().intValue();*/

            String[] parts = numericAmount.split("\\.");
            String wholeNumber = "";
            String cents = "";

            if (parts.length > 1) {
                wholeNumber = parts[0];
                cents = parts[1];
                // Now, valueAfterDecimal will contain "97"
                // You can use this value as needed
            } else {
                wholeNumber = parts[0];
                cents = "00";
            }

//            long wholeNumber = numericValue.longValue();
//            int cents = numericValue.remainder(BigDecimal.ONE).movePointRight(2).abs().intValue();

            String wholeNumberWords = "";
            if (wholeNumber.startsWith("-")) {
                wholeNumberWords ="Negative *" + convertToSpokenWords(Long.parseLong(wholeNumber));
            } else {
                wholeNumberWords = convertToSpokenWords(Long.parseLong(wholeNumber));
            }


            // Convert the cents to spoken words
            String centsWords = Words.convert(Long.parseLong(cents));

            String words = null;
            switch (currency) {
                case "1":
                    words = wholeNumberWords + " dollars and " + centsWords + " cents";
                    break;
                case "2":
                    words = "SR *" + wholeNumberWords + " Point " + centsWords + "";
                    break;
                case "3":
                    words = "AD *" + wholeNumberWords + " Point " + centsWords + "";
                    break;
                case "4":
                    words = "QR *" + wholeNumberWords + " Point " + centsWords + "";
                    break;
                case "5":
                    words = wholeNumberWords + " Euros  and " + centsWords + " cents";
                    break;
                case "6":
                    words = wholeNumberWords + " Pounds  and " + centsWords + " pence";
                    break;
            }

            return words;
        } catch (Exception e) {
            // Handle the case where the string is not a valid number
            e.printStackTrace();
            return ""; // or handle it differently based on your use case
        }
    }

    public static String convertToSpokenWords(long number) {
        // Implementation for converting a number to spoken words
        // Use your existing logic or library for this conversion
        // ...

        return Words.convert(number);
    }
}
