package com.newage.studlab.Model

class NotificationModel(var title:String,
                        var message:String,
                        var arrivalTime:String,
                        var isChecked:String,
                        var notificationType:String) {
    constructor():this("","","","false","")
}