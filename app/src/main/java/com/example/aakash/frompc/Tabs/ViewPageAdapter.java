/*FromPC is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Foobar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FromPC.  If not, see <http://www.gnu.org/licenses/>.*/

package com.example.aakash.frompc.Tabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.aakash.frompc.Tab1;
import com.example.aakash.frompc.Tab2;

public class ViewPageAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[];
    int TabCount;

    public ViewPageAdapter(FragmentManager fm, CharSequence mTitles[], int tabCount) {
        super(fm);

        this.Titles = mTitles;
        this.TabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        if(position == 0){
            Tab1 tab1 = new Tab1();
            return tab1;
        }else {
            Tab2 tab2 = new Tab2();
            return tab2;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }
    @Override
    public int getCount() {
        return TabCount;
    }
}
