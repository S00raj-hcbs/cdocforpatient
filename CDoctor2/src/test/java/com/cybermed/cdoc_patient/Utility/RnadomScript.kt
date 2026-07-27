package com.cybermed.cdoc_patient.Utility

import java.util.regex.Pattern

fun main() {
    var input = """
        Farhang	05-01-0001
        Paie	05-01-0002
        Myers	05-01-0003
        Blackwood	05-01-0004
        Richardson	05-01-0005
        Mandala	05-01-0006
        King	05-01-0007
        Knight	05-01-0008
        Webb	05-01-0009
        Kuhner	05-01-0010
        	05-01-0011
        Prisco	05-01-0012
        Coatesyou	05-01-0013
        Hoskins	05-01-0014
        Jordan	05-01-0015
        Peppe	05-01-0016
        Monti	05-01-0017
        Maffia	05-01-0018
        Quintanilla	05-01-0019
        Fushcetto	05-01-0020
        Praileau	05-01-0021
        Piering	05-01-0022
        	05-01-0023
        	05-01-0024
        King	05-01-0025
        DeQuarto	05-01-0026
        Lewis	05-01-0027
        MANNING	05-01-0028
        Young	05-01-0029
        McMaster	05-01-0030
        Afran	05-01-0031
        Perlmutter	05-01-0032
        Manning	05-01-0033
        Timmerman	05-01-0034
        Gjekaj	05-01-0035
        LAMBRIGHT	05-01-0036
        Dorville	05-01-0037
        Lumia	05-01-0038
        Santiago Sayaz	05-01-0039
        Singh	05-01-0040
        Renna	05-01-0041
        White	05-01-0042
        Pokuah	05-01-0043
        Bouldin	05-01-0044
        Eason	05-01-0045
        Devine	05-01-0046
        Gilbert	05-01-0047
        Holt	05-01-0048
        Quinones	05-01-0049
        Nieves	05-01-0050
        	05-01-0051
        Lumia	05-01-0052
        Brewster	05-01-0053
        CRAWFORD	05-01-0054
        Lombardi	05-01-0055
        Sharkey	05-01-0056
        Ruffinatti	05-01-0057
        Kelley	05-01-0058
        Murray	05-01-0059
        Zammet	05-01-0060
        HOLDER	05-01-0061
        Diaz	05-01-0062
        Cruz	05-01-0063
        Arnopp	05-01-0064
        Tsapakis	05-01-0065
        	05-01-0066
        Wassenbergh	05-01-0067
        Bennett	05-01-0068
        Moustakakis	05-01-0069
        Ramos	05-01-0070
        Thompson	05-01-0071
        Likoka	05-01-0072
        Lampitelli	05-01-0073
        Gianotta	05-01-0074
        	05-01-0075
        Samuel	05-01-0076
        Allen	05-01-0077
        Filippi	05-01-0078
        Antonio	05-01-0079
        Vann	05-01-0080
        Malone	05-01-0081
        Cisario	05-01-0082
        Hodge	05-01-0083
        DECESARE	05-01-0084
        Esguerra	05-01-0085
        Castillomena	05-01-0086
        	05-01-0087
        Stewart	05-01-0088
        Barnes	05-01-0089
        Trapp	05-01-0090
        Grotyohann	05-01-0091
        Persaud	05-01-0092
        Chaparro	05-01-0093
        Thompson	05-01-0094
        Taylor	05-01-0095
        Alam	05-01-0096
        Cruz	05-01-0097
        Weinkauff	05-01-0098
        Townsendd	05-01-0099
        Arango	05-01-0100
        Bruno	05-01-0101
        Longo	05-01-0102
        Longo	05-01-0103
        Elsaid	05-01-0104
        Baskerville	05-01-0105
        Fanizza	05-01-0106
        	05-01-0107
        Taormino	05-01-0108
        Richard	05-01-0109
        Pertab	05-01-0110
        Ramratan	05-01-0111
        Johnson	05-01-0112
        Benjamin	05-01-0113
        Mifsud	05-01-0114
        Kaplan	05-01-0115
        Battle	05-01-0116
        Euceda	05-01-0117
        	05-01-0118
        Kelly	05-01-0119
        Pemberton	05-01-0120
        Berardi	05-01-0121
        Lynch	05-01-0122
        Williams	05-01-0123
        Anderson	05-01-0124
        	05-01-0125
        Mungo	05-01-0126
        	05-01-0127
        Horton	05-01-0128
        Tomlinson	05-01-0129
        	05-01-0130
        Jackson	05-01-0131
        Mazarese	05-01-0132
        Brown	05-01-0133
        BRUTUS	05-01-0134
        Facinelli	05-01-0135
        Payamps	05-01-0136
        Kleiber	05-01-0137
        	05-01-0138
        Cova	05-01-0139
        Cruz	05-01-0140
        Guillot	05-01-0141
        Costoso	05-01-0142
        Iser Jr	05-01-0143
        Harrichand	05-01-0144
        Ramirez	05-01-0145
        Carlyle	05-01-0146
        Charles	05-01-0147
        Thomas	05-01-0148
        DAVIS	05-01-0149
        Giordano	05-01-0150
        Clarke	05-01-0151
        Melendez	05-01-0152
        Denning	05-01-0153
        Villette	05-01-0154
        Test	05-01-0155
        WILDS	05-01-0156
        Heim	05-01-0157
        Colecchia	05-01-0158
        Thomas	05-01-0159
        Deluca	05-01-0160
        Varrindoyer	05-01-0161
        Cuatlacuatli	05-01-0162
        Stocker	05-01-0163
        	05-01-0164
        Sparks	05-01-0165
        Placide	05-01-0166
        	05-01-0167
        Ray	05-01-0168
        Spencer	05-01-0169
        Hatton	05-01-0170
        Peppers	05-01-0171
        Brown	05-01-0172
        CALAHORRANO	05-01-0173
        Coppadge	05-01-0174
        Bily	05-01-0175
        Damon	05-01-0176
        Johnson	05-01-0177
        MOULTRIE	05-01-0178
        Simmons	05-01-0179
        Sanabria	05-01-0180
        LangaigneCox	05-01-0181
        Lewis	05-01-0182
        	05-01-0183
        Greco	05-01-0184
        Burch	05-01-0185
        EDGHILL	05-01-0186
        DAVID	05-01-0187
        Rothstein	05-01-0188
        	05-01-0189
        Shelby	05-01-0190
        Carbonel	05-01-0191
        TATE	05-01-0192
        Kancza	05-01-0193
        Kahn	05-01-0194
        McNally	05-01-0195
        	05-01-0196
        Malfis	05-01-0197
        Thompson	05-01-0198
        Pepe	05-01-0199
        Bourne	05-01-0200
        Lewis	05-01-0201
        Campbell	05-01-0202
        Perez	05-01-0203
        Farinella	05-01-0204
        Novak	05-01-0205
        Tiwary	05-01-0206
        	05-01-0207
        Sellers	05-01-0208
        Torres	05-01-0209
        Mercado	05-01-0210
        Josephs	05-01-0211
        Noel	05-01-0212
        Liotta	05-01-0213
        Davis	05-01-0214
        Meltz	05-01-0215
        GUITEAU	05-01-0216
        Massucci	05-01-0217
        Kahn	05-01-0218
        Sicard	05-01-0219
        Carnovale	05-01-0220
        Millien	05-01-0221
        Perry	05-01-0222
        Costa	05-01-0223
        	05-01-0224
        	05-01-0225
        	05-01-0226
        Sutton	05-01-0227
        Dundara	05-01-0228
        SINGH	05-01-0229
        AGASI	05-01-0230
        Grieco	05-01-0231
        Campos	05-01-0232
        Arrrigo	05-01-0233
        Duran	05-01-0234
        Spencer	05-01-0235
        Davis	05-01-0236
        Favata	05-01-0237
        Dymond	05-01-0238
        	05-01-0239
        Springer	05-01-0240
        	05-01-0241
        Gounaris	05-01-0242
        Colamarino	05-01-0243
        Palmeri	05-01-0244
        Bartlett	05-01-0245
        CARTER	05-01-0246
        Guerriero	05-01-0247
        Rogers	05-01-0248
        Doggett	05-01-0249
        Nesbitt	05-01-0250
        	05-01-0251
        Pender Jr	05-01-0252
        Fortugno	05-01-0253
        Dames	05-01-0254
        PAUL	05-01-0255
        AdgersonSmith	05-01-0256
        GIBSON	05-01-0257
        Reid	05-01-0258
        CUFFIE	05-01-0259
        Hall	05-01-0260
        McCarthy	05-01-0261
        Davis	05-01-0262
        Peggy	05-01-0263
        WADLEY	05-01-0264
        MCCABE	05-01-0265
        FEIRMAN	05-01-0266
        SIMMON	05-01-0267
        Irizarry	05-01-0268
        COUNCIL	05-01-0269
        Jardine	05-01-0270
        KAVANAGH	05-01-0271
        Samaroo	05-01-0272
        Coffiel	05-01-0273
        Dorsainvil	05-01-0274
        McCarthy	05-01-0275
        AHMED	05-01-0276
        Williams	05-01-0277
        GOLLOP	05-01-0278
        PIERRENOEL	05-01-0279
        Ellis	05-01-0280
        Barnett	05-01-0281
        BARNEY	05-01-0282
        Umland	05-01-0283
        WILSON	05-01-0284
        Morales	05-01-0285
        Martin	05-01-0286
        Singh	05-01-0287
        Marrero	05-01-0288
        	05-01-0289
        VARELA	05-01-0290
        Woods	05-01-0291
        BRANKER	05-01-0292
        Padilla	05-01-0293
        	05-01-0294
        Green	05-01-0295
        Marrero	05-01-0296
        Sguera	05-01-0297
        Harrison	05-01-0298
        Primus	05-01-0299
        	05-01-0300
        Vega	05-01-0301
        Wiles	05-01-0302
        	05-01-0303
        GONZALES	05-01-0304
        	05-01-0305
        Park	05-01-0306
        	05-01-0307
        HOWES	05-01-0308
        Crovella	05-01-0309
        Crovella	05-01-0310
        	05-01-0311
        SMITH	05-01-0312
        	05-01-0313
        	05-01-0314
        	05-01-0315
        	05-01-0316
        StevensHarvey	05-01-0317
        	05-01-0318
        	05-01-0319
        Howard	05-01-0320
        Kalphat	05-01-0321
        Cates	05-01-0322
        Arredondo	05-01-0323
        FERRARA	05-01-0324
        Arredondo	05-01-0325
        Peterson	05-01-0326
        Cvitkovich	05-01-0327
        ROLAND	05-01-0328
        SMITH	05-01-0329
        Feierman	05-01-0330
        	05-01-0331
        	05-01-0332
        	05-01-0333
        	05-01-0334
        	05-01-0335
        Avena	05-01-0336
        	05-01-0337
        Allen	05-01-0338
        	05-01-0339
        	05-01-0340
        Kollappallil	05-01-0341
        	05-01-0342
        	05-01-0343
        Smith	05-01-0344
        	05-01-0345
        	05-01-0346
        Rodriguez	05-01-0347
        	05-01-0348
        	05-01-0349
        	05-01-0350
        	05-01-0351
        	05-01-0352
        	05-01-0353
        	05-01-0354
        	05-01-0355
        	05-01-0356
        	05-01-0357
        	05-01-0358
        	05-01-0359
        	05-01-0360
        	05-01-0361
        	05-01-0362
        	05-01-0363
        	05-01-0364
        	05-01-0375
    """.trimIndent()
    val regex : Pattern = Pattern.compile("(\\w+)\\t(\\d{2}-\\d{2}-\\d{4})");
    val matcher = regex.matcher(input);
    var count = 0

    while(matcher.find()){
        count ++
        print("'" + matcher.group(2) + "', " )
    }
    println()
    println(count)
}

fun generateSQL(last_name : String , code : String) : String{
    return """
        or last_name like '%$last_name% '
    """.trimIndent()
}