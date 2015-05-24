package com.wesllei.ruufmt.navigation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wesllei.ruufmt.R;
import com.wesllei.ruufmt.interfaces.OnNavigationClick;

import java.util.ArrayList;

/**
 * Created by wesllei on 10/05/15.
 */
public class NavigationFragment extends Fragment {

    private FragmentActivity context;
    private ArrayList<NavigationItem> items = new ArrayList<>();
    private View view;
    private OnNavigationClick navigationClikCallBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.fragment_navigation, container, false);

        View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationItem item = items.get(v.getId());
                if (item.getType() == NavigationItem.TYPE_MAIN) {
                    view.findViewById(R.id.navigation_banner).setBackgroundColor(getResources().getColor(item.getColor()));
                }
                navigationClikCallBack.OnNavigationClick(item);
            }
        };

        LinearLayout navigationMainList = (LinearLayout) view.findViewById(R.id.navigation_main_list);
        LinearLayout navigationFooterList = (LinearLayout) view.findViewById(R.id.navigation_footer_list);

        for (int i = 0; i < items.size(); i++) {
            View child = inflater.inflate(R.layout.navigation_list_item, container, false);
            child.setId(i);
            ((TextView) child.findViewById(R.id.item_tile)).setText(items.get(i).geTitle());
            ((ImageView) child.findViewById(R.id.item_icon)).setImageResource(items.get(i).getIcon());
            child.setOnClickListener(onClick);
            if (items.get(i).getType() == NavigationItem.TYPE_MAIN) {
                navigationMainList.addView(child);
            } else {
                if (items.get(i).getType() == NavigationItem.TYPE_FOOTER) {
                    navigationFooterList.addView(child);
                }
            }
        }
        return view;
    }

    public void setItems(ArrayList<NavigationItem> items) {
        this.items = items;
    }

    public void setNavigationClikCallBack(OnNavigationClick navigationClikCallBack) {
        this.navigationClikCallBack = navigationClikCallBack;
    }


}
