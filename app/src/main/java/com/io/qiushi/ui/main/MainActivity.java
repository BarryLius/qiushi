package com.io.qiushi.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.io.qiushi.R;
import com.io.qiushi.ui.commom.adapter.OnItemClicklistener;
import com.io.qiushi.ui.image.ImageFragment;
import com.io.qiushi.ui.text.TextFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnItemClicklistener {
    private Context mContext;
    private int selected_tag = 0;
    @BindView(R.id.dl_right)
    DrawerLayout dlRight;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_change)
    RecyclerView rvChange;

    MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        //default fragment
        changeFragment(ImageFragment.newInstance("", ""), 0);

        rvChange.setLayoutManager(new LinearLayoutManager(mContext));

        List<String> list = new ArrayList<>();
        list.add(getString(R.string.right_1));
        list.add(getString(R.string.right_2));
        mainAdapter = new MainAdapter(mContext, list);
        rvChange.setAdapter(mainAdapter);

        mainAdapter.setOnItemClicklistener(this);
    }

    @Override
    public void OnItemClick(int position, View view) {
        switch (position) {
            case 0:
                if (selected_tag != 0) {
                    changeFragment(ImageFragment.newInstance("", ""), 0);
                    selected_tag = 0;
                }
                break;
            case 1:
                if (selected_tag != 1) {
                    changeFragment(TextFragment.newInstance("", ""), 1);
                    selected_tag = 1;
                }
                break;
            default:
                changeFragment(ImageFragment.newInstance("", ""), 0);
                break;
        }
        dlRight.closeDrawer(GravityCompat.END);
    }

    @Override
    public void onBackPressed() {
        if (dlRight.isDrawerOpen(GravityCompat.END)) {
            dlRight.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    private void changeFragment(Fragment fragment, int tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_root, fragment);
        ft.commit();
        selected_tag = tag;
    }
}
