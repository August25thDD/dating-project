package com.yang.commons.enums;

public enum CommentType {
     LIKE_TYPE(1),
    COMMENT_TYPE(2),
    LOVE_TYPE(3);

    private int type;

    private CommentType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

}
