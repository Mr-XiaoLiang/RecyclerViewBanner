package liang.lollipop.rvbannerlib

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import liang.lollipop.rvbannerlib.banner.LBannerLayoutManager
import liang.lollipop.rvbannerlib.banner.OnSelectedListener
import liang.lollipop.rvbannerlib.banner.Orientation
import liang.lollipop.rvbannerlib.banner.ScrollState
import java.util.concurrent.Delayed

/**
 * Banner初始化的工具类的Kotlin版
 * @author  Lollipop
 */
class BannerUtil private constructor(private val recyclerView: RecyclerView):
        Handler.Callback,
        OnSelectedListener {

    companion object {
        fun with(recyclerView: RecyclerView): BannerUtil{
            return BannerUtil(recyclerView)
        }

        private const val WHAT_NEXT_PAGE = 12

    }

    private val builder = Builder()

    private class Builder{
        /**
         * 滚动方向
         */
        var orientation = Orientation.HORIZONTAL

        /**
         * 次要方块的露出距离
         * 此处单位为px
         * 当{@link #orientation}为{@link #Orientation.HORIZONTAL}时，表示左右侧Item的露出距离
         * 当{@link #orientation}为{@link #Orientation.VERTICAL}时，表示上下方Item的露出距离
         */
        var secondaryExposed = 0

        /**
         * 次要方块的露出距离的权重
         * 注意，只有当{@link #secondaryExposed}为0时，才会使用此属性
         * 此权重为相对RecyclerView而言，并且分别针对左右两侧。
         * 即：当secondaryExposedWeight = 0.1F,那么主Item的宽度为RecyclerView.width * 0.8F
         */
        var secondaryExposedWeight = 0.14F

        /**
         * 次item缩放量
         * 表示当Item位于次Item位置时，显示尺寸相对于完整尺寸的量
         */
        var scaleGap = 0.8F

        /**
         * 是否是ViewPager模式，如果是，那么将一次只能翻动1页
         */
        var isPagerMode = false

        /**
         * 自动轮播的间隔
         */
        var autoNextDelayed = 3000L

        /**
         * 是否自动轮播
         */
        var isAutoNext = false
    }

    private var position = 0

    private val onSelectedListenerList = ArrayList<OnSelectedListener>()

    /**
     * 自动轮播的方向，用于控制轮播页面的增减
     */
    private var positionDirection = true

    private val autoHandler = Handler(Looper.getMainLooper(),this)

    fun attachAdapter(adapter: RecyclerView.Adapter<*>): BannerUtil{
        recyclerView.adapter = adapter
        return this
    }

    fun setOrientation(o: Orientation): BannerUtil{
        builder.orientation = o
        return this
    }

    fun setSecondaryExposed(secondaryExposed: Int): BannerUtil{
        builder.secondaryExposed = secondaryExposed
        return this
    }

    fun setSecondaryExposedWeight(weight: Float): BannerUtil{
        builder.secondaryExposedWeight = weight
        builder.secondaryExposed = 0
        return this
    }

    fun setScaleGap(scale: Float): BannerUtil{
        builder.scaleGap = scale
        return this
    }

    fun isPagerMode(isPager: Boolean): BannerUtil{
        builder.isPagerMode = isPager
        return this
    }

    fun setAutoNextDelayed(delayed: Long): BannerUtil{
        builder.autoNextDelayed = delayed
        return this
    }

    fun isAutoNext(auto: Boolean): BannerUtil{
        builder.isAutoNext = auto
        return this
    }

    fun init(): BannerUtil{

        recyclerView.layoutManager = LBannerLayoutManager().apply {
            orientation = builder.orientation
            secondaryExposed = builder.secondaryExposed
            secondaryExposedWeight = builder.secondaryExposedWeight
            scaleGap = builder.scaleGap
            onSelectedListener = this@BannerUtil
        }

        val snapHelper = if(builder.isPagerMode){
            PagerSnapHelper()
        }else{
            LinearSnapHelper()
        }
        snapHelper.attachToRecyclerView(recyclerView)

        notifyDataSetChanged()

        return this
    }

    fun notifyDataSetChanged(): BannerUtil{
        recyclerView.adapter.notifyDataSetChanged()
        return this
    }

    fun scrollToPosition(position: Int): BannerUtil{
        recyclerView.scrollToPosition(position)
        this.position = position
        return this
    }

    fun smoothScrollToPosition(position: Int): BannerUtil{
        recyclerView.smoothScrollToPosition(position)
        this.position = position
        return this
    }

    override fun handleMessage(msg: Message?): Boolean {
        if(msg?.what == WHAT_NEXT_PAGE){
            nextPosition()
            autoHandler.sendEmptyMessageDelayed(WHAT_NEXT_PAGE,builder.autoNextDelayed)
            return true
        }
        return false
    }

    /**
     * 开启自动轮播后，需要主动调用生命周期方法，才会触发自动轮播
     * 以此来防止生命周期之外的异常
     */
    fun onResume(): BannerUtil{
        if(builder.isAutoNext){
            autoHandler.sendEmptyMessageDelayed(WHAT_NEXT_PAGE,builder.autoNextDelayed)
        }
        return this
    }

    /**
     * 开启自动轮播后，需要主动调用生命周期方法，才会自动停止轮播
     * 以此来防止生命周期之外的异常
     */
    fun onPause(): BannerUtil{
        autoHandler.removeMessages(WHAT_NEXT_PAGE)
        return this
    }

    /**
     * 下一个页面
     */
    fun nextPosition(): BannerUtil{
        if(positionDirection){
            position++
        }else{
            position--
        }
        if(position < 0){
            positionDirection = true
        }else if(position >= recyclerView.adapter.itemCount){
            positionDirection = false
        }
        smoothScrollToPosition(position)
        return this
    }

    /**
     * 对滑动的监听中转
     */
    override fun onSelected(position: Int, state: ScrollState){
        this.position = position
        for (listener in onSelectedListenerList){
            listener.onSelected(position,state)
        }
    }

    /**
     * 添加一个监听滚动的监听器
     */
    fun addOnSelectedListener(listener: OnSelectedListener): BannerUtil{
        onSelectedListenerList.add(listener)
        return this
    }

    /**
     * 移出一个监听滚动的监听器
     */
    fun removeOnSelectedListener(listener: OnSelectedListener): BannerUtil{
        onSelectedListenerList.remove(listener)
        return this
    }

}