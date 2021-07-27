package com.newage.studlab.Plugins

class FileHelper {
    companion object {
        fun fileTypre(file:String):String{
            if (file == "PDF") {
                return "https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/file%20icon%2Fpdf.png?alt=media&token=4f5bdd27-7802-40e2-abd1-0771c5509863"
            } else if (file == "PPT" || file == "PPTX") {
                return "https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/file%20icon%2Fppt.png?alt=media&token=099cb7de-2e83-41fd-992d-f7337691f5b5"
            } else if (file == "DOC"){
                return  "https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/file%20icon%2Fdoc.png?alt=media&token=58057918-35e6-4616-8d8e-0a9dd4a0f2e3"
            } else{
                return "https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/file%20icon%2Fdocument.png?alt=media&token=f1bf5380-19c5-4811-bb76-e6577471fa28"
            }
        }
    }
}