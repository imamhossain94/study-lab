package com.newage.studlab.Model

class AssignmentModel(var assignmentTitle:String,
                      var submissionDate:String,
                      var assignmentText:String,
                      var createdDate:String,
                      var isSubmitted:String,
                      var imageOne:String,
                      var imageTwo:String,
                      var imageThree:String) {

    constructor():this("","","","","","","","")

}