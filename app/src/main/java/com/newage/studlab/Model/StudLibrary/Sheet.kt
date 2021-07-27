package com.newage.studlab.Model.StudLibrary

class Sheet (var proCode:String,
             var semName: String,
             var courCode: String,
             var chapter: String,
             var sheetTitle:String,
             var sheetBy: String,
             var sheetIcon:String,
             var sheetUri: String,
             var uploadBy: String,
             var uploadDate:String,
             var sheetFileSize:String,
             var sheetDownloadTime:String){
    constructor() :this("","","","","","","","","","","","")
}