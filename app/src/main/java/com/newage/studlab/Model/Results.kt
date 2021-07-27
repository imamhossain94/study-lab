package com.newage.studlab.Model

class Results(var Student_ID:String,
              var Student_Name:String,
              var Student_Cgpa:String,
              var Student_Sgpa:String,
              var program_Code:String,
              var Intake_No:Long,
              var Semester_Title:String) {
    constructor(): this("","","","","LLB",
        36,"Fourth Semester")

    public fun setData(programCode:String, intakeNo:Long, semesterTitle:String){
        this.program_Code = programCode
        this.Intake_No = intakeNo
        this.Semester_Title = semesterTitle
    }
}

