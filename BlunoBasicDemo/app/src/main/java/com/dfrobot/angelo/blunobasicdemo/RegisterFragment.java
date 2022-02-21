package com.dfrobot.angelo.blunobasicdemo;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A fragment representing a list of Items.
 */
public class RegisterFragment extends Fragment {

    // TODO: Customize parameters
    private RegisterAdapter registerAdapter;
    private RecyclerView recyclerView;
    private List<String> allGestures;
    private TextView text;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RegisterFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        text = (TextView) view.findViewById(R.id.text);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new SpacesItemDecoration(30));

        allGestures = new ArrayList<>();

        allGestures.add("柱体变高"); // 轻击0
        allGestures.add("柱体前进"); //
        allGestures.add("柱体后退"); //
        allGestures.add("柱体变矮"); // 重击0
        allGestures.add("柱体左移"); // 长按1
        allGestures.add("音量增高"); // 双击 0
        allGestures.add("音量降低"); // 三击 0
        allGestures.add("结束游戏"); // 0 然后 2
        allGestures.add("进入关卡"); // 四击 0
        allGestures.add("开始游戏"); // 双击 (0 + 2)
        allGestures.add("视角移动"); // 长按0
        allGestures.add("场景缩放"); // 长按0 + 长按2
        allGestures.add("相机切换"); // 1 + 2

//        for (int i = 0; i < 3; i++) {
//            allGestures.add("双击（轻）" + i + "号位");
//            allGestures.add("双击（重）" + i + "号位");
//        }
//        for (int i = 0; i < 3; i++) {
//            allGestures.add("由强到弱三次点击" + i + "号位");
//        }
//        for (int i = 0; i < 4; i++) {
//            allGestures.add("长按" + i + "号位");
//        }
//        allGestures.add("双指同时按0、2号位一次");
//        allGestures.add("双指同时轻按三次、重按三次0、2号位");
//        allGestures.add("双指同时按轻重轻重顺序按四次0、2号位");
//        allGestures.add("连续点击0、1号位");
//        allGestures.add("连续点击0、2号位");
//        allGestures.add("连续点击0、3号位");
//        allGestures.add("连续点击1、3号位");
//        allGestures.add("从左往右普通滑动0、1号位");
//        allGestures.add("从左往右加重滑动0、1号位");
//        allGestures.add("从上往下普通滑动0、3号位");

        registerAdapter = new RegisterAdapter(getContext(), allGestures);
        recyclerView.setAdapter(registerAdapter);

        return view;
    }


    public abstract class TypeAbstractViewHolder extends RecyclerView.ViewHolder{
        public TypeAbstractViewHolder(View itemView) {
            super(itemView);
        }
        public void bindModel(String gesture, Context context){
        }
    }

    public class Holder extends TypeAbstractViewHolder {

        public TextView title;

        public Holder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }

        @Override
        public void bindModel(String gesture, Context context) {
            title.setText(gesture);
            final String tmp = gesture;
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // 开始注册，UI加锁
                    MainActivity.inProgress = true;
                    text.setVisibility(View.VISIBLE);

                    // 每个手势注册三次
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String str = "";
                            for (int i = 1; i <= 3; i++) {
                                str += "正在第" + i + "次录入" + tmp + "， 请捏握\n";
                                final String tmp1 = str;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        text.setText(tmp1);
                                    }
                                });

                                // 等待第i次注册，注册线程开锁
                                MainActivity.RegisterLock = false;
                                while(!MainActivity.RegisterLock);

                                // 移开手指就暴力地停顿两秒（
                                str += "第" + i + "次录入完毕，请移开手指\n";
                                final String tmp2 = str;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        text.setText(tmp2);
                                    }
                                });
                                try {
                                    Thread.sleep(2000);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    text.setText("");
                                    text.setVisibility(View.GONE);
                                    MainActivity.inProgress = false;
                                    MainActivity.RegisterLock = false;
                                }
                            });
                        }
                    }).start();
                }
            });
        }
    }

    public class RegisterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private Context context;

        private List<String> gestures;

        public RegisterAdapter(Context context,List<String> gestures) {
            this.context = context;
            this.gestures = gestures;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(R.layout.item, parent, false);
            Holder holder = new Holder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            ((TypeAbstractViewHolder)holder).bindModel(gestures.get(position),context);

        }

        @Override
        public int getItemCount() {
            return gestures.size();// 这里可能是size+1?
        }

        @Override
        public int getItemViewType(int position) {
            return 1;
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space / 2;
            outRect.right = space / 2;
            outRect.bottom = space / 2;
            outRect.top = space / 2;
        }
    }
}