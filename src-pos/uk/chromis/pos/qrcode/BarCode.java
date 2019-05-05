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


package uk.chromis.pos.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.CodaBarWriter;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.Code39Writer;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.oned.EAN8Writer;
import com.google.zxing.oned.UPCAWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BarCode {

    private BitMatrix byteMatrix;
    private BufferedImage image;

    public BarCode() {

    }

    public BufferedImage getQRCode(String codeText, Integer size) {
        try {
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            byteMatrix = qrCodeWriter.encode(codeText, BarcodeFormat.QR_CODE, size, size, hintMap);
            int imageWidth = byteMatrix.getWidth();
            image = new BufferedImage(imageWidth, imageWidth, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();

            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, imageWidth, imageWidth);
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < imageWidth; i++) {
                for (int j = 0; j < imageWidth; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
            return image;
        } catch (WriterException e) {
            return null;
        }
    }

    public BufferedImage getBarcode(String codeText, String codeType, Integer bcWidth, Integer bcHeight) {
        bcWidth = (codeText.length() >= 8) ? 200 : bcWidth;
        bcWidth = (bcWidth == 0) ? 150 : bcWidth;
        bcHeight = (bcHeight == 0) ? 20 : bcHeight;

        try {
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            switch (codeType) {
                case "QR_CODE":
                    return getQRCode(codeText, bcWidth);
                case "CODE_39":
                case "CODE39":
                    Code39Writer codeWriter = new Code39Writer();
                    byteMatrix = codeWriter.encode(codeText, BarcodeFormat.CODE_39, bcWidth, bcHeight, hintMap);
                    return createBC();
                case "CODE_128":
                case "CODE128":
                    Code128Writer code128Writer = new Code128Writer();
                    byteMatrix = code128Writer.encode(codeText, BarcodeFormat.CODE_128, bcWidth, bcHeight, hintMap);
                    return createBC();
                case "EAN_13":
                case "EAN13":
                    EAN13Writer ean13Writer = new EAN13Writer();
                    byteMatrix = ean13Writer.encode(codeText, BarcodeFormat.EAN_13, bcWidth, bcHeight, hintMap);
                    return createBC();
                case "EAN_8":
                case "EAN8":
                    EAN8Writer ean8Writer = new EAN8Writer();
                    byteMatrix = ean8Writer.encode(codeText, BarcodeFormat.EAN_8, bcWidth, bcHeight, hintMap);
                    return createBC();
                case "CODABAR":
                    CodaBarWriter CodaBarWriter = new CodaBarWriter();
                    byteMatrix = CodaBarWriter.encode(codeText, BarcodeFormat.CODABAR, bcWidth, bcHeight, hintMap);
                    return createBC();
                case "UPC_A":
                case "UPCA":
                    UPCAWriter UPCAWriter = new UPCAWriter();
                    byteMatrix = UPCAWriter.encode(codeText, BarcodeFormat.UPC_A, bcWidth, bcHeight, hintMap);
                    return createBC();
            }
        } catch (WriterException ex) {
            Logger.getLogger(BarCode.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private BufferedImage createBC() {
        int imageWidth = byteMatrix.getWidth();
        int imageHeight = byteMatrix.getHeight();
        image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, imageWidth, imageHeight);
        graphics.setColor(Color.BLACK);
        for (int i = 0; i < imageWidth; i++) {
            for (int j = 0; j < imageHeight; j++) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        return image;
    }

}
