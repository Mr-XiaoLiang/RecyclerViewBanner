package liang.lollipop.rvbannerlib;

import android.support.v7.widget.RecyclerView;

import liang.lollipop.rvbannerlib.banner.OnSelectedListener;
import liang.lollipop.rvbannerlib.banner.Orientation;

/**
 * Banner初始化的工具类的Java版
 * @author  Lollipop
 */
public class BannerUtil4J {

    private BannerUtil bannerUtil;

    private BannerUtil4J(RecyclerView recyclerView){
        bannerUtil = BannerUtil.Companion.with(recyclerView);
    }

    public static BannerUtil4J with(RecyclerView recyclerView){
        return new BannerUtil4J(recyclerView);
    }


    public BannerUtil4J setOrientation(Orientation orientation) {
        bannerUtil.setOrientation(orientation);
        return this;
    }

    public BannerUtil4J setSecondaryExposed(int secondaryExposed) {
        bannerUtil.setSecondaryExposed(secondaryExposed);
        return this;
    }

    public BannerUtil4J setSecondaryExposedWeight(float secondaryExposedWeight) {
        bannerUtil.setSecondaryExposedWeight(secondaryExposedWeight);
        return this;
    }

    public BannerUtil4J setScaleGap(float scaleGap) {
        bannerUtil.setScaleGap(scaleGap);
        return this;
    }

    public BannerUtil4J setPagerMode(boolean isPager) {
        bannerUtil.isPagerMode(isPager);
        return this;
    }

    public BannerUtil4J attachAdapter(RecyclerView.Adapter adapter){
        bannerUtil.attachAdapter(adapter);
        return this;
    }

    public BannerUtil4J setAutoNextDelayed(long delayed){
        bannerUtil.setAutoNextDelayed(delayed);
        return this;
    }

    public BannerUtil4J isAutoNext(boolean auto){
        bannerUtil.isAutoNext(auto);
        return this;
    }

    public BannerUtil4J init(){

        bannerUtil.init();

        return this;
    }

    public BannerUtil4J notifyDataSetChanged(){
        bannerUtil.notifyDataSetChanged();
        return this;
    }

    public BannerUtil4J  scrollToPosition(int position){
        bannerUtil.scrollToPosition(position);
        return this;
    }

    public BannerUtil4J smoothScrollToPosition(int position){
        bannerUtil.smoothScrollToPosition(position);
        return this;
    }

    /**
     * 开启自动轮播后，需要主动调用生命周期方法，才会触发自动轮播
     * 以此来防止生命周期之外的异常
     */
    public BannerUtil4J onResume(){
        bannerUtil.onResume();
        return this;
    }

    /**
     * 开启自动轮播后，需要主动调用生命周期方法，才会自动停止轮播
     * 以此来防止生命周期之外的异常
     */
    public BannerUtil4J onPause(){
        bannerUtil.onPause();
        return this;
    }

    /**
     * 下一个页面
     */
    public BannerUtil4J nextPosition(){
        bannerUtil.nextPosition();
        return this;
    }

    /**
     * 添加一个监听滚动的监听器
     */
    public BannerUtil4J addOnSelectedListener(OnSelectedListener listener){
        bannerUtil.addOnSelectedListener(listener);
        return this;
    }

    /**
     * 移出一个监听滚动的监听器
     */
    public BannerUtil4J removeOnSelectedListener(OnSelectedListener listener){
        bannerUtil.removeOnSelectedListener(listener);
        return this;
    }

}
