package com.newage.studlab.Model.BloodModel

class Blood(var bloodName:String,
            var bloodId:String,
            var bloodGroup:String,
            var bloodReqDate:String,
            var bloodNeededDate:String,
            var bloodLastDonateDate:String,
            var bloodHospitalName:String,
            var bloodContactNo:String,
            var bloodTotalDonate:String,
            var bloodRequestGrant:String){
    constructor(): this("","","","null","null","null","null","null","null","null")

    constructor(bloodName:String,
                bloodId:String,
                bloodGroup:String,
                bloodTotalDonate:String,
                bloodLastDonateDate: String) : this() {
        this.bloodName = bloodName
        this.bloodId = bloodId
        this.bloodGroup = bloodGroup
        this.bloodTotalDonate = bloodTotalDonate
        this.bloodLastDonateDate = bloodLastDonateDate
    }

    constructor(bloodReqName:String,
                bloodReqId:String,
                bloodReqGroup:String,
                bloodReqDate:String,
                bloodNeededDate: String,
                bloodHospitalName:String,
                bloodContactNo:String,
                bloodRequestGrant:String) : this() {
        this.bloodName = bloodReqName
        this.bloodId = bloodReqId
        this.bloodGroup = bloodReqGroup
        this.bloodReqDate = bloodReqDate
        this.bloodNeededDate = bloodNeededDate
        this.bloodHospitalName = bloodHospitalName
        this.bloodContactNo = bloodContactNo
        this.bloodRequestGrant = bloodRequestGrant
    }

}