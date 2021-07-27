package com.newage.studlab.Model.BloodModel

class Donation(var receiverId:String,
               var donorId:String,
               var confirmationDate:String,
               var description:String) {
    constructor(): this("","","","")
}