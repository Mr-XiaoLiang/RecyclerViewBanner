package liang.lollipop.recyclerviewbannerdeme;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

import liang.lollipop.rvbannerlib.BannerUtil4J;
import liang.lollipop.rvbannerlib.banner.OnSelectedListener;
import liang.lollipop.rvbannerlib.banner.Orientation;
import liang.lollipop.rvbannerlib.banner.ScrollState;

public class Main4JActivity extends AppCompatActivity {

    private BannerUtil4J bannerUtil4J;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4_j);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();
    }

    private void initView(){

        ArrayList<Bean> dataList = new ArrayList<>();

        Random random = new Random();

        for(int i = 0;i<=35;i++){
            dataList.add(new Bean("Test"+i,nextColor(random)));
        }

        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        final RecyclerView recyclerView2 = findViewById(R.id.recyclerView2);
        final RecyclerView recyclerView3 = findViewById(R.id.recyclerView3);

        final TextView positionValue = findViewById(R.id.positionValue);

        final TextView positionValue2 = findViewById(R.id.positionValue2);

        bannerUtil4J = BannerUtil4J.with(recyclerView)
                .attachAdapter(new TestAdapter(dataList,getLayoutInflater()))
                .init()
                .addOnSelectedListener(new OnSelectedListener() {
                    @Override
                    public void onSelected(int position, @NotNull ScrollState state) {
                        positionValue.setText(position+"--"+state.toString());
                    }
                })
                .isAutoNext(true);

        BannerUtil4J.with(recyclerView2)
                .attachAdapter(new TestAdapter(dataList,getLayoutInflater()))
                .setOrientation(Orientation.VERTICAL)
                .setSecondaryExposedWeight(0.15F)
                .addOnSelectedListener(new OnSelectedListener() {
                    @Override
                    public void onSelected(int position, @NotNull ScrollState state) {
                        positionValue2.setText(position+"--"+state.toString());
                    }
                })
                .init();

        ArrayList<Object> bannerList = new ArrayList<>();
        bannerList.add(new BannerBean(dataList));
        bannerList.addAll(dataList);
        recyclerView3.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView3.setAdapter(new BannerAdapter(bannerList,getLayoutInflater()));
        recyclerView3.getAdapter().notifyDataSetChanged();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(30);
                recyclerView2.smoothScrollToPosition(30);
                recyclerView3.smoothScrollToPosition(31);
            }
        });
    }

    private int nextColor(Random random){
        return Color.rgb(random.nextInt(255),random.nextInt(255),random.nextInt(255));
    }


    @Override
    protected void onResume() {
        super.onResume();
        bannerUtil4J.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bannerUtil4J.onPause();
    }

    class TestAdapter extends RecyclerView.Adapter<TestHolder>{

        private ArrayList<Bean> data;
        private LayoutInflater layoutInflater;

        public TestAdapter(ArrayList<Bean> data, LayoutInflater layoutInflater) {
            this.data = data;
            this.layoutInflater = layoutInflater;
        }

        @Override
        public TestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TestHolder(layoutInflater.inflate(R.layout.item_test,parent,false));
        }

        @Override
         public int getItemCount(){
            return data.size();
         }

         @Override
         public void onBindViewHolder(TestHolder holder,int position) {
            holder.onBind(data.get(position));
         }


    }

    class TestHolder extends RecyclerView.ViewHolder{

        private TextView textView;

        public TestHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.itemText);
        }

        public void onBind(Bean bean){
            onBind(bean.text,bean.color);
        }

        public void onBind(String text,int color){
            textView.setText(text);
            textView.setBackgroundColor(color);
        }

    }

    class BannerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private ArrayList<Object> data;
        private LayoutInflater layoutInflater;

        public BannerAdapter(ArrayList<Object> data, LayoutInflater layoutInflater) {
            this.data = data;
            this.layoutInflater = layoutInflater;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType == 0){
                return new BannerHolder(layoutInflater.inflate(R.layout.item_banner,parent,false));
            }else{
                return new TestHolder(layoutInflater.inflate(R.layout.item_test,parent,false));
            }
        }

        @Override
        public int getItemCount(){
            return data.size();
        }

        @Override
        public int getItemViewType(int position) {
            return data.get(position) instanceof BannerBean ? 0 : 1;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder,int position) {
            Object bean = data.get(position);
            if(holder instanceof BannerHolder && bean instanceof BannerBean){
                ((BannerHolder)holder).onBind((BannerBean)bean);
            }else if(holder instanceof TestHolder && bean instanceof Bean){
                ((TestHolder)holder).onBind((Bean)bean);
            }

        }


    }

    class BannerHolder extends RecyclerView.ViewHolder{

        private RecyclerView recyclerView;
        private BannerUtil4J bannerUtil4J;
        private ArrayList<Bean> data = new ArrayList<>();

        public BannerHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            bannerUtil4J = BannerUtil4J.with(recyclerView)
                    .attachAdapter(new TestAdapter(data,LayoutInflater.from(itemView.getContext())))
                    .init();
        }

        public void onBind(BannerBean bean){

            data.clear();
            data.addAll(bean.data);
            bannerUtil4J.notifyDataSetChanged();

        }

    }

    class Bean{

        String text;
        int color;

        public Bean(String text, int color) {
            this.text = text;
            this.color = color;
        }
    }

    class BannerBean{

        ArrayList<Bean> data = new ArrayList<>();

        public BannerBean(ArrayList<Bean> data) {
            this.data.addAll(data);
        }
    }

}
