package com.benjaminwan.ocrlibrary;

import lombok.Data;

import java.util.ArrayList;
@Data
public class OcrResult implements OcrOutput {
    private final double dbNetTime;
    private final ArrayList<TextBlock> textBlocks;
    private double detectTime;
    private String strRes;

    public OcrResult(double dbNetTime, ArrayList<TextBlock> textBlocks, double detectTime, String strRes) {
        this.dbNetTime = dbNetTime;
        this.textBlocks = textBlocks;
        this.detectTime = detectTime;
        this.strRes = strRes;
    }
}
