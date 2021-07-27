package com.newage.studlab.Model.StudLibrary

data class Book(var proCode:String,
                var semName: String,
                var courCode: String,
                var bookTitleEdition:String,
                var bookRating:String,
                var bookWriterName: String,
                var bookDescription: String,
                var bookTotalDownload:String,
                var bookTotalLoved: String,
                var bookLovedBY: String,
                var bookCoverImage: String,
                var bookPdfFile: String,
                var bookPdfSize: String){
    constructor():this("","","","","","","","","","","","","")
}