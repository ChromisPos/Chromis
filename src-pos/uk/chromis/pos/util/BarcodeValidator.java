/*
**    Chromis POS  - The New Face of Open Source POS
**    Copyright (c)2015-2016
**    http://www.chromis.co.uk
**
**    This file is part of Chromis POS Version V0.60.2 beta
**
**    Chromis POS is free software: you can redistribute it and/or modify
**    it under the terms of the GNU General Public License as published by
**    the Free Software Foundation, either version 3 of the License, or
**    (at your option) any later version.
**
**    Chromis POS is distributed in the hope that it will be useful,
**    but WITHOUT ANY WARRANTY; without even the implied warranty of
**    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
**    GNU General Public License for more details.
**
**    You should have received a copy of the GNU General Public License
**    along with Chromis POS.  If not, see <http://www.gnu.org/licenses/>
**
**
 */
package uk.chromis.pos.util;

/**
 *
 * @author John
 */
public class BarcodeValidator {

    public BarcodeValidator() {

    }

    public static String encoderToUPC_E(String barcode) {
        //Check the length of the barcode UPC-A should be 12 characters
        if (barcode.length() != 12) {
            return null;
        }

        StringBuilder UPC_E = new StringBuilder();
        StringBuilder key = new StringBuilder();
        char checkSum = barcode.charAt(11);
        char numberSystem = barcode.charAt(0);
        String manufacturer = barcode.substring(1, 6);
        String product = barcode.substring(6, 11);

        key.append(numberSystem);
        key.append(checkSum);

        //Only barcodes starting 0 or 1 can be encoded  
        if (numberSystem != '0' && numberSystem != '1') {
            return null;
        }

        switch (manufacturer.substring(2, 5)) {
            case "000":
            case "100":
            case "200":
                //Check the product code is between 00000 - 00999                    
                if (!product.startsWith("00")) {
                    return null;
                }
                UPC_E.append("0");
                UPC_E.append(manufacturer.substring(0, 2));
                UPC_E.append(product.substring(2, 5));
                UPC_E.append(manufacturer.substring(2, 3));
                UPC_E.append(checkSum);
                return UPC_E.toString();
            case "300":
            case "400":
            case "500":
            case "600":
            case "700":
            case "800":
            case "900":
                //Check the product code is between 00000 - 00099 
                if (!product.startsWith("000")) {
                    return null;
                }
                UPC_E.append("0");
                UPC_E.append(manufacturer.substring(0, 3));
                UPC_E.append(product.substring(3, 5));
                UPC_E.append("3");
                UPC_E.append(checkSum);
                return UPC_E.toString();
        }

        switch (manufacturer.charAt(4)) {
            case '0':
                if (!product.startsWith("0000")) {
                    return null;
                }
                UPC_E.append("0");
                UPC_E.append(manufacturer.substring(0, 4));
                UPC_E.append(product.charAt(4));
                UPC_E.append("4");
                UPC_E.append(checkSum);
                return UPC_E.toString();
            default:
                if (product.charAt(4) >= '0' && product.charAt(4) <= '4') {
                    return null;
                }
                UPC_E.append("0");
                UPC_E.append(manufacturer.substring(0, 5));
                UPC_E.append(product.charAt(4));
                UPC_E.append(checkSum);
                return UPC_E.toString();
        }

    }

    public static String encoderToUPC_A(String code) {
        //UPC-E should always start with 0
        if (code.charAt(0) != '0') {
            return null;
        }

        String barcode = code.substring(1, code.length());

        //Check the length of the barcode UPC-A should be 7 characters
        if (barcode.length() != 7) {
            return null;
        }

        StringBuilder UPC_A = new StringBuilder();
        char lastChar = barcode.charAt(5);

        switch (lastChar) {
            case '0':
            case '1':
            case '2':
                UPC_A.append("0");
                UPC_A.append(barcode.substring(0, 2));
                UPC_A.append(barcode.charAt(5));
                UPC_A.append("0000");
                UPC_A.append(barcode.substring(2, 5));
                UPC_A.append(barcode.charAt(6));
                return UPC_A.toString();
            case '3':
                UPC_A.append("0");
                UPC_A.append(barcode.substring(0, 3));
                UPC_A.append("00000");
                UPC_A.append(barcode.substring(3, 5));
                UPC_A.append(barcode.charAt(6));
                return UPC_A.toString();
            case '4':
                UPC_A.append("0");
                UPC_A.append(barcode.substring(0, 4));
                UPC_A.append("00000");
                UPC_A.append(barcode.charAt(4));
                UPC_A.append(barcode.charAt(6));
                return UPC_A.toString();
            default:
                UPC_A.append("0");
                UPC_A.append(barcode.substring(0, 5));
                UPC_A.append("0000");
                UPC_A.append(barcode.charAt(5));
                UPC_A.append(barcode.charAt(6));
                return UPC_A.toString();
        }
    }

    private static Boolean validateCheckSum(String barcode) {
        return validateCheckSum(barcode, false);
    }

    private static Boolean validateCheckSum(String barcode, Boolean EAN13) {
        int odd = 0;
        int even = 0;

        for (int i = 0; i < barcode.length() - 1; i++) {
            if ((EAN13 ? i : i + 1) % 2 == 0) {
                even = even + Character.getNumericValue(barcode.charAt(i));
            } else {
                odd = odd + (Character.getNumericValue(barcode.charAt(i)) * 3);
            }
        }

        int checkDigit = (10 - ((odd + even) % 10)) % 10;
        if (checkDigit == Character.getNumericValue(barcode.charAt(barcode.length() - 1))) {
            return true;
        } else {
            return false;
        }
    }

    public static String BarcodeValidate(String barcode) {
        if (barcode.matches("[0-9]+")) {
            int bSize = barcode.length();
            switch (bSize) {
                case 8:
                    //lets chekc if EAN-8 first
                    // if that fails test for UPC-E
                    if (validateCheckSum(barcode)) {
                        return "EAN-8";
                    } else if (validateCheckSum(encoderToUPC_A(barcode))) {
                        return "UPC-E";
                    } else {
                        return "null";
                    }
                case 12:
                    if (validateCheckSum(barcode)) {
                        return "UPC-A";
                    } else {
                        return "null";
                    }
                case 13:
                    if (validateCheckSum(barcode, true)) {
                        return "EAN-13";
                    } else {
                        return "null";
                    }
                case 14:
                    if (validateCheckSum(barcode, true)) {
                        return "GTIN";
                    } else {
                        return "null";
                    }
                default:
                    return "CODE128";
            }
        } else {
            return "CODE128";
        }
    }
}

