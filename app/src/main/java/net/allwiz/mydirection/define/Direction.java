package net.allwiz.mydirection.define;

// reference: https://developers.google.com/maps/documentation/urls/android-intents
public class Direction {

    public class Transportation {
        public static final int CAR = 0;
        public static final int TRANSIT = 1;
        public static final int BKIE = 2;
        public static final int WALK = 3;

    }

    public class Mode {
        public static final String DRIVING = "mode=d";
        public static final String TRANSIT = "mode=transit";//"transit_mode=train|tram|subway";
        public static final String WALKING = "mode=w";
        public static final String BICYCLING = "mode=b";

    }


    public class Avoid {
        public static final String TOLLS = "t";
        public static final String HIGHWAYS = "h";
        public static final String FERRIES = "f";
    }
}

