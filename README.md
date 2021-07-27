# Study Lab [Unofficial]

The Study-Lab is an educational application that is developed to support the students of BUBT.


## Screenshots
<p align="center">

  <img alt="image" src="https://github.com/imamhossain94/study-lab/blob/main/screenshots/image1.jpg" width="40%"> &nbsp;&nbsp;
  <img alt="image" src="https://github.com/imamhossain94/study-lab/blob/main/screenshots/image2.jpg" width="40%">
  <br /><br /><br />
  <img alt="image" src="https://github.com/imamhossain94/study-lab/blob/main/screenshots/image3.jpg" width="25%"> &nbsp;&nbsp;
  <img alt="image" src="https://github.com/imamhossain94/study-lab/blob/main/screenshots/image4.jpg" width="25%"> &nbsp;&nbsp;
  <img alt="image" src="https://github.com/imamhossain94/study-lab/blob/main/screenshots/image5.jpg" width="25%">
  <br /><br /><br />
  <img alt="image" src="https://github.com/imamhossain94/study-lab/blob/main/screenshots/image6.jpg" width="40%"> &nbsp;&nbsp;
  <img alt="image" src="https://github.com/imamhossain94/study-lab/blob/main/screenshots/image7.jpg" width="40%">
  <br /><br /><br />
  <img alt="image" src="https://github.com/imamhossain94/study-lab/blob/main/screenshots/image8.jpg" width="40%"> &nbsp;&nbsp;
  <img alt="image" src="https://github.com/imamhossain94/study-lab/blob/main/screenshots/image9.jpg" width="40%">
  <br /><br /><br />
  <img alt="image" src="https://github.com/imamhossain94/study-lab/blob/main/screenshots/image11.jpg" width="40%"> &nbsp;&nbsp;
  <img alt="image" src="https://github.com/imamhossain94/study-lab/blob/main/screenshots/image12.jpg" width="40%">
  <br /><br /><br />
  <img alt="image" src="https://github.com/imamhossain94/study-lab/blob/main/screenshots/image13.jpg" width="40%"> &nbsp;&nbsp;
  <img alt="image" src="https://github.com/imamhossain94/study-lab/blob/main/screenshots/image14.jpg" width="40%">
  <br /><br /><br />
  <img alt="image" src="https://github.com/imamhossain94/study-lab/blob/main/screenshots/image15.jpg" width="40%"> &nbsp;&nbsp;
  <img alt="image" src="https://github.com/imamhossain94/study-lab/blob/main/screenshots/image16.jpg" width="40%">
  <br /><br /><br />
  <img alt="image" src="https://github.com/imamhossain94/study-lab/blob/main/screenshots/image17.jpg" width="40%"> &nbsp;&nbsp;
  <img alt="image" src="https://github.com/imamhossain94/study-lab/blob/main/screenshots/image18.jpg" width="40%">
  <br /><br /><br />
  <img alt="image" src="https://github.com/imamhossain94/study-lab/blob/main/screenshots/image19.jpg" width="40%"> &nbsp;&nbsp;
  <img alt="image" src="https://github.com/imamhossain94/study-lab/blob/main/screenshots/image20.jpg" width="40%">
  <br /><br /><br />
  <img alt="image" src="https://github.com/imamhossain94/study-lab/blob/main/screenshots/image21.jpg" width="40%"> &nbsp;&nbsp;
  <img alt="image" src="https://github.com/imamhossain94/study-lab/blob/main/screenshots/image22.jpg" width="40%">
  
</p>


## Feature

* Studlab library
* Blood bank
* Notification
* Routine
* Course info
* University profile
* Result
* Utility
* Annex mobile
* My profile

Lot more!!


## Limitation

Minimum Android Version: 6.0 (M)
Maximum Android Version: 10.0 (Q)

Minimum Api Level: 23
Maximum Api Level: 29

In Android Version 11.0 (R) The system throwing an unknown error at runtime. 
```
A/libc: fdsan: attamped to close file descriptor 98.......
```

Storage Limit: 5GB
Simultaneous Connection: 100 Device
Apps Not optimized

User needs to login with Annex ID Password because we don’t get any API from annex so we have to get those data from annex by scrapping them using Python.

If annex is down or not working then some of our features might not work like smart routine, fees and waivers.

Because we used firebase’s realtime database so there might be a problem to scale the data and reuse them in any platform. 


## Package Used

* appcompat
* core-ktx
* firebase-auth
* firebase-database
* firebase-storage
* firebase-firestore
* firebase-messaging
* retrofit
* gson
* espresso
* picasso
* CircularArcProgressView
* MotionToast
* okhttp
* pinview
* groupie
* RoundKornerLayouts
* andriodx
* android-gif-drawable
* zoomage
* gridlayout
* Encryption
* ChartProgressBar
* viewpagerdotsindicator
* chip-navigation-bar
* stepview
* light
* Toesty
* overscroll-bouncy
* overscroll-docor-andriod
* MpAndriodChart
* ImageView
* CardView
* Button
* TextView

