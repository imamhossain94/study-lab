package com.newage.studlab.Model.AppsInfoModel

class HomeInfo(var totalStudent:String,
               var totalTeacher:String,
               var totalFaculty:String,
               var totalProgram:String,
               var totalBloodRequest:String,
               var totalResponse: String) {
    constructor(): this("","","","","","")
}