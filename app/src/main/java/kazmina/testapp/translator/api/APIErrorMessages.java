package kazmina.testapp.translator.api;

import kazmina.testapp.translator.R;

/**
 *
 */

public interface APIErrorMessages {
    int CODE_INVALID = 401;
    int CODE_BLOCKED = 402;
    int CODE_LIMIT = 404;
    int MESSAGE_LIMIT = R.string.message_limit;
    int MESSAGE_INVALID = R.string.message_invalid;
    int MESSAGE_BLOCKED = R.string.message_blocked;
    int MESSAGE_OTHER = R.string.message_other;
    int MESSAGE_EMPTY = R.string.message_empty_code;
}
