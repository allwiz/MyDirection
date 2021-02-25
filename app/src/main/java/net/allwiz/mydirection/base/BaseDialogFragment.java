package net.allwiz.mydirection.base;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import net.allwiz.mydirection.database.DirectionDb;
import net.allwiz.mydirection.shared.SharedApplication;

public class BaseDialogFragment extends DialogFragment {
    private static final String TAG = BaseDialogFragment.class.getSimpleName();
    protected SharedApplication mSharedApplication;

    protected Activity mActivity;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = getActivity();
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mActivity = activity;
    }


    @Override
    public void onResume() {
        super.onResume();

        //removeFrameBorder();
        adjustWindowWidth();
    }

    public Context getContext() {
        return mActivity.getApplicationContext();
    }


    protected void setSharedApplication() {
        mSharedApplication = (SharedApplication)getActivity().getApplicationContext();
    }


    protected DirectionDb getDirectionDb() {
        return mSharedApplication.getDirectionDb();
    }

    protected void hideTitle() {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }

    private void removeFrameBorder() {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme);
    }

    protected void adjustWindowWidth() {
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    protected void OnBackPressed() {
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if ((keyCode ==  android.view.KeyEvent.KEYCODE_BACK))
                {
                    return true;
                }

                return false;
            }
        });
    }


    protected void showKeyboard(boolean show) {
        View view = mActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

            if (show) {
                imm.showSoftInput(view, 0);
            } else {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
