package com.arcadan.dodgetheenemies.data.models

import com.arcadan.dodgetheenemies.data.DatabaseManager

class HeartsCount(
    localDateTime: Long,
    timerHasStarted: Boolean,
    counter: Int,
    daysCount: Long
) {
    private var _counter = counter
    var counter: Int
        get() = _counter
        set(value) {
            _counter = value
            DatabaseManager.setValue("heartsCount/counter", _counter)
        }
    private var _timerHasStarted = timerHasStarted
    var timerHasStarted: Boolean
        get() = _timerHasStarted
        set(value) {
            _timerHasStarted = value
            DatabaseManager.setValue(
                "heartsCount/timerHasStarted",
                if (_timerHasStarted) {
                    "true"
                } else {
                    "false"
                }
            )
        }
    private var _localDateTime = localDateTime
    var localDateTime: Long
        get() = _localDateTime
        set(value) {
            _localDateTime = value
            DatabaseManager.setValue("heartsCount/localDateTime", _localDateTime)
        }
    private var _daysCount = daysCount
    var daysCount: Long
        get() = _daysCount
        set(value) {
            _daysCount = value
            DatabaseManager.setValue("heartsCount/daysCount", _daysCount)
        }
}
