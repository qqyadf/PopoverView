package qqyadf.popoverview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PopoverView.PopoverViewDelegate{
    RelativeLayout mMainLayout;
    ListView mListView;
    PopoverView mPopoverView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainLayout = (RelativeLayout) findViewById(R.id.activity_main);

        ArrayList<String> array = new ArrayList<String>();
        array.add("aaaaaaaaaa");
        array.add("bbbbbbbbbb");
        array.add("cccccccccc");
        array.add("dddddddddd");
        array.add("eeeeeeeeee");
        array.add("ffffffffff");
        array.add("gggggggggg");
        array.add("hhhhhhhhhh");
        array.add("iiiiiiiiii");
        mListView = new ListView(this);
        CustomAdapter adapter = new CustomAdapter(this, array);
        mListView.setAdapter(adapter);
        mListView.setHorizontalScrollBarEnabled(false);
        mListView.setVerticalScrollBarEnabled(false);

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopoverView = new PopoverView(MainActivity.this);
                mPopoverView.delegate = MainActivity.this;
                //mPopoverView.showAtView(v, PopoverView.PopoverPositionLeft, new SizeClass(320, 340), mListView, mMainLayout);
                mPopoverView.showAtView(v, new SizeClass(320, 340), mListView, mMainLayout);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopoverView = new PopoverView(MainActivity.this);
                mPopoverView.delegate = MainActivity.this;
                //mPopoverView.showAtView(v, PopoverView.PopoverPositionRight, new SizeClass(320, 340), mListView, mMainLayout);
                mPopoverView.showAtView(v, new SizeClass(320, 340), mListView, mMainLayout);
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopoverView = new PopoverView(MainActivity.this);
                mPopoverView.delegate = MainActivity.this;
                //mPopoverView.showAtView(v, PopoverView.PopoverPositionUp, new SizeClass(320, 340), mListView, mMainLayout);
                mPopoverView.showAtView(v, new SizeClass(320, 340), mListView, mMainLayout);
            }
        });

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopoverView = new PopoverView(MainActivity.this);
                mPopoverView.delegate = MainActivity.this;
                //mPopoverView.showAtView(v, PopoverView.PopoverPositionDown, new SizeClass(320, 340), mListView, mMainLayout);
                mPopoverView.showAtView(v, new SizeClass(320, 340), mListView, mMainLayout);
            }
        });

        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopoverView = new PopoverView(MainActivity.this);
                mPopoverView.delegate = MainActivity.this;
                //mPopoverView.showAtView(v, new SizeClass(320, 340), mListView, mMainLayout);
                mPopoverView.showAtView(v, new SizeClass(320, 340), mListView, mMainLayout);
            }
        });
    }

    @Override
    public void popoverViewWillShow(PopoverView view) {

    }

    @Override
    public void popoverViewDidShow(PopoverView view) {

    }

    @Override
    public void popoverViewWillDismiss(PopoverView view) {

    }

    @Override
    public void popoverViewDidDismiss(PopoverView view) {

    }
}