## API Used

API Developer: <a href="https://github.com/xaadu"> Abdullah Zayed.</a>

* Type: GET, Bubt Std Verify: https://www.bubt.edu.bd/global_file/getData.php?id=17181103084&type=stdVerify

```json
{
  "sis_std_id": "17181103084",
  "sis_std_name": "Md. Imam Hossain",
  "sis_std_prgrm_sn": "B.Sc. Engg. in CSE",
  "sis_std_prgrm_id": "006",
  "sis_std_intk": "37",
  "sis_std_email": "imamagun94@gmail.com",
  "sis_std_father": "Mahbub Rashid",
  "sis_std_gender": "M",
  "sis_std_LocGuardian": "Mahbub Rashid",
  "sis_std_Bplace": "Vasantek, Dhaka",
  "sis_std_Status": "R",
  "sis_std_blood": "",
  "gazo": "data:image/jpeg;base64,"
}
```

* Type: GET, Bubt Faculty Verify: https://www.bubt.edu.bd/global_file/getData.php?id=18020331033&type=empVerify

```json
[
  {
    "EmpId": "18020331033",
    "DemoId": "18020331033",
    "EmpName": "Md. Ahsanul Haque",
    "DOB": "1996-06-21T00:00:00",
    "PermanentAddress": "South Atapara, Bogura Sadar-5800, Bogura",
    "FatherName": "Md. Abdul Awal",
    "ECName": "Md. Abdul Awal",
    "ECNo": "01711936404",
    "ECRelation": "Father",
    "Gender": "Male",
    "DeptName": "Department of Computer Science & Engineering",
    "PosName": "Lecturer",
    "BloodGroup": "A+",
    "StatusId": "1",
    "EmpImage": "..."
  }
]
```

* Type: GET, Annex Login: https://bubt.herokuapp.com/bubt/v1/login?id=17181103084&pass=password

```json
{
  "PHPSESSID": "f6efdbd85acc66effd5af564aa070d13",
  "status": "success"
}
```

* Type: GET, Smart Routine: https://bubt.herokuapp.com/bubt/v1/routine?phpsessid=f6efdbd85acc66effd5af564aa070d13

