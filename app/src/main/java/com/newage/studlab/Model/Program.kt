package com.newage.studlab.Model

data class Program(var programCode:String,
                   var programTitle: String,
                   var progFaculty:String,
                   var progImage:String,
                   var studentNumber:String,
                   var facultyNumber:String){
    constructor() : this( "", "","", "","","")
}