package com.newage.studlab.Model.StudLibrary

data class Slide(var proCode:String,
                 var semName: String,
                 var courCode: String,
                 var chapter: String,
                 var slideTitle:String,
                 var slideUri:String,
                 var slideIcon:String,
                 var uploadBy:String,
                 var uploadDate:String,
                 var slideFileSize:String,
                 var slideDownloadTime:String){


    constructor() :this("","","","","","","","","","","")

}