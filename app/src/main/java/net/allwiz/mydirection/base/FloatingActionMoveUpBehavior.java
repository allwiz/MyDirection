package net.allwiz.mydirection.base;

// https://github.com/ajaydewari/FloatingActionButtonMenu/blob/master/app/src/main/java/com/ajaysinghdewari/floatingactionbuttonmenu/activities/utils/FloatingActionMoveUpBehavior.java
// https://www.bignerdranch.com/blog/customizing-coordinatorlayouts-behavior/

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.snackbar.Snackbar;

public class FloatingActionMoveUpBehavior extends CoordinatorLayout.Behavior<View> {

    public FloatingActionMoveUpBehavior() {
        super();
    }

    public FloatingActionMoveUpBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }



    @SuppressWarnings("deprecation")
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        float translationY = Math.min(0, ViewCompat.getTranslationY(dependency) - dependency.getHeight());
        ViewCompat.setTranslationY(child, translationY);
        return true;
    }

    //you need this when you swipe the snackbar(thanx to ubuntudroid's comment)
    public void onDependentViewRemoved(CoordinatorLayout parent, View child, View dependency) {
        ViewCompat.animate(child).translationY(0).start();
    }
}
