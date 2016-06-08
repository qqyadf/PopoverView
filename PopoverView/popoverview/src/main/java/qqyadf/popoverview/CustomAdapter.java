package qqyadf.popoverview;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Yeah on 2016-06-08.
 */

public class CustomAdapter extends BaseAdapter {

    private String TAG = "CustomAdapter";
    private Context mContext;
    private ArrayList<String> list = new ArrayList<String>();

    public CustomAdapter(Context context, ArrayList<String> list) {
        mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int index) {
        // TODO Auto-generated method stub
        return list.get(index);
    }

    @Override
    public long getItemId(int index) {
        // TODO Auto-generated method stub
        return index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView mTextView = null;
        if (convertView == null) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(dpToPix(40, mContext.getResources()),
                    dpToPix(40, mContext.getResources()));
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            RelativeLayout Layout = new RelativeLayout(mContext);
            mTextView = new TextView(mContext);
            Layout.addView(mTextView, lp);
            convertView = Layout;
            convertView.setTag(mTextView);
        } else {
            mTextView = (TextView)convertView.getTag();
        }

        mTextView.setText(list.get(position));

        return convertView;
    }

    private int dpToPix(int dp, Resources res) {
        return (int)(dp*res.getDisplayMetrics().density+0.5f);
    }
}
