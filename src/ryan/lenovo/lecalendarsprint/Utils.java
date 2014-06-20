package ryan.lenovo.lecalendarsprint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;

import com.google.common.base.Splitter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utils {
    //2 weeks
    static final int PERIOD = 14;
    static final int MAX_PERIOD_NUMBER = 20;
    static final String INVALID_SPRINT_NAME = "?";

    static final String DATE_FORMAT = "MM/dd";
    static final DateTime FIRSTDAY = new DateTime(2014, 3, 24, 1, 0, 0, 0);
    static final int MIN_COLUMN = 3;
    static final int MAX_COLUMN = 5;
    static final int DEFAULT_COLUMN = 4;
    static final String[] COLUMNS = new String[] {
        "3","4","5"
    };

    public static String getPref(Context context) {
        String def = context.getResources().getString(R.string.default_sprints);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(SettingsActivity.KEY_SPRINT_NAMES, def);
    }

    public static List<String> getSprintNames(Context context) {
        String pref = getPref(context);
        ArrayList<String> s = new ArrayList<>();
        Iterable<String> results = Splitter.on('#')
            .trimResults()
            .omitEmptyStrings()
            .split(pref);
        Iterator<String> iter = results.iterator(); 
        while(iter.hasNext()) { 
            s.add(iter.next()); 
        }
        return s;
    }
}
