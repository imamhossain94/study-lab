package com.newage.studlab.Model.StudLibrary

class Suggestion (var proCode:String,
                  var semName: String,
                  var courCode: String,
                  var suggestionBy: String,
                  var suggestionFor:String,
                  var suggestionDate:String,
                  var fileIcon:String,
                  var fileUri: String,
                  var uploadBy: String,
                  var uploadDate:String,
                  var fileSize:String,
                  var fileDownloadTime:String){
    constructor() :this("","","","","","","","","","","","")
}