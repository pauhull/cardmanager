package de.pauhull.cardmanager.manager;

import android.graphics.Bitmap;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;

public class BarcodeManager {

    public static BarcodeManager instance;

    private int lastBarcodeFormat;
    private String lastBarcodeValue;

    private BarcodeManager() {
    }

    /*
        https://stackoverflow.com/a/14207326
     */

    public Bitmap toBitmap(String value, BarcodeFormat format, int width, int height) {

        try {

            Map<EncodeHintType, Object> hints = null;

            for (char c : value.toCharArray()) {
                if (c > 0xFF) {
                    hints = new EnumMap<>(EncodeHintType.class);
                    hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                    break;
                }
            }

            MultiFormatWriter writer = new MultiFormatWriter();
            System.out.println(value);
            BitMatrix bitMatrix = writer.encode(value, format, width, height, hints);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean isIso_8859_1(String s) {

        byte[] msgBinary = s.getBytes(StandardCharsets.ISO_8859_1);

        for (int i = 0; i < msgBinary.length; i++) {
            char ch = (char) (msgBinary[i] & 0xff);
            if (ch == '?' && s.charAt(i) != '?') {
                return false;
            }
        }

        return true;
    }

    public boolean isBarcode(int format) {

        switch (format) {
            case Barcode.QR_CODE:
            case Barcode.AZTEC:
            case Barcode.DATA_MATRIX:
                return false;

            default:
                return true;
        }
    }

    public BarcodeFormat formatFromInt(int format) {

        switch (format) {

            case Barcode.AZTEC:
                return BarcodeFormat.AZTEC;
            case Barcode.CODABAR:
                return BarcodeFormat.CODABAR;
            case Barcode.CODE_39:
                return BarcodeFormat.CODE_39;
            case Barcode.CODE_93:
                return BarcodeFormat.CODE_93;
            case Barcode.CODE_128:
                return BarcodeFormat.CODE_128;
            case Barcode.DATA_MATRIX:
                return BarcodeFormat.DATA_MATRIX;
            case Barcode.EAN_8:
                return BarcodeFormat.EAN_8;
            case Barcode.EAN_13:
                return BarcodeFormat.EAN_13;
            case Barcode.ITF:
                return BarcodeFormat.ITF;
            case Barcode.PDF417:
                return BarcodeFormat.PDF_417;
            case Barcode.QR_CODE:
                return BarcodeFormat.QR_CODE;
            case Barcode.UPC_A:
                return BarcodeFormat.UPC_A;
            case Barcode.UPC_E:
                return BarcodeFormat.UPC_E;
        }

        return null;
    }

    public static void create() {
        if (instance == null) {
            instance = new BarcodeManager();
        }
    }

    public static BarcodeManager getInstance() {
        return instance;
    }

    public int getLastBarcodeFormat() {
        return lastBarcodeFormat;
    }

    public String getLastBarcodeValue() {
        return lastBarcodeValue;
    }

    public void setLastBarcodeFormat(int lastBarcodeFormat) {
        this.lastBarcodeFormat = lastBarcodeFormat;
    }

    public void setLastBarcodeValue(String lastBarcodeValue) {
        this.lastBarcodeValue = lastBarcodeValue;
    }
}
