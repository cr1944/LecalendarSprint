package ryan.lenovo.lecalendarsprint;

import org.joda.time.DateTime;

public class Utils {
    //2 weeks
    static final int PERIOD = 14;
    static final int MAX_PERIOD_NUMBER = 20;
    //u can modify these names
    static final String[] SPRINT_NAMES = new String[] {
        "2.4", "2.6", "2.8", "2.9", "2.10", "2.11", "2.12", "2.13", "2.14",
        "2.15", "2.16", "2.17", "2.18", "2.19", "2.x", "2.x", "2.x", "2.x"
    };
    static final String DATE_FORMAT = "MM/dd";
    static final DateTime FIRSTDAY = new DateTime(2014, 3, 24, 1, 0, 0, 0);
    static final int MIN_COLUMN = 3;
    static final int MAX_COLUMN = 5;
    static final int DEFAULT_COLUMN = 4;
    static final String[] COLUMNS = new String[] {
        "3","4","5"
    };

}
