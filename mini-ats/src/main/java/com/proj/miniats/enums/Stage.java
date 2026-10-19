package com.proj.miniats.enums;

public enum Stage {
	APPLIED,
    SCREENING,
    INTERVIEW,
    OFFER,
    HIRED,
    REJECTED;

    /** HIRED and REJECTED are end states: no further movement expected. */
    public boolean isTerminal() {
        return this == HIRED || this == REJECTED;
    }
}
