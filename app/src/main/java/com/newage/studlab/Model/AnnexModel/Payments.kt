package com.newage.studlab.Model.AnnexModel

class Payments(var Account_Code:String,
               var Payment_Amount:String,
               var Payment_No:String,
               var Reciept_No:String,
               var Waiver:String) {
    constructor():this("","","","",
            "")
}