```json
{
  "data": [
    {
      "Building": "",
      "Day": "Saturday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "08:30 AM to 10:00 AM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Saturday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "10:00 AM to 11:30 AM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Saturday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "11:30 AM to 01:00 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Saturday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "01:20 PM to 02:50 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Saturday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "02:50 PM to 04:20 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Saturday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "04:20 PM to 05:50 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Saturday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "06:00 PM to 07:30 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Saturday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "07:30 PM to 09:00 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Sunday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "08:30 AM to 10:00 AM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "B-2",
      "Day": "Sunday",
      "Intake": "37",
      "Room_No": "320",
      "Schedule": "10:00 AM to 11:30 AM",
      "Section": "2",
      "Subject_Code": "CSE 465",
      "Teacher_Code": "MAJ"
    },
    {
      "Building": "B-2",
      "Day": "Sunday",
      "Intake": "37",
      "Room_No": "417",
      "Schedule": "11:30 AM to 01:00 PM",
      "Section": "3",
      "Subject_Code": "CSE 414",
      "Teacher_Code": "THZ"
    },
    {
      "Building": "B-2",
      "Day": "Sunday",
      "Intake": "37",
      "Room_No": "417",
      "Schedule": "01:20 PM to 02:50 PM",
      "Section": "3",
      "Subject_Code": "CSE 414",
      "Teacher_Code": "THZ"
    },
    {
      "Building": "",
      "Day": "Sunday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "02:50 PM to 04:20 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Sunday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "04:20 PM to 05:50 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Sunday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "06:00 PM to 07:30 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Sunday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "07:30 PM to 09:00 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Monday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "08:30 AM to 10:00 AM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Monday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "10:00 AM to 11:30 AM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Monday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "11:30 AM to 01:00 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Monday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "01:20 PM to 02:50 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Monday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "02:50 PM to 04:20 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Monday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "04:20 PM to 05:50 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Monday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "06:00 PM to 07:30 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Monday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "07:30 PM to 09:00 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Tuesday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "08:30 AM to 10:00 AM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Tuesday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "10:00 AM to 11:30 AM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Tuesday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "11:30 AM to 01:00 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "B-2",
      "Day": "Tuesday",
      "Intake": "37",
      "Room_No": "318",
      "Schedule": "01:20 PM to 02:50 PM",
      "Section": "2",
      "Subject_Code": "CSE 477",
      "Teacher_Code": "SUA"
    },
    {
      "Building": "B-2",
      "Day": "Tuesday",
      "Intake": "37",
      "Room_No": "317",
      "Schedule": "02:50 PM to 04:20 PM",
      "Section": "2",
      "Subject_Code": "CSE 413",
      "Teacher_Code": "THZ"
    },
    {
      "Building": "",
      "Day": "Tuesday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "04:20 PM to 05:50 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Tuesday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "06:00 PM to 07:30 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Tuesday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "07:30 PM to 09:00 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Wednesday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "08:30 AM to 10:00 AM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Wednesday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "10:00 AM to 11:30 AM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "B-2",
      "Day": "Wednesday",
      "Intake": "37",
      "Room_No": "318",
      "Schedule": "11:30 AM to 01:00 PM",
      "Section": "2",
      "Subject_Code": "CSE 477",
      "Teacher_Code": "SUA"
    },
    {
      "Building": "B-2",
      "Day": "Wednesday",
      "Intake": "37",
      "Room_No": "319",
      "Schedule": "01:20 PM to 02:50 PM",
      "Section": "2",
      "Subject_Code": "CSE 413",
      "Teacher_Code": "THZ"
    },
    {
      "Building": "",
      "Day": "Wednesday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "02:50 PM to 04:20 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Wednesday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "04:20 PM to 05:50 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Wednesday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "06:00 PM to 07:30 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Wednesday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "07:30 PM to 09:00 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "B-2",
      "Day": "Thursday",
      "Intake": "37",
      "Room_No": "419",
      "Schedule": "08:30 AM to 10:00 AM",
      "Section": "2",
      "Subject_Code": "CSE 478",
      "Teacher_Code": "SUA"
    },
    {
      "Building": "B-2",
      "Day": "Thursday",
      "Intake": "37",
      "Room_No": "419",
      "Schedule": "10:00 AM to 11:30 AM",
      "Section": "2",
      "Subject_Code": "CSE 478",
      "Teacher_Code": "SUA"
    },
    {
      "Building": "B-2",
      "Day": "Thursday",
      "Intake": "37",
      "Room_No": "320",
      "Schedule": "11:30 AM to 01:00 PM",
      "Section": "2",
      "Subject_Code": "CSE 465",
      "Teacher_Code": "MAJ"
    },
    {
      "Building": "",
      "Day": "Thursday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "01:20 PM to 02:50 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Thursday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "02:50 PM to 04:20 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Thursday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "04:20 PM to 05:50 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Thursday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "06:00 PM to 07:30 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Thursday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "07:30 PM to 09:00 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Friday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "08:30 AM to 10:00 AM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Friday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "10:00 AM to 11:30 AM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Friday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "11:30 AM to 01:00 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Friday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "01:20 PM to 02:50 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Friday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "02:50 PM to 04:20 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Friday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "04:20 PM to 05:50 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Friday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "06:00 PM to 07:30 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    },
    {
      "Building": "",
      "Day": "Friday",
      "Intake": "",
      "Room_No": "",
      "Schedule": "07:30 PM to 09:00 PM",
      "Section": "",
      "Subject_Code": "",
      "Teacher_Code": ""
    }
  ],
  "status": "success"
}
```

And Six More Apis!!


## TODOs

* Chat option to chat with other students.
* My class. (concept Google class room)
* Info BUBT. (stuff)
* Online assignment solution.
* Detailed result.
* Optimistic solution. (optimize apps)
* Better UI experience.
* Google map SDK.
* Guest user account.
* Google AdMob.


## Notice

This was my second android project. So, I know there have a lot of unnecessary or logicless codeing. If you want you can contribut to this project please rewrite some activity of this project.


<div align="center">

[![Open Source Love svg1](https://badges.frapsoft.com/os/v1/open-source.svg?v=103)](#)
[![GitHub Forks](https://img.shields.io/github/forks/saadhaxxan/Car_Game_Python_Pygame.svg?style=social&label=Fork&maxAge=2592000)](https://github.com/imamhossain94/study-lab/fork)
[![GitHub Issues](https://img.shields.io/github/issues/saadhaxxan/Car_Game_Python_Pygame.svg?style=flat&label=Issues&maxAge=2592000)](https://github.com/imamhossain94/study-lab/issues)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat&label=Contributions&colorA=red&colorB=black	)](#)

</div>




## 📁 Download Now

Get it from <a href="https://drive.google.com/file/d/1g50P9glhaz1Qa1GPliWYZNoD4IoUaMjX/view?usp=sharing">Google drive</a>


## 💻 Installation steps


Clone or download this project in your working directory, Open it using android studio, Sync the project, build & run the project.

## 🧑 Author

#### Md. Imam Hossain

You can also follow my GitHub Profile to stay updated about my latest projects:

[![GitHub Follow](https://img.shields.io/badge/Connect-imamhossain94-blue.svg?logo=Github&longCache=true&style=social&label=Follow)](https://github.com/imamhossain94)

If you liked the repo then kindly support it by giving it a star ⭐!

Copyright (c) 2021 MD. IMAM HOSSAIN