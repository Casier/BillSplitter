package casier.billsplitter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class InterceptClickRelativeLayout extends RelativeLayout {
    public InterceptClickRelativeLayout(Context context) {
        super(context);
    }

    public InterceptClickRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptClickRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
