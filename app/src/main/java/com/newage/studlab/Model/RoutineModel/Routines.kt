package com.newage.studlab.Model.RoutineModel

class Routines(var programCode:String,
               var intakeNo: String,
               var sectionNo: String,
               var semesterName: String,
               var sessionName:String,
               var routineType: String,
               var routineImage: String,
               var uploadDate:String,
               var uploadBy:String) {
    constructor() :this("","",
        "","","",
        "", "","","")

}