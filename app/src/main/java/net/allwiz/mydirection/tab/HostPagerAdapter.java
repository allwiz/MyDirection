package net.allwiz.mydirection.tab;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import net.allwiz.mydirection.R;
import net.allwiz.mydirection.define.Category;

import java.util.List;

public class HostPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = FragmentPagerAdapter.class.getSimpleName();

    private Context         mContext;
    private List<Fragment>  mFragments;


    public HostPagerAdapter(Context context, FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mContext = context;
        mFragments = fragments;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case Category.RECENT:
                return mContext.getString(R.string.page_title_recent);

            case Category.FAVORITE:
                return mContext.getString(R.string.page_title_favorite);

            case Category.TRAVEL:
                return mContext.getString(R.string.page_title_travel);
        }
        return null;
    }
}

