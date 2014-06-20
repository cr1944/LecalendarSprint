
package ryan.lenovo.lecalendarsprint;

import org.joda.time.DateTime;
import org.joda.time.Days;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class MainView extends View {
    private float mHeaderHeight;
    private float mFooterHeight;
    private float mRowMargin;
    private float mColumWidth;
    private float mRowHeight;
    private float mTextSize;
    private float mLineWidth;
    private float mPadding;
    private int mColumnNumber = Utils.DEFAULT_COLUMN;
    private int mSprintNumber;
    private float mIndent;
    private float[] mLines;
    private String[] mDateStrings;
    private String[] mSprintStrings;
    private float[] mDatesX;
    private float mThisWeekX;
    private DateTime mTempDate;
    private Paint mP = new Paint();
    private String mText1, mText2, mText3;
    private String mThisWeek;
    private GestureDetector mGestureDetector;
    private static final int DEFAULT_HEADER_HEIGHT = 50;
    private static final int DEFAULT_ROW_MARGIN = 10;
    private static final int DEFAULT_TEXT_SIZE = 20;
    private static final int DEFAULT_LINE_WIDTH = 2;
    private static final int DEFAULT_INDENT = 20;
    private static final int DEFAULT_FOOTER_HEIGHT = 40;

    private static final int LINE_COLOR = Color.BLACK;
    private static final int TEXT_COLOR = Color.DKGRAY;
    private static final int ITEM_COLOR_1 = Color.RED;
    private static final int ITEM_COLOR_2 = Color.GREEN;
    private static final int ITEM_COLOR_3 = Color.BLUE;

    public MainView(Context context) {
        this(context, null);
    }

    public MainView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Resources res = context.getResources();
        float density = res.getDisplayMetrics().density;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MainView, defStyleAttr, 0);
        mHeaderHeight = a.getDimension(R.styleable.MainView_headerHeight, DEFAULT_HEADER_HEIGHT
                * density);
        mFooterHeight = DEFAULT_FOOTER_HEIGHT * density;
        mRowMargin = a.getDimension(R.styleable.MainView_rowMargin, DEFAULT_ROW_MARGIN
                * density);
        mTextSize = a.getDimension(R.styleable.MainView_textSize, DEFAULT_TEXT_SIZE * density);
        mLineWidth = a.getDimension(R.styleable.MainView_lineWidth, DEFAULT_LINE_WIDTH * density);
        mPadding = a.getDimension(R.styleable.MainView_padding, 0);
        mIndent = a.getDimension(R.styleable.MainView_indent, DEFAULT_INDENT * density);
        a.recycle();
        mText1 = res.getString(R.string.text1);
        mText2 = res.getString(R.string.text2);
        mText3 = res.getString(R.string.text3);
        mThisWeek = res.getString(R.string.current);
        mGestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void setColumnNumber(int number) {
        if (number < Utils.MIN_COLUMN || number > Utils.MAX_COLUMN) {
            throw new IllegalArgumentException("setColumnNumber " + number + "out of bounds!");
        }
        mColumnNumber = number;
        mSprintNumber = mColumnNumber + 2;
        resetSize(getWidth(), getHeight());
        invalidate();
    }

    /**
     * 
     * @param date the date first displayed
     */
    public void setDates(DateTime date) {
        mTempDate = date;
        resetStrings();
        invalidate();
    }

    public void refresh() {
        resetStrings();
        invalidate();
    }

    private void resetStrings() {
        if (mTempDate == null) {
            mTempDate = Utils.FIRSTDAY;
        }

        mDateStrings = new String[mColumnNumber + 1];
        mSprintStrings = new String[mSprintNumber];
        mDateStrings[0] = mTempDate.toString(Utils.DATE_FORMAT);
        for (int i = 1; i < mColumnNumber + 1; i++) {
            DateTime date = mTempDate.plusDays(i * Utils.PERIOD);
            mDateStrings[i] = date.toString(Utils.DATE_FORMAT);
        }
        int days = Days.daysBetween(Utils.FIRSTDAY, mTempDate).getDays();
        int index = days / Utils.PERIOD;
        int validNamesSize = Utils.getSprintNames(getContext()).size();
        for (int i = 0; i < mSprintNumber; i++) {
            int realIndex = i + index;
            if (realIndex >= 0 && realIndex < validNamesSize) {
                mSprintStrings[i] = Utils.getSprintNames(getContext()).get(realIndex);
            } else {
                mSprintStrings[i] = Utils.INVALID_SPRINT_NAME;
            }
        }
        resetToday();
    }

    private void resetToday() {
        int days = Days.daysBetween(mTempDate, DateTime.now()).getDays();
        int index = days / Utils.PERIOD;
        if (days < 0 || index < 0 || index >= mColumnNumber) {
            mThisWeekX = -1;
            return;
        }
        mThisWeekX = mPadding + mIndent + mColumWidth * index + mColumWidth / 2;
    }

    private void resetSize(int w, int h) {
        //lines number is mColumnNumber + 2
        mLines = new float[mColumnNumber * 4 + 8];
        mDatesX = new float[mColumnNumber + 1];
        int lineCount = 0;
        mLines[lineCount++] = mPadding;
        mLines[lineCount++] = mPadding + mHeaderHeight;
        mLines[lineCount++] = w - mPadding;
        mLines[lineCount++] = mPadding + mHeaderHeight;

        mColumWidth = (w - mPadding * 2 - mIndent * 2) / mColumnNumber;
        mRowHeight = (h - mHeaderHeight - mPadding - mFooterHeight - mPadding) / mSprintNumber - mRowMargin;
        for (int i = 0; i < mColumnNumber + 1; i++) {
            mLines[lineCount++] = mPadding + mIndent + mColumWidth * i;
            mLines[lineCount++] = mPadding + mHeaderHeight;
            mLines[lineCount++] = mPadding + mIndent + mColumWidth * i;
            mLines[lineCount++] = h - mPadding - mFooterHeight;
            mDatesX[i] = mPadding + mIndent + mColumWidth * i;
        }
        resetStrings();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        resetSize(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int w = getWidth();
        final int h = getHeight();
        canvas.drawColor(0xffdddddd);

        //draw lines
        mP.setAntiAlias(false);
        mP.setStyle(Style.FILL_AND_STROKE);
        mP.setStrokeWidth(mLineWidth);
        mP.setColor(LINE_COLOR);
        canvas.drawLines(mLines, mP);

        //draw dates
        mP.setFakeBoldText(false);
        mP.setAntiAlias(true);
        mP.setTextSize(mTextSize);
        mP.setColor(TEXT_COLOR);
        mP.setStyle(Style.FILL);
        mP.setTextAlign(Align.CENTER);
        for (int i = 0; i < mDateStrings.length; i++) {
            canvas.drawText(mDateStrings[i], mDatesX[i], mHeaderHeight - 1, mP);
        }
        if (mThisWeekX > 0) {
            mP.setColor(Color.RED);
            canvas.drawText(mThisWeek, mThisWeekX, mHeaderHeight / 2, mP);
            mP.setColor(TEXT_COLOR);
        }

        //draw rects
        for (int j = 0; j < mColumnNumber; j++) {
            float left = mPadding + mIndent + mColumWidth * j;
            float top = (mRowHeight + mRowMargin) * j + mRowMargin + mHeaderHeight + mPadding;
            float right = mPadding + mIndent + mColumWidth * j + mColumWidth;
            float bottom = (mRowHeight + mRowMargin) * (j + 1) + mHeaderHeight + mPadding;
            canvas.save(Canvas.CLIP_SAVE_FLAG);
            canvas.clipRect(left, top, right, bottom);
            canvas.drawColor(ITEM_COLOR_3);
            canvas.restore();

            top += (mRowHeight + mRowMargin);
            bottom += (mRowHeight + mRowMargin);
            canvas.save(Canvas.CLIP_SAVE_FLAG);
            canvas.clipRect(left, top, right, bottom);
            canvas.drawColor(ITEM_COLOR_2);
            canvas.restore();

            top += (mRowHeight + mRowMargin);
            bottom += (mRowHeight + mRowMargin);
            canvas.save(Canvas.CLIP_SAVE_FLAG);
            canvas.clipRect(left, top, right, bottom);
            canvas.drawColor(ITEM_COLOR_1);
            canvas.restore();
        }

        //draw sprint text
        mP.setAntiAlias(true);
        mP.setColor(Color.WHITE);
        mP.setFakeBoldText(true);
        for (int j = 0; j < mColumnNumber; j++) {
            float x = mPadding + mIndent + mColumWidth * j + mColumWidth / 2;
            float y = (mRowHeight + mRowMargin) * j + mRowMargin + mHeaderHeight + mPadding + mRowHeight / 2;
            canvas.drawText(mSprintStrings[j], x, y, mP);
            y += (mRowHeight + mRowMargin);
            canvas.drawText(mSprintStrings[j + 1], x, y, mP);
            y += (mRowHeight + mRowMargin);
            canvas.drawText(mSprintStrings[j + 2], x, y, mP);
        }

        //draw samples
        mP.setColor(TEXT_COLOR);
        mP.setTextAlign(Align.LEFT);
        canvas.drawText(mText1, mPadding * 2 + mTextSize, h - mPadding, mP);
        canvas.drawText(mText2, mPadding * 2 + mTextSize + w / 3, h - mPadding, mP);
        canvas.drawText(mText3, mPadding * 2 + mTextSize + w * 2 / 3, h - mPadding, mP);

        //draw sample rects
        mP.setAntiAlias(false);
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(mPadding, h - mPadding - mTextSize, mPadding + mTextSize, h - mPadding);
        canvas.drawColor(ITEM_COLOR_1);
        canvas.restore();

        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(mPadding + w / 3, h - mPadding - mTextSize, mPadding + w / 3 + mTextSize, h - mPadding);
        canvas.drawColor(ITEM_COLOR_2);
        canvas.restore();

        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(mPadding + w * 2 / 3, h - mPadding - mTextSize, mPadding + w * 2 / 3 + mTextSize, h - mPadding);
        canvas.drawColor(ITEM_COLOR_3);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    private class GestureListener extends SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://work.lenovo.com/issue/secure/RapidBoard.jspa?rapidView=80&view=planning"));
            getContext().startActivity(intent);
            return true;
        }
    }
}
