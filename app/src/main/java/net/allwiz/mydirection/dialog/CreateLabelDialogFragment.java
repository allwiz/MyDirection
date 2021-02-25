package net.allwiz.mydirection.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.Nullable;

import net.allwiz.mydirection.BuildConfig;
import net.allwiz.mydirection.R;
import net.allwiz.mydirection.base.BaseDialogFragment;
import net.allwiz.mydirection.define.Category;
import net.allwiz.mydirection.define.Action;
import net.allwiz.mydirection.define.Command;
import net.allwiz.mydirection.define.Value;
import net.allwiz.mydirection.util.LogEx;

import java.util.ArrayList;

public class CreateLabelDialogFragment extends BaseDialogFragment {
    private static final String TAG = CreateLabelDialogFragment.class.getSimpleName();

    private CreateLabelDialogFragmentControlListener    mListener;

    private AutoCompleteTextView mLabelText;

    private Button              mOKButton;
    private Button              mCancelButton;

    private int                 mCategory = Category.FAVORITE;


    public static CreateLabelDialogFragment newInstance(int category) {
        CreateLabelDialogFragment c = new CreateLabelDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Command.Name.CATEGORY, category);
        c.setArguments(bundle);
        return c;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_create_label, container, false);

        setCallbackControlListener();
        setArguments();

        setSharedApplication();
        getDialog().setCanceledOnTouchOutside(false);
        OnBackPressed();

        createAutoCompleteLabelText(view);
        createOKButton(view);
        createCancelButton(view);


        showKeyboard(true);

        return view;
    }



    public interface CreateLabelDialogFragmentControlListener {
        public void onCreateLabelDialogFragmentControlListener(int action);
        public void onCreateLabelDialogFragmentControlListener(int action, String labelName, long labelIndex);
    }


    private void setCallbackControlListener() {
        try {
            mListener = (CreateLabelDialogFragmentControlListener)mActivity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }


    private void setArguments() {
        if (getArguments() != null) {
            mCategory = getArguments().getInt(Command.Name.CATEGORY);
        }
    }


    // https://developer.android.com/reference/android/widget/AutoCompleteTextView
    private void createAutoCompleteLabelText(View view) {
        mLabelText = (AutoCompleteTextView) view.findViewById(R.id.dialog_fragment_create_label_auto_complete_text);

        //ArrayList<String> items = new ArrayList<String>();

        ArrayList<String> items = getDirectionDb().fetchLabelNameItems(mCategory);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, items);
        mLabelText.setAdapter(adapter);

        mLabelText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mLabelText.showDropDown();
                return false;
            }
        });

    }

    private void createOKButton(View view) {
        mOKButton = (Button)view.findViewById(R.id.dialog_fragment_create_label_ok_button);
        mOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String labelName = mLabelText.getText().toString();
                LogEx.d(TAG, "LABEL NAME: " + labelName);
                if (labelName.isEmpty()) {
                    mListener.onCreateLabelDialogFragmentControlListener(Action.Check.Label.EMPTY_LABEL_NAME);
                    return;
                }

                if (getDirectionDb().existLabelNameItem(labelName)) {
                    mListener.onCreateLabelDialogFragmentControlListener(Action.Check.Label.EXIST_LABEL_NAME);
                    return;
                }

                long labelIndex = getDirectionDb().insertLabelItem(mCategory, labelName);
                if (labelIndex == Value.Database.INVALID_ROW_ID) {
                    mListener.onCreateLabelDialogFragmentControlListener(Action.Check.Label.FAILED_INSERT_LABEL_NAME);
                } else {

                    if (BuildConfig.DEBUG) {
                        long idx = getDirectionDb().getLabelItemIndex(labelName);
                        LogEx.d(TAG, String.format("LABEL INDEX: %d", idx));
                    }

                    //mListener.onCreateLabelDialogFragmentControlListener(Action.Check.Label.OK);
                    mListener.onCreateLabelDialogFragmentControlListener(Action.Check.Label.OK, labelName, labelIndex);
                    dismiss();
                }
            }
        });
    }


    private void createCancelButton(View view) {
        mCancelButton = (Button)view.findViewById(R.id.dialog_fragment_create_label_cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mListener.onCreateLabelDialogFragmentControlListener(Action.Button.CANCEL);
                dismiss();
            }
        });
    }
}
