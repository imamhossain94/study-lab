package com.newage.studlab.Model.UserModel

class Student(var sis_std_id: String,
                   var sis_std_name:String ,
                   var sis_std_prgrm_sn: String,
                   var sis_std_intk: String,
                   var sis_std_father: String,
                   var sis_std_gender: String,
                   var sis_std_LocGuardian: String,
                   var sis_std_Bplace: String,
                   var sis_std_blood: String,
                   var sis_std_Status: String,
                   var sis_std_image: String,
                   var sis_std_Phone: String,
                   var sis_std_pass: String){

    constructor(): this("","","","","","",
        "","","","","","","")

}
