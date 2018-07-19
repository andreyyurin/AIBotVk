package ltc.aibotvk.Adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ltc.aibotvk.Fragments.FrGenerated;
import ltc.aibotvk.Fragments.FrPersonal;

/**
 * Created by admin on 28.06.2018.
 */

public class PageAdapter extends FragmentPagerAdapter {

    int numPages;

    public PageAdapter(FragmentManager fm, int numPages) {
        super(fm);
        this.numPages = numPages;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch (position) {
            case 0:
                FrPersonal fr1 = new FrPersonal();
                return fr1;
            case 1:
                FrGenerated fr2 = new FrGenerated();
                return fr2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numPages;
    }
}
