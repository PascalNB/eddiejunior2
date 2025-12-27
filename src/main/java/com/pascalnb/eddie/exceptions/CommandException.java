package com.pascalnb.eddie.exceptions;

public class CommandException extends Exception {

    private final boolean custom;

    public CommandException(String message) {
        super(message);
        this.custom = true;
    }

    public CommandException(String message, Throwable cause) {
        super(message, cause);
        this.custom = true;
    }

    public CommandException(Throwable cause) {
        super(cause);
        this.custom = cause instanceof CommandException commandException && commandException.isCustom();
    }

    public boolean isCustom() {
        return custom;
    }

    public String getPrettyError() {
        String message;
        Throwable cause = this.getCause();

        if (!this.isCustom()) {
            return this.getMessage();
        }

        if (cause != null) {
            String causeMessage;

            if (cause instanceof NullPointerException) {
                this.printStackTrace();
                causeMessage = cause.getMessage();
            } else if (cause instanceof CommandException commandException) {
                return commandException.getPrettyError();
            } else {
                causeMessage = cause.getMessage();
            }

            message = this.getMessage() + ": " + causeMessage;

        } else { // no underlying cause
            message = this.getMessage();
        }

        return message;
    }

}