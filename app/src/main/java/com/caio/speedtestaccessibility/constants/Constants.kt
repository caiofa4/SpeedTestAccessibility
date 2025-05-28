package com.caio.speedtestaccessibility.constants

enum class TestState {
    IDLE,
    READY,
    RUNNING,
    TAKE_SCREENSHOT,
    WAIT_SCREENSHOT,
    FINISHED,
    DETAILED_RESULTS,
    PARSING_RESULTS,
    NOT_RUNNING
}

object ViewIds {
    const val goButton = "org.zwanoo.android.speedtest:id/go_button"
    const val closeDetailsButton = "org.zwanoo.android.speedtest:id/side_menu_close_button"
    const val closeTestButton = "org.zwanoo.android.speedtest:id/closeIcon"
    const val pingMetric = "org.zwanoo.android.speedtest:id/ping_metric"

    const val detailedResult = "org.zwanoo.android.speedtest:id/suite_completed_feedback_assembly_detailed_result"
    const val testAgain = "org.zwanoo.android.speedtest:id/suite_completed_feedback_assembly_test_again"

    const val metricResult = "org.zwanoo.android.speedtest:id/ookla_view_result_details_blob_metric"
    const val pingLowResult = "org.zwanoo.android.speedtest:id/ookla_view_result_details_low_value"
    const val pingHighResult = "org.zwanoo.android.speedtest:id/ookla_view_result_details_high_value"
    const val pingJitterResult = "org.zwanoo.android.speedtest:id/ookla_view_result_details_jitter_value"
    const val testResult = "org.zwanoo.android.speedtest:id/ookla_view_result_details_blob_value"
    const val connectionsResult = "org.zwanoo.android.speedtest:id/ookla_view_result_details_blob_sublabel"
}

object AppPackageNames {
    const val ookla_speed_test = "org.zwanoo.android.speedtest"
}