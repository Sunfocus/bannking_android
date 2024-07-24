package com.bannking.app.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Words {

    private static final String[] tensNames = {
            "",
            " ten",
            " twenty",
            " thirty",
            " forty",
            " fifty",
            " sixty",
            " seventy",
            " eighty",
            " ninety"
    };

    private static final String[] numNames = {
            "",
            " one",
            " two",
            " three",
            " four",
            " five",
            " six",
            " seven",
            " eight",
            " nine",
            " ten",
            " eleven",
            " twelve",
            " thirteen",
            " fourteen",
            " fifteen",
            " sixteen",
            " seventeen",
            " eighteen",
            " nineteen"
    };

    private Words() {}


    public static String convertLessThanOneThousand(int number) {
        String soFar;

        if (number % 100 < 20){
            soFar = numNames[number % 100];
            number /= 100;
        }
        else {
            soFar = numNames[number % 10];
            number /= 10;

            soFar = tensNames[number % 10] + soFar;
            number /= 10;
        }
        if (number == 0) return soFar;
        return numNames[number] + " hundred" + soFar;
    }


    public static String convert(long number) {


        if(String.valueOf(number).startsWith("-")) {

            if (number == 0) { return "zero"; }

            String snumber = Long.toString(number);

            // pad with "0"
            String mask = "000000000000000";
            DecimalFormat df = new DecimalFormat(mask);
            snumber = df.format(number);

        /*// XXXnnnnnnnnn
        int billions = Integer.parseInt(snumber.substring(0,3));
        // nnnXXXnnnnnn
        int millions  = Integer.parseInt(snumber.substring(3,6));
        // nnnnnnXXXnnn
        int hundredThousands = Integer.parseInt(snumber.substring(6,9));
        // nnnnnnnnnXXX
        int thousands = Integer.parseInt(snumber.substring(9,12));*/

            // XXXnnnnnnnnn
            int trillions = Integer.parseInt(snumber.substring(0,4));
            // nnnXXXnnnnnn
            int billions  = Integer.parseInt(snumber.substring(4,7));
            // nnnnnnXXXnnn
            int millions = Integer.parseInt(snumber.substring(7,10));
            // nnnnnnnnnXXX
            int hundredThousands = Integer.parseInt(snumber.substring(10,13));
            // nnnnnnnnnXXX
            int thousands = Integer.parseInt(snumber.substring(13,16));


            String tradTrillion;
            switch (trillions) {
                case 0:
                    tradTrillion = "";
                    break;
                case 1 :
                    tradTrillion = convertLessThanOneThousand(trillions)
                            + " trillion *";
                    break;
                default :
                    tradTrillion = convertLessThanOneThousand(trillions)
                            + " trillion *";
            }

            String result = tradTrillion;

            String tradBillions;
            switch (billions) {
                case 0:
                    tradBillions = "";
                    break;
                case 1 :
                    tradBillions = convertLessThanOneThousand(billions)
                            + " billion *";
                    break;
                default :
                    tradBillions = convertLessThanOneThousand(billions)
                            + " billion *";
            }
            result = result + tradBillions;

            String tradMillions;
            switch (millions) {
                case 0:
                    tradMillions = "";
                    break;
                case 1 :
                    tradMillions = convertLessThanOneThousand(millions)
                            + " million *";
                    break;
                default :
                    tradMillions = convertLessThanOneThousand(millions)
                            + " million *";
            }
            result =  result + tradMillions;

            String tradHundredThousands;
            switch (hundredThousands) {
                case 0:
                    tradHundredThousands = "";
                    break;
                case 1 :
                    tradHundredThousands = "one thousand *";
                    break;
                default :
                    tradHundredThousands = convertLessThanOneThousand(hundredThousands)
                            + " thousand *";
            }
            result =  result + tradHundredThousands;

            String tradThousand;
            tradThousand = convertLessThanOneThousand(thousands);
            result =  result + tradThousand;

            // remove extra spaces!
            return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");


        }
        else {

        }


        // 0 to 999 999 999 999 999
        if (number == 0) { return "zero"; }

        String snumber = Long.toString(number);

        // pad with "0"
        String mask = "000000000000000";
        DecimalFormat df = new DecimalFormat(mask);
        snumber = df.format(number);

        /*// XXXnnnnnnnnn
        int billions = Integer.parseInt(snumber.substring(0,3));
        // nnnXXXnnnnnn
        int millions  = Integer.parseInt(snumber.substring(3,6));
        // nnnnnnXXXnnn
        int hundredThousands = Integer.parseInt(snumber.substring(6,9));
        // nnnnnnnnnXXX
        int thousands = Integer.parseInt(snumber.substring(9,12));*/

        // XXXnnnnnnnnn
        int trillions = Integer.parseInt(snumber.substring(0,3));
        // nnnXXXnnnnnn
        int billions  = Integer.parseInt(snumber.substring(3,6));
        // nnnnnnXXXnnn
        int millions = Integer.parseInt(snumber.substring(6,9));
        // nnnnnnnnnXXX
        int hundredThousands = Integer.parseInt(snumber.substring(9,12));
        // nnnnnnnnnXXX
        int thousands = Integer.parseInt(snumber.substring(12,15));


        String tradTrillion;
        switch (trillions) {
            case 0:
                tradTrillion = "";
                break;
            case 1 :
                tradTrillion = convertLessThanOneThousand(trillions)
                        + " trillion *";
                break;
            default :
                tradTrillion = convertLessThanOneThousand(trillions)
                        + " trillion *";
        }

        String result = tradTrillion;

        String tradBillions;
        switch (billions) {
            case 0:
                tradBillions = "";
                break;
            case 1 :
                tradBillions = convertLessThanOneThousand(billions)
                        + " billion *";
                break;
            default :
                tradBillions = convertLessThanOneThousand(billions)
                        + " billion *";
        }
        result = result + tradBillions;

        String tradMillions;
        switch (millions) {
            case 0:
                tradMillions = "";
                break;
            case 1 :
                tradMillions = convertLessThanOneThousand(millions)
                        + " million *";
                break;
            default :
                tradMillions = convertLessThanOneThousand(millions)
                        + " million *";
        }
        result =  result + tradMillions;

        String tradHundredThousands;
        switch (hundredThousands) {
            case 0:
                tradHundredThousands = "";
                break;
            case 1 :
                tradHundredThousands = "one thousand *";
                break;
            default :
                tradHundredThousands = convertLessThanOneThousand(hundredThousands)
                        + " thousand *";
        }
        result =  result + tradHundredThousands;

        String tradThousand;
        tradThousand = convertLessThanOneThousand(thousands);
        result =  result + tradThousand;

        // remove extra spaces!
        return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
    }

}
