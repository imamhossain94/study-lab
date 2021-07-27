package com.newage.studlab.Model


data class Course (val courseCode:String ,
                   val courseTitle: String,
                   val courseCredit: Double,
                   val programCode:String,
                   val semesterTitle: String){
    constructor(): this("","", 0.0,"","")
}
