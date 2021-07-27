package com.newage.studlab.Model.StudLibrary

data class Lecture (var proCode:String,
                    var semName: String,
                    var courCode: String,
                    val lectureNo: String,
                    val chapter: String,
                    val lectureTitle:String,
                    val lectureBy: String,
                    val lectureIcon:String,
                    val lectureUri: String,
                    val uploadBy: String,
                    val uploadDate:String,
                    val lectureDate: String,
                    val lectureFileSize:String,
                    var lectureDownloadTime:String){
    constructor() :this("","","","","","","","","","","","","","")
}

