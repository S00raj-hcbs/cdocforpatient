package com.cybermed.cdoc_patient.Utility

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.regex.Pattern

fun transformToSQL(): (List<String>) -> String = { list ->
    """
insert into IoT_Device_Inventory
values('${switchDevice(list[0])}', '${cmCode(list[0])}', '${list[1].trim()}', 'BODYTRACE', 'BTXXX', null, null, null, null, getdate(), null, null, 'org_code', null)
    """.trimIndent()
}

var BPCount = 176
var ScaleCount = 176

fun fillin(num: Int): String {
    if (num < 10)
        return "0$num"
    return "$num"
}

fun switchDevice(s: String): String {
    if (s.trim() == "BT005")
        return "IChoice_Scale"
    else if (s.trim() == "BT105")
        return "IChoice_BP"
    return ""
}

fun cmCode(s: String): String {
    if (s.trim() == "BT005")
        return "09-04-0${ScaleCount++}"
    else if (s.trim() == "BT105")
        return "09-01-0${BPCount++}"
    return ""
}

@RunWith(AndroidJUnit4::class)
class test {

    @Test
    fun insert_sql() {
        val file = File("C:\\Users\\danie\\OneDrive\\Work\\code.txt")
        val br = BufferedReader(FileReader(file))

        var wholeString = ""
        var st: String? = br.readLine()
        while (st != null) {
            wholeString = wholeString + st + "\t"
            st = br.readLine()
        }

        val patientArray = wholeString.split("\t").run {
            if (size % 2 == 0) this else dropLast(1)
        }

        val sql = patientArray.chunked(2, transform = transformToSQL())
        for (statement in sql) {
            println(statement)
        }
    }

    @Test
    fun bodyTrace() {
        val file = File("C:\\Users\\danie\\Desktop\\batch2.txt")
        val br = BufferedReader(FileReader(file))

        var wholeString = ""
        var st: String? = br.readLine()
        while (st != null) {
            wholeString += st
            st = br.readLine()
        }

        val regex: Pattern = Pattern.compile("(BT105|BT005).*?<TD>(8652\\d+?)&");
        val matcher = regex.matcher(wholeString);
        var count = 0

        while (matcher.find()) {
            count++
            println(transformToSQL()(listOf<String>(matcher.group(1), matcher.group(2))))
        }
        println()
        println(count)
    }
}
