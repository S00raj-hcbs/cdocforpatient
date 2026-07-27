package com.cybermed.cdoc_patient.Utility

import java.util.regex.Pattern
import kotlin.collections.ArrayList

fun main() {
    val input =
            """
Returned
Howes, Lois 0308



Not Wanted
Heim, Linda – 0157
Novak, Theresia – 0205
Liotta, Diana – 0213
Deluca, Elizabeth – 0160
Costoso, Marie – 0142
Carnovale, Antonio – 0220
Irizarry, Margaret – 0268
Coffiel, Cynthia – 0273
No Name – 0270
Doggett, Wilma – 0249
Reid, Dennis – 0258
Hall, Thelma - 0260
            """.trimIndent()

    val pattern = Pattern.compile("\\d+")
    val matcher = pattern.matcher(input)

    val list = ArrayList<String>()

    while(matcher.find()){
        list.add("05-01-${matcher.group(0)}")
        println("05-01-${matcher.group(0)}")
    }

    list.sort()
    print(list)

}