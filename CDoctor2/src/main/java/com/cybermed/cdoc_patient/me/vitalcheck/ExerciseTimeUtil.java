package com.cybermed.cdoc_patient.me.vitalcheck;

import androidx.health.connect.client.records.ExerciseSessionRecord;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ExerciseTimeUtil {

    public static String getExerciseTime(ExerciseSessionRecord record) {

        if (record == null) return "--";

        ZoneId zoneId = ZoneId.systemDefault();

        LocalDateTime start =
                LocalDateTime.ofInstant(record.getStartTime(), zoneId);

        LocalDateTime end =
                LocalDateTime.ofInstant(record.getEndTime(), zoneId);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("hh:mm a");

        long minutes = Duration.between(
                record.getStartTime(),
                record.getEndTime()
        ).toMinutes();

        return minutes + " min";
    }

}
