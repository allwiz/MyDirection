package net.allwiz.mydirection.define;

public class Action {
    public class Button {
        public static final int OK = 0;
        public static final int CANCEL = 1;
    }


    public class Check {
        public class Label {
            public static final int OK = 1000;
            public static final int EMPTY_LABEL_NAME = 1001;
            public static final int EXIST_LABEL_NAME = 1002;
            public static final int FAILED_INSERT_LABEL_NAME = 1003;
        }
    }


    public class Place {
        public static final int SHOW = 1;
        public static final int ADD = 2;
        public static final int ADD_WITH_LABEL = 3;
        public static final int MODIFY = 4;
        public static final int MODIFY_ADDRESS = 5;

        public static final int DELETE_LABEL = 6;
        public static final int DELETE_ADDRESS = 7;
    }
}
