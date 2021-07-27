package com.newage.studlab.Model.BubtModel

class NoticeDetailed (var description:String,
                      var pubDate:String,
                      var title:String,
                      var downloads:ArrayList<Url>,
                      var images:ArrayList<Url>) {

    constructor():this("","","",ArrayList(),ArrayList())

}