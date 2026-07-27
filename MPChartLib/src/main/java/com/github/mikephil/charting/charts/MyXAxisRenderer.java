package com.github.mikephil.charting.charts;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

public class MyXAxisRenderer extends XAxisRenderer {
    private int labelCount;

    public MyXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans,
                           int labelCount) {
        super(viewPortHandler, xAxis, trans);
        this.labelCount= labelCount;
    }

    @Override
    protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
        int offset = 24;
        float widthUnit = mViewPortHandler.contentWidth() / labelCount - offset;
        Rect rect = new Rect();
        int labelFittingLength = formattedLabel.length();
        mAxisLabelPaint.getTextBounds(formattedLabel, 0, labelFittingLength, rect);
        while (rect.width() > widthUnit) {
            labelFittingLength--;
            mAxisLabelPaint.getTextBounds(formattedLabel, 0, labelFittingLength, rect);
        }
        // space 5dp from XAxis
        float yPreviousLine = y - mXAxis.getYOffset() + 15;
        if (labelFittingLength == formattedLabel.length()) {
            super.drawLabel(c, formattedLabel, x, yPreviousLine, anchor, angleDegrees);
        } else {
            int spaceMultipleLine = 8;
            List<String> subLabels = splitByNumber(formattedLabel, labelFittingLength);
            for (String subLabel : subLabels) {
                Utils.drawXAxisValue(c, subLabel, x, yPreviousLine, mAxisLabelPaint, anchor, angleDegrees);
                yPreviousLine = yPreviousLine + rect.height() + spaceMultipleLine;
            }
        }

    }

    private List<String> splitByNumber(String s, int size) {
        List<String> strings = new ArrayList<>();
        int index = 0;
        while (index < s.length()) {
            strings.add(s.substring(index, Math.min(index + size, s.length())));
            index += size;
        }
        return strings;
    }
}
