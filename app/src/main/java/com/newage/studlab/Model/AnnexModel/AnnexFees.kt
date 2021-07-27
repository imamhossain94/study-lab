package com.newage.studlab.Model.AnnexModel

class AnnexFees(var Demand:String,
                var Due:String,
                var Paid:String,
                var Remarks:String,
                var Semester:String,
                var Waiver:String,
                var payments:ArrayList<Payments>) {

    constructor():this("","","","",
        "","",ArrayList())
}

