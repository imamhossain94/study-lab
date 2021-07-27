package com.newage.studlab.Plugins

import android.util.Base64
import com.newage.studlab.Model.Program
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class StudLabAssistant {

    companion object {

        var oldState:String = ""
        var selectedProgram = ""

        fun setIntroClickedState(state:String){
            this.oldState = state
        }



        fun idToDeptCodeIntake(id:String):String{

            //-----CSE-----------
            if(id.contains("17181103")){
                return "CSE/Intake 37"
            }else if(id.contains("17182103")){
                return "CSE/Intake 38"
            }else if(id.contains("17183103")){
                return "CSE/Intake 39"
            }else if(id.contains("18191103")){
                return "CSE/Intake 40"
            }else if(id.contains("18192103")){
                return "CSE/Intake 41"
            }else if(id.contains("18193103")){
                return "CSE/Intake 42"
            }else if(id.contains("19201103")){
                return "CSE/Intake 43"
            }else if(id.contains("19202103")){
                return "CSE/Intake 44"
            }else if(id.contains("19203103")){
                return "CSE/Intake 45"
            }else if(id.contains("20211103")){
                return "CSE/Intake 46"
            }else if(id.contains("20212103")){
                return "CSE/Intake 47"
            }else if(id.contains("20213103")){
                return "CSE/Intake 48"
            }

            //-----BBA-----------
            else if(id.contains("17181101")){
                return "BBA/Intake 44"
            }else if(id.contains("17182101")){
                return "BBA/Intake 45"
            }else if(id.contains("17183101")){
                return "BBA/Intake 46"
            }else if(id.contains("18191101")){
                return "BBA/Intake 47"
            }else if(id.contains("18192101")){
                return "BBA/Intake 48"
            }else if(id.contains("18193101")){
                return "BBA/Intake 49"
            }else if(id.contains("19201101")){
                return "BBA/Intake 50"
            }else if(id.contains("19202101")){
                return "BBA/Intake 51"
            }else if(id.contains("19203101")){
                return "BBA/Intake 52"
            }else if(id.contains("20211101")){
                return "BBA/Intake 53"
            }else if(id.contains("20212101")){
                return "BBA/Intake 54"
            }else if(id.contains("20213103")){
                return "BBA/Intake 55"
            }

            //-----ECO-----------
            else if(id.contains("17181106")){
                return "ECO/Intake 28"
            }else if(id.contains("17182106")){
                return "ECO/Intake 29"
            }else if(id.contains("17183106")){
                return "ECO/Intake 30"
            }else if(id.contains("18191106")){
                return "ECO/Intake 31"
            }else if(id.contains("18192106")){
                return "ECO/Intake 32"
            }else if(id.contains("18193106")){
                return "ECO/Intake 33"
            }else if(id.contains("19201106")){
                return "ECO/Intake 34"
            }else if(id.contains("19202106")){
                return "ECO/Intake 35"
            }else if(id.contains("19203106")){
                return "ECO/Intake 36"
            }else if(id.contains("20211106")){
                return "ECO/Intake 37"
            }else if(id.contains("20212106")){
                return "ECO/Intake 38"
            }else if(id.contains("20213106")){
                return "ECO/Intake 39"
            }

            //-----EEE-----------
            else if(id.contains("17181108")){
                return "EEE/Intake 22"
            }else if(id.contains("17182108")){
                return "EEE/Intake 23"
            }else if(id.contains("17183108")){
                return "EEE/Intake 24"
            }else if(id.contains("18191108")){
                return "EEE/Intake 25"
            }else if(id.contains("18192108")){
                return "EEE/Intake 26"
            }else if(id.contains("18193108")){
                return "EEE/Intake 27"
            }else if(id.contains("19201108")){
                return "EEE/Intake 28"
            }else if(id.contains("19202108")){
                return "EEE/Intake 29"
            }else if(id.contains("19203108")){
                return "EEE/Intake 30"
            }else if(id.contains("20211108")){
                return "EEE/Intake 31"
            }else if(id.contains("20212108")){
                return "EEE/Intake 32"
            }else if(id.contains("20213108")){
                return "EEE/Intake 33"
            }

            //-----ENG-----------
            else if(id.contains("17181102")){
                return "ENG/Intake 41"
            }else if(id.contains("17182102")){
                return "ENG/Intake 42"
            }else if(id.contains("17183102")){
                return "ENG/Intake 43"
            }else if(id.contains("18191102")){
                return "ENG/Intake 44"
            }else if(id.contains("18192102")){
                return "ENG/Intake 45"
            }else if(id.contains("18193102")){
                return "ENG/Intake 46"
            }else if(id.contains("19201102")){
                return "ENG/Intake 47"
            }else if(id.contains("19202102")){
                return "ENG/Intake 48"
            }else if(id.contains("19203102")){
                return "ENG/Intake 49"
            }else if(id.contains("20211102")){
                return "ENG/Intake 50"
            }else if(id.contains("20212102")){
                return "ENG/Intake 51"
            }else if(id.contains("20213102")){
                return "ENG/Intake 52"
            }

            //-----LLB-----------
            else if(id.contains("17181105")){
                return "LLB/Intake 33"
            }else if(id.contains("17182105")){
                return "LLB/Intake 34"
            }else if(id.contains("17183105")){
                return "LLB/Intake 35"
            }else if(id.contains("18191105")){
                return "LLB/Intake 36"
            }else if(id.contains("18192105")){
                return "LLB/Intake 37"
            }else if(id.contains("18193105")){
                return "LLB/Intake 38"
            }else if(id.contains("19201105")){
                return "LLB/Intake 39"
            }else if(id.contains("19202105")){
                return "LLB/Intake 40"
            }else if(id.contains("19203105")){
                return "LLB/Intake 41"
            }else if(id.contains("20211105")){
                return "LLB/Intake 42"
            }else if(id.contains("20212105")){
                return "LLB/Intake 43"
            }else if(id.contains("20213105")){
                return "LLB/Intake 44"
            }

            //-----TEX-----------
            else if(id.contains("17181107")){
                return "TEX/Intake 22"
            }else if(id.contains("17182107")){
                return "TEX/Intake 23"
            }else if(id.contains("17183107")){
                return "TEX/Intake 24"
            }else if(id.contains("18191107")){
                return "TEX/Intake 25"
            }else if(id.contains("18192107")){
                return "TEX/Intake 26"
            }else if(id.contains("18193107")){
                return "TEX/Intake 27"
            }else if(id.contains("19201107")){
                return "TEX/Intake 28"
            }else if(id.contains("19202107")){
                return "TEX/Intake 29"
            }else if(id.contains("19203107")){
                return "TEX/Intake 30"
            }else if(id.contains("20211107")){
                return "TEX/Intake 31"
            }else if(id.contains("20212107")){
                return "TEX/Intake 32"
            }else if(id.contains("20213107")){
                return "TEX/Intake 33"
            }

            //-----MAT-----------
            //-----CSE_Evn-----------
            //-----EEE_Evn-----------
            //-----TEX_Evn-----------

            return ""
        }


        fun userPostToFaculty(post:String):String{
            if (post.contains("CSE") || post.contains("Computer") ||
                post.contains("EEE") || post.contains("Electrical")||
                post.contains("Textile") || post.contains("Mathematics")||
                post.contains("Statistics")) {
                return "Faculty of Engineering & Applied Sciences"

            } else if (post == "BBA" || post.contains("Management")||
                post.contains("Accounting")||post.contains("Marketing")||
                post.contains("Finance")) {
                return "Faculty of Business"

            } else if (post.contains("English")) {
                return "Faculty of Arts & Humanities"

            } else if (post.contains("Economics")) {
                return "Faculty of Social Sciences"

            } else if (post == "LL.B(Hons)" || post.contains("Law")) {
                return "Faculty of Law"

            } else{
                return "Unknown"
            }
        }

        fun pCodeToHint(pCode: String): String{
            if (pCode == "CSE" || pCode == "CSE_E") {
                return "Computer"
            }else if (pCode == "EEE" || pCode == "EEE_E") {
                return "Electrical"
            }else if (pCode == "TEX" || pCode == "TEX_E") {
                return "Textile"
            }else if (pCode == "MAT") {
                return "Mathematics"
            }else if (pCode == "BBA") {
                return "Business"
            }else if (pCode == "ACT") {
                return "Accounting"
            }else if (pCode == "MGT") {
                return "Management"
            }else if (pCode == "MKT") {
                return "Marketing"
            }else if (pCode == "FIN") {
                return "Finance"
            }else if (pCode == "LLB") {
                return "Law"
            }else if (pCode == "ENG") {
                return "English"
            }else{
                return "Economics"
            }
        }

        public fun titleToDeptCode(dept: String): String {
            if (dept.contains("CSE") || dept.contains("Computer")) {
                return "CSE"

            } else if (dept.contains("Mathematics")) {
                return "MAT"

            } else if (dept.contains("EEE") || dept.contains("Electrical")) {
                return "EEE"

            } else if (dept.contains("Textile")) {
                return "TEX"

            } else if (dept.contains("English")) {
                return "ENG"

            } else if (dept == "BBA" || dept.contains("Management")||
                dept.contains("Accounting")||dept.contains("Marketing")|| dept.contains("Finance")){
                return "BBA"

            } else if (dept.contains("Economics")) {
                return "ECO"

            } else if (dept == "LL.B(Hons)" || dept.contains("Law")) {
                return "LLB"

            }else {
                return "Unknown"
            }
        }

        public fun strToDouble(str:String):Double{
            if(str == ""){
                return 0.0
            }else{
                return str.toDouble()
            }
        }

        fun pointToGrade(point:String):String{
            val p = strToDouble(point)
            if(p == 4.00){
                return "A+"
            }else if (p >= 3.75 && p < 4.00){
                return "A"
            }else if(p >= 3.50 && p < 3.75){
                return "A-"
            }else if(p >= 3.25 && p < 3.50){
                return "B+"
            }else if(p >= 3.00 && p < 3.25){
                return "B"
            }else if(p >= 2.75 && p < 3.00){
                return "B-"
            }else if(p >= 2.50 && p < 2.75){
                return "C+"
            }else if(p >= 2.25 && p < 2.50){
                return "C"
            }else if(p >= 2.00 && p < 2.25){
                return "D"
            }else{
                return "F"
            }
        }

        fun gradeToPoint(grade:String):String{
            when (grade) {
                "A+" -> {
                    return "4.00"
                }
                "A" -> {
                    return "3.75"
                }
                "A-" -> {
                    return "3.50"
                }
                "B+" -> {
                    return "3.25"
                }
                "B" -> {
                    return "3.00"
                }
                "B-" -> {
                    return "2.75"
                }
                "C+" -> {
                    return "2.50"
                }
                "C" -> {
                    return "2.25"
                }
                "D" -> {
                    return "2.00"
                }
                else -> {
                    return "0.00"
                }
            }
        }

        fun textSemesterToNumber(sem: String): String {

            if (sem.contains("First")) {
                return "1"
            } else if (sem.contains("Second")) {
                return "2"
            } else if (sem.contains("Third")) {
                return "3"
            } else if (sem.contains("Fourth")) {
                return "4"
            } else if (sem.contains("Fifth")) {
                return "5"
            } else if (sem.contains("Sixth")){
                return "6"
            } else if (sem.contains("Seventh")) {
                return "7"
            } else if (sem.contains("Eighth")) {
                return "8"
            }else if (sem.contains("Ninth")){
                return "9"
            } else if (sem.contains("Tenth")) {
                return "10"
            } else if (sem.contains("Eleventh")) {
                return "11"
            }else{
                return "12"
            }
        }

        fun textSemesterToOrdinal(sem: String): String {

            if (sem.contains("First")) {
                return "1st Semester"
            } else if (sem.contains("Second")) {
                return "2nd Semester"
            } else if (sem.contains("Third")) {
                return "3rd Semester"
            } else if (sem.contains("Fourth")) {
                return "4th Semester"
            } else if (sem.contains("Fifth")) {
                return "5th Semester"
            } else if (sem.contains("Sixth")){
                return "6th Semester"
            } else if (sem.contains("Seventh")) {
                return "7th Semester"
            } else if (sem.contains("Eighth")) {
                return "8th Semester"
            }else if (sem.contains("Ninth")){
                return "9th Semester"
            } else if (sem.contains("Tenth")) {
                return "10th Semester"
            } else if (sem.contains("Eleventh")) {
                return "11th Semester"
            }else{
                return "12th Semester"
            }
        }

        fun ordinalSemesterToText(sem: String): String {

            if (sem.contains("1st")) {
                return "First Semester"
            } else if (sem.contains("2nd")) {
                return "Second Semester"
            } else if (sem.contains("3rd")) {
                return "Third Semester"
            } else if (sem.contains("4th")) {
                return "Fourth Semester"
            } else if (sem.contains("5th")) {
                return "Fifth Semester"
            } else if (sem.contains("6th")){
                return "Sixth Semester"
            } else if (sem.contains("7th")) {
                return "Seventh Semester"
            } else if (sem.contains("8th")) {
                return "Eighth Semester"
            }else if (sem.contains("9th")){
                return "Ninth Semester"
            } else if (sem.contains("10th")) {
                return "Tenth Semester"
            } else if (sem.contains("11th")) {
                return "Eleventh Semester"
            }else{
                return "Twelfth Semester"
            }
        }

        fun textSemesterToOrdinalValue(sem: String): String {

            if (sem.contains("First")) {
                return "1st"
            } else if (sem.contains("Second")) {
                return "2nd"
            } else if (sem.contains("Third")) {
                return "3rd"
            } else if (sem.contains("Fourth")) {
                return "4th"
            } else if (sem.contains("Fifth")) {
                return "5th"
            } else if (sem.contains("Sixth")){
                return "6th"
            } else if (sem.contains("Seventh")) {
                return "7th"
            } else if (sem.contains("Eighth")) {
                return "8th"
            }else if (sem.contains("Ninth")){
                return "9th"
            } else if (sem.contains("Tenth")) {
                return "10th"
            } else if (sem.contains("Eleventh")) {
                return "11th"
            }else{
                return "12th"
            }
        }

        fun numberSemesterToText(sem: String): String {

            when {
                sem.contains("1") -> {
                    return "First Semester"
                }
                sem.contains("2") -> {
                    return "Second Semester"
                }
                sem.contains("3") -> {
                    return "Third Semester"
                }
                sem.contains("4") -> {
                    return "Fourth Semester"
                }
                sem.contains("5") -> {
                    return "Fifth Semester"
                }
                sem.contains("6") -> {
                    return "Sixth Semester"
                }
                sem.contains("7") -> {
                    return "Seventh Semester"
                }
                sem.contains("8") -> {
                    return "Eighth Semester"
                }
                sem.contains("9") -> {
                    return "Ninth Semester"
                }
                sem.contains("10") -> {
                    return "Tenth Semester"
                }
                sem.contains("11") -> {
                    return "Eleventh Semester"
                }
                else -> {
                    return "Twelfth Semester"
                }
            }
        }


        var isAdded = false;
    }

    public fun bbaProgram():ArrayList<Program>{
        val bbaProg:ArrayList<Program> = ArrayList<Program>()
        val act = Program("ACT","Department of Accounting","Faculty of Business","https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/app%20image%2Fprograms%2Fact.png?alt=media&token=1f340dd2-c63f-4c73-ae9a-d9295f4cf6b7","0","10")
        val mgt = Program("MGT","Department of Management","Faculty of Business","https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/app%20image%2Fprograms%2Fmgt.png?alt=media&token=fd2586c2-ca89-4193-92b8-5c223ee6554e","0","21")
        val mkt = Program("MKT","Department of Marketing","Faculty of Business","https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/app%20image%2Fprograms%2Fmkt.png?alt=media&token=45b30c75-f415-44d5-bf15-0e387f160bc8","0","6")
        val fin = Program("FIN","Department of Finance","Faculty of Business","https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/app%20image%2Fprograms%2Ffin.png?alt=media&token=793e00c4-370f-46c9-a374-4551b408554a","0","8")

        bbaProg.add(act)
        bbaProg.add(mgt)
        bbaProg.add(mkt)
        bbaProg.add(fin)

        return bbaProg
    }

    public fun emptyStringToInt(str:String):Int{
        if(str.isNotEmpty()){
            return str.toInt();
        }else{
            return 0
        }
    }


    fun splitLabClass(){

    }


}
