package com.ezen.propick.notification.Enum;

import lombok.Getter;

@Getter
public enum PopUpStatus {
    IN_PROGRESS("in_progress"),
    COMPLETED("completed");

    private final String value;

    PopUpStatus(String value) {
        this.value = value;
    }

}