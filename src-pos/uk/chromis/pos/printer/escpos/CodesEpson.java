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

package uk.chromis.pos.printer.escpos;

import uk.chromis.pos.printer.DevicePrinter;
import uk.chromis.pos.printer.DeviceTicket;

/**
 *
 *
 */
public class CodesEpson extends Codes {

    private static final byte[] INITSEQUENCE = {};
    private static final byte[] CHAR_SIZE_0 = {0x1D, 0x21, 0x00};
    private static final byte[] CHAR_SIZE_1 = {0x1D, 0x21, 0x01};
    private static final byte[] CHAR_SIZE_2 = {0x1D, 0x21, 0x30};
    private static final byte[] CHAR_SIZE_3 = {0x1D, 0x21, 0x31};
    public static final byte[] BOLD_SET = {0x1B, 0x45, 0x01};
    public static final byte[] BOLD_RESET = {0x1B, 0x45, 0x00};
    public static final byte[] UNDERLINE_SET = {0x1B, 0x2D, 0x01};
    public static final byte[] UNDERLINE_RESET = {0x1B, 0x2D, 0x00};
    private static final byte[] OPEN_DRAWER = {0x1B, 0x70, 0x00, 0x32, -0x06};
    private static final byte[] PARTIAL_CUT_1 = {0x1B, 0x69};
    private static final byte[] IMAGE_HEADER = {0x1D, 0x76, 0x30, 0x03};
    private static final byte[] NEW_LINE = {0x0D, 0x0A}; // Print and carriage return
    private static final byte[] IMAGE_LOGO = {0x1C, 0x70, 0x01, 0x00};
    private static final byte[] IMAGE_BEGIN = {0x1B, 0x30};
    private static final byte[] IMAGE_END = {0x1B, 0x7A, 0x01};
    private static final byte[] PAGEMODE = {0x1B, 0x4C};

    /**
     * Creates a new instance of CodesEpson
     */
    public CodesEpson() {
    }

    @Override
    public byte[] getInitSequence() {
        return INITSEQUENCE;
    }

    @Override
    public byte[] getSize0() {
        return CHAR_SIZE_0;
    }

    @Override
    public byte[] getSize1() {
        return CHAR_SIZE_1;
    }

    @Override
    public byte[] getSize2() {
        return CHAR_SIZE_2;
    }

    @Override
    public byte[] getSize3() {
        return CHAR_SIZE_3;
    }

    @Override
    public byte[] getBoldSet() {
        return BOLD_SET;
    }

    @Override
    public byte[] getBoldReset() {
        return BOLD_RESET;
    }

    @Override
    public byte[] getUnderlineSet() {
        return UNDERLINE_SET;
    }

    @Override
    public byte[] getUnderlineReset() {
        return UNDERLINE_RESET;
    }

    @Override
    public byte[] getOpenDrawer() {
        return OPEN_DRAWER;
    }

    @Override
    public byte[] getCutReceipt() {
        return PARTIAL_CUT_1;
    }

    @Override
    public byte[] getNewLine() {
        return NEW_LINE;
    }

    @Override
    public byte[] getImageHeader() {
        return IMAGE_HEADER;
    }

    @Override
    public int getImageWidth() {
        return 256;
    }

    @Override
    public byte[] getImageLogo(Byte iNumber) {
       byte[] IMAGE_LOGO = {0x1C, 0x70, iNumber, 0x00};
       // byte[] IMAGE_LOGO = {0x1D, 0x28,0x4C, 0x06, 0x00, 0x30, 0x45, 32, 32, 0x01, 0x01};
        return IMAGE_LOGO;
    }
    
    @Override
    public byte[] setPageMode() {
        return PAGEMODE;
    }

    @Override
    public Boolean printBarcode(PrinterWritter out, String type, String position, String code) {
// Modified 07.02.2014 JDL         
        if (DevicePrinter.BARCODE_EAN13.equals(type)) {
            out.write(new byte[]{0x1b, 0x61, 0x01}); //set to print in the centre of the line  
            out.write(new byte[]{0x1D, 0x77, 0x02}); // set the width of barcode  
            out.write(new byte[]{0x1D, 0x48, 0x02}); // set the position of user readable text
            out.write(new byte[]{0x1D, 0x68, 0x20}); // set the height of barcode
            out.write(new byte[]{0x1D, 0x6B, 0x02}); // setup to use ean13             
            out.write(DeviceTicket.transNumber(DeviceTicket.alignBarCode(code, 12).substring(0, 12)));
            out.write(new byte[]{0x00}); //end barcode 
            return true;
        }
        return false;
    }

}
