package com.cybermed.cdoc_patient.util;

import java.util.LinkedList;

public class SoundWaveNormalizer {

    private LinkedList<Integer> data;

    private final static int SAMPLE_NUM = 50;

    public SoundWaveNormalizer() {
        data = new LinkedList<>();
    }

    public int normalize(int input) {
        int absInput = Math.abs(input);
        data.add(absInput);
        if (data.size() > SAMPLE_NUM) {
            data.removeFirst();
        }
        float perHeight = Short.MAX_VALUE / (float) getMax();
        return (int)(input * perHeight);
    }

    private int getMax() {
        int max = 1;
        for(Integer i: data) {
            max = Math.max(max, i);
        }
        return max;
    }
}
