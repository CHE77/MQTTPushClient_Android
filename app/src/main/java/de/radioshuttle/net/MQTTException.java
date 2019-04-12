/*
 *	$Id$
 *	This is an unpublished work copyright (c) 2018 HELIOS Software GmbH
 *	30827 Garbsen, Germany.
 */

package de.radioshuttle.net;

public class MQTTException extends ServerError {

    public MQTTException(int code, String msg) {
        super(code, msg);
    }

    public static final short REASON_CODE_SUBSCRIBE_FAILED				= 0x80;
}
