package com.cybermed.cdoc_patient.me.vitalcheck

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.BloodGlucoseRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.Instant
import kotlin.reflect.KClass

object HealthConnectHelper {

    fun checkPermissionsAsync(
        client: HealthConnectClient,
        onResult: (Set<String>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val granted = client.permissionController.getGrantedPermissions()
            withContext(Dispatchers.Main) {
                onResult(granted)
            }
        }
    }

    fun readGlucoseRecordsJava(
        client: HealthConnectClient,
        request: ReadRecordsRequest<BloodGlucoseRecord>
    ): List<BloodGlucoseRecord> = runBlocking {
        client.readRecords(request).records
    }

    fun readBloodPressureRecordsJava(
        client: HealthConnectClient,
        request: ReadRecordsRequest<BloodPressureRecord>
    ): List<BloodPressureRecord> = runBlocking {
        client.readRecords(request).records
    }

    fun readStepRecordsJava(
        client: HealthConnectClient,
        request: ReadRecordsRequest<StepsRecord>
    ): List<StepsRecord> = runBlocking {
        client.readRecords(request).records
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Record> toKClass(javaClass: Class<T>): KClass<T> {
        return (javaClass.kotlin as KClass<T>)
    }

    //Step record function
    @JvmStatic
    fun readStepsByTimeRangeJava(
        client: HealthConnectClient,
        start: Instant,
        end: Instant,
        callback: (List<StepsRecord>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = readStepsByTimeRange(client, start, end)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }

    @JvmStatic
    suspend fun readStepsByTimeRange(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): List<StepsRecord> {

        val request = ReadRecordsRequest(
            StepsRecord::class,
            TimeRangeFilter.between(start, end)
        )

        val response = client.readRecords(request)
        return response.records
    }

    //Glucose record function
    @JvmStatic
    fun readBodyGlucoseByTimeRangeJava(
        client: HealthConnectClient,
        start: Instant,
        end: Instant,
        callback: (List<BloodGlucoseRecord>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = readGlucoseByTimeRange(client, start, end)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }

    @JvmStatic
    suspend fun readGlucoseByTimeRange(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): List<BloodGlucoseRecord> {

        val request = ReadRecordsRequest(
            BloodGlucoseRecord::class,
            TimeRangeFilter.between(start, end)
        )

        val response = client.readRecords(request)
        return response.records
    }
    //Blood pressure record function
    @JvmStatic
    fun readBloodPressureByTimeRangeJava(
        client: HealthConnectClient,
        start: Instant,
        end: Instant,
        callback: (List<BloodPressureRecord>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = readBloodPressureByTimeRange(client, start, end)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }

    @JvmStatic
    suspend fun readBloodPressureByTimeRange(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): List<BloodPressureRecord> {

        val request = ReadRecordsRequest(
            BloodPressureRecord::class,
            TimeRangeFilter.between(start, end)
        )

        val response = client.readRecords(request)
        return response.records
    }

    //Heart Rate record function
    @JvmStatic
    fun readHeartRateByTimeRangeJava(
        client: HealthConnectClient,
        start: Instant,
        end: Instant,
        callback: (List<HeartRateRecord>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = readHRTimeRange(client, start, end)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }

    @JvmStatic
    suspend fun readHRTimeRange(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): List<HeartRateRecord> {

        val request = ReadRecordsRequest(
            HeartRateRecord::class,
            TimeRangeFilter.between(start, end)
        )

        val response = client.readRecords(request)
        return response.records
    }


    //Weight record function
    @JvmStatic
    fun readWeightRangeJava(
        client: HealthConnectClient,
        start: Instant,
        end: Instant,
        callback: (List<WeightRecord>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = readWeightRange(client, start, end)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }

    @JvmStatic
    suspend fun readWeightRange(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): List<WeightRecord> {

        val request = ReadRecordsRequest(
            WeightRecord::class,
            TimeRangeFilter.between(start, end)
        )

        val response = client.readRecords(request)
        return response.records
    }

    //Height record function
    @JvmStatic
    fun readHeightRangeJava(
        client: HealthConnectClient,
        start: Instant,
        end: Instant,
        callback: (List<HeightRecord>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = readHeightRange(client, start, end)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }

    @JvmStatic
    suspend fun readHeightRange(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): List<HeightRecord> {

        val request = ReadRecordsRequest(
            HeightRecord::class,
            TimeRangeFilter.between(start, end)
        )

        val response = client.readRecords(request)
        return response.records
    }

    //Temperature record function
    @JvmStatic
    fun readTemperatureRecordJava(
        client: HealthConnectClient,
        start: Instant,
        end: Instant,
        callback: (List<BodyTemperatureRecord>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = readTemperatureRecordRange(client, start, end)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }

    @JvmStatic
    suspend fun readTemperatureRecordRange(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): List<BodyTemperatureRecord> {

        val request = ReadRecordsRequest(
            BodyTemperatureRecord::class,
            TimeRangeFilter.between(start, end)
        )

        val response = client.readRecords(request)
        return response.records
    }

/*    //respiratory rate record function
    @JvmStatic
    fun readRespiratoryRateRecordJava(
        client: HealthConnectClient,
        start: Instant,
        end: Instant,
        callback: (List<RespiratoryRateRecord>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = readRespiratoryRecordRange(client, start, end)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }

    @JvmStatic
    suspend fun readRespiratoryRecordRange(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): List<RespiratoryRateRecord> {

        val request = ReadRecordsRequest(
            RespiratoryRateRecord::class,
            TimeRangeFilter.between(start, end)
        )

        val response = client.readRecords(request)
        return response.records
    }*/

    //OxygenSaturation rate record function
    @JvmStatic
    fun readOxygenSaturationRecordJava(
        client: HealthConnectClient,
        start: Instant,
        end: Instant,
        callback: (List<OxygenSaturationRecord>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = readOxygenSaturationRecordRange(client, start, end)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }

    @JvmStatic
    suspend fun readOxygenSaturationRecordRange(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): List<OxygenSaturationRecord> {

        val request = ReadRecordsRequest(
            OxygenSaturationRecord::class,
            TimeRangeFilter.between(start, end)
        )

        val response = client.readRecords(request)
        return response.records
    }

   /* //HeartRateVariabilityRms record function
    @JvmStatic
    fun readHeartRateVariabilityRmssdRecordJava(
        client: HealthConnectClient,
        start: Instant,
        end: Instant,
        callback: (List<HeartRateVariabilityRmssdRecord>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = readHeartRateVariabilityRmssdRecordRange(client, start, end)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }

    @JvmStatic
    suspend fun readHeartRateVariabilityRmssdRecordRange(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): List<HeartRateVariabilityRmssdRecord> {

        val request = ReadRecordsRequest(
            HeartRateVariabilityRmssdRecord::class,
            TimeRangeFilter.between(start, end)
        )

        val response = client.readRecords(request)
        return response.records
    }*/
/*    //Vo2Max record function
    @JvmStatic
    fun readVo2MaxRecordJava(
        client: HealthConnectClient,
        start: Instant,
        end: Instant,
        callback: (List<Vo2MaxRecord>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = readVo2MaxRecordRange(client, start, end)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }

    @JvmStatic
    suspend fun readVo2MaxRecordRange(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): List<Vo2MaxRecord> {

        val request = ReadRecordsRequest(
            Vo2MaxRecord::class,
            TimeRangeFilter.between(start, end)
        )

        val response = client.readRecords(request)
        return response.records
    }*/

    //ExerciseSession record function
    @JvmStatic
    fun readExerciseSessionRecordJava(
        client: HealthConnectClient,
        start: Instant,
        end: Instant,
        callback: (List<ExerciseSessionRecord>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = readExerciseSessionRecordRange(client, start, end)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }

    @JvmStatic
    suspend fun readExerciseSessionRecordRange(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): List<ExerciseSessionRecord> {

        val request = ReadRecordsRequest(
            ExerciseSessionRecord::class,
            TimeRangeFilter.between(start, end)
        )

        val response = client.readRecords(request)
        return response.records
    }

/*    //BasalMetabolicRate record function
    @JvmStatic
    fun readBasalMetabolicRateRecordJava(
        client: HealthConnectClient,
        start: Instant,
        end: Instant,
        callback: (List<BasalMetabolicRateRecord>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = readBasalMetabolicRateRecordRange(client, start, end)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }

    @JvmStatic
    suspend fun readBasalMetabolicRateRecordRange(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): List<BasalMetabolicRateRecord> {

        val request = ReadRecordsRequest(
            BasalMetabolicRateRecord::class,
            TimeRangeFilter.between(start, end)
        )

        val response = client.readRecords(request)
        return response.records
    }*/
    //Distance record function
    @JvmStatic
    fun readDistanceRecordJava(
        client: HealthConnectClient,
        start: Instant,
        end: Instant,
        callback: (List<DistanceRecord>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = readDistanceRecordRange(client, start, end)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }

    @JvmStatic
    suspend fun readDistanceRecordRange(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): List<DistanceRecord> {

        val request = ReadRecordsRequest(
            DistanceRecord::class,
            TimeRangeFilter.between(start, end)
        )

        val response = client.readRecords(request)
        return response.records
    }

    /*//body fat record function
    @JvmStatic
    fun readBodyFatRecordJava(
        client: HealthConnectClient,
        start: Instant,
        end: Instant,
        callback: (List<BodyFatRecord>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = readBodyFatRecordRange(client, start, end)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }

    @JvmStatic
    suspend fun readBodyFatRecordRange(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): List<BodyFatRecord> {

        val request = ReadRecordsRequest(
            BodyFatRecord::class,
            TimeRangeFilter.between(start, end)
        )

        val response = client.readRecords(request)
        return response.records
    }*/

   /* //Total calories burned record function
    @JvmStatic
    fun readTotalCaloriesBurnedRecordJava(
        client: HealthConnectClient,
        start: Instant,
        end: Instant,
        callback: (List<TotalCaloriesBurnedRecord>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = readTotalCaloriesBurnedRecordRange(client, start, end)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }

    @JvmStatic
    suspend fun readTotalCaloriesBurnedRecordRange(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): List<TotalCaloriesBurnedRecord> {

        val request = ReadRecordsRequest(
            TotalCaloriesBurnedRecord::class,
            TimeRangeFilter.between(start, end)
        )

        val response = client.readRecords(request)
        return response.records
    }*/

  /*  //Resting heart rate record function
    @JvmStatic
    fun readRestingHeartRateRecordJava(
        client: HealthConnectClient,
        start: Instant,
        end: Instant,
        callback: (List<RestingHeartRateRecord>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = readRestingHeartRateRecordRange(client, start, end)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }

    @JvmStatic
    suspend fun readRestingHeartRateRecordRange(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): List<RestingHeartRateRecord> {

        val request = ReadRecordsRequest(
            RestingHeartRateRecord::class,
            TimeRangeFilter.between(start, end)
        )

        val response = client.readRecords(request)
        return response.records
    }*/

   /* //Lean body mass record function
    @JvmStatic
    fun readLeanBodyMassRecordJava(
        client: HealthConnectClient,
        start: Instant,
        end: Instant,
        callback: (List<LeanBodyMassRecord>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = readLeanBodyMassRecordRange(client, start, end)
            withContext(Dispatchers.Main) {
                callback(result)
            }
        }
    }

    @JvmStatic
    suspend fun readLeanBodyMassRecordRange(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): List<LeanBodyMassRecord> {

        val request = ReadRecordsRequest(
            LeanBodyMassRecord::class,
            TimeRangeFilter.between(start, end)
        )

        val response = client.readRecords(request)
        return response.records
    }*/


}

