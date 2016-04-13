package jifa.racecarsandstuff;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewFont extends TextView {

    public TextViewFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewFont(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font.ttf");
            setTypeface(tf);
        }
    }

}
