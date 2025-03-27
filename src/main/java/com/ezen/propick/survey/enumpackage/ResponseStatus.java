package com.ezen.propick.survey.enumpackage;

public enum ResponseStatus {
    ACTIVE("활성"),
    DELETED("삭제됨");

    private final String label;

    ResponseStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
