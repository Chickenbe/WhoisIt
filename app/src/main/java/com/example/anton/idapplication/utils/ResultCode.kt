package com.example.anton.idapplication.utils

import android.app.Activity

class ResultCode {

    companion object{
        const val RESULT_RECOGNITION_ERROR = Activity.RESULT_FIRST_USER + 1
        const val RESULT_TEXT_RECOGNITION_SUPPORT_ERROR = Activity.RESULT_FIRST_USER + 2
        const val RESULT_FACE_DETECTOR_SUPPORT_ERROR = Activity.RESULT_FIRST_USER + 2
        const val RESULT_FACE_DETECTION_ERROR = Activity.RESULT_FIRST_USER + 4
    }

}