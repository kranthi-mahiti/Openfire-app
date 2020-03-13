/*
 * Copyright (c) 2019.
 * Project created and maintained by sanjay kranthi kumar
 * if need to contribute contact us on
 * kranthi0987@gmail.com
 */

package com.sanjay.openfire.exceptions;

public class OelpException extends Throwable {
    private static final long serialVersionUID = 1L;
    private Throwable thwStack;

    public OelpException(Exception excp) {
        super(excp);
        setThwStack(excp);

    }

    public OelpException(String msg, Throwable e) {
        super(msg, e);
        setThwStack(e);
    }

    public OelpException() {

        super();

    }

    public OelpException(String message) {
        super(message);
    }

    public Throwable getThwStack() {
        return thwStack;
    }

    public void setThwStack(Throwable throwable) {
        thwStack = throwable;
    }
}
