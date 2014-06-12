
package ryan.lenovo.lecalendarsprint;

import org.joda.time.DateTime;
import org.joda.time.Days;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;

public class MainActivity extends RoboActivity implements OnItemSelectedListener,
    OnClickListener, OnValueChangeListener {

    @InjectView(R.id.spinner1) Spinner mSpinner1;
    @InjectView(R.id.mainView) MainView mMainView;
    @InjectView(R.id.button1) Button mButton1;

    private DateTime mDateTime;
    private AlertDialog mAlertDialog;
    private int mCurrentIndex;

    public static final NumberPicker.Formatter FORMATTER = new NumberPicker.Formatter() {
        final StringBuilder mBuilder = new StringBuilder();

        @Override
        public String format(int value) {
            mBuilder.delete(0, mBuilder.length());
            DateTime start = Utils.FIRSTDAY.plusDays(value * Utils.PERIOD);
            mBuilder.append(start.toString(Utils.DATE_FORMAT));
            mBuilder.append(" - ");
            mBuilder.append(start.plusDays(Utils.PERIOD - 3).toString(Utils.DATE_FORMAT));
            return mBuilder.toString();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSpinner1.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Utils.COLUMNS));
        mSpinner1.setSelection(Utils.DEFAULT_COLUMN - Utils.MIN_COLUMN);
        mSpinner1.setOnItemSelectedListener(this);
        mButton1.setOnClickListener(this);
        mCurrentIndex = Days.daysBetween(Utils.FIRSTDAY, DateTime.now()).getDays() / Utils.PERIOD;
        mDateTime = Utils.FIRSTDAY.plusDays(mCurrentIndex * Utils.PERIOD);
        mMainView.setDates(mDateTime);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mMainView.setColumnNumber(position + Utils.MIN_COLUMN);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onClick(View v) {
        if (mAlertDialog == null) {
            initGotoDialog();
        }
        mAlertDialog.show();

    }

    private void initGotoDialog() {
        NumberPicker mNumberPicker = new NumberPicker(this);
        mNumberPicker.setMinValue(0);
        mNumberPicker.setMaxValue(Utils.MAX_PERIOD_NUMBER);
        mNumberPicker.setFormatter(FORMATTER);
        mNumberPicker.setValue(mCurrentIndex);
        mNumberPicker.setWrapSelectorWheel(false);
        mNumberPicker.setOnValueChangedListener(this);
        mAlertDialog = new AlertDialog.Builder(this)
        .setView(mNumberPicker)
        .setPositiveButton(android.R.string.ok, new android.content.DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mMainView.setDates(mDateTime);
            }
        }).create();
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        mDateTime = Utils.FIRSTDAY.plusDays(newVal * Utils.PERIOD);
    }
}
