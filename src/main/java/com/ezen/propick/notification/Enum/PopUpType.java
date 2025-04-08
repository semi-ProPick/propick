package com.ezen.propick.notification.Enum;

import lombok.Getter;

@Getter
public enum PopUpType {
    NEW_MEMBER("new_member"),
    EXISTING_MEMBER("existing_member");

    private final String value;

    PopUpType(String value) {
        this.value = value;
    }

}