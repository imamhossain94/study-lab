package com.newage.studlab.Model.AnnexModel

class AnnexResults(var cgpa:String,
                   var sgpa:String,
                   var semester:String,
                   var results:ArrayList<CourseResults>) {

    constructor():this("","","",ArrayList())

}

