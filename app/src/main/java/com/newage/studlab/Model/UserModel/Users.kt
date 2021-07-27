package com.newage.studlab.Model.UserModel

class Users(var user_id:String,
            var user_name:String,
            var user_gender:String,
            var user_dob:String,
            var user_phone_old: String,
            var user_phone_new: String,
            var user_password: String,
            var user_type:String,
            var user_image:String,
            var user_faculty_name: String,
            var user_prog_or_dept: String,
            var user_shift_or_post:String,
            var user_intake:String,
            var user_section: String,
            var user_semester: String,
            var user_address: String,
            var user_status:String,
            var user_blood: String,
            var user_room_no:String,
            var annex_id:String,
            var annex_pass: String,
            var annex_session_id:String){

    constructor(): this("null","null","null","null","null","null","null","null",
        "null","null","null","null", "null","null","null",
        "null","null","null","null","null","null","null")

    constructor(user_id:String, user_name:String, user_prog_or_dept:String, user_image:String) : this() {
        this.user_id = user_id
        this.user_name = user_name
        this.user_prog_or_dept = user_prog_or_dept
        this.user_image = user_image
    }

}