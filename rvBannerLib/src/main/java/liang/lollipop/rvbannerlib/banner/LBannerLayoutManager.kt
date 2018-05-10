package liang.lollipop.rvbannerlib.banner

import android.graphics.PointF
import android.graphics.Rect
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.ViewGroup

/**
 * 使用RecyclerView实现的Banner滚动图
 * 此类为RecyclerView的布局控制实现
 * @author Lollipop
 * @date 2018/05/09
 */
class LBannerLayoutManager: RecyclerView.LayoutManager(),RecyclerView.SmoothScroller.ScrollVectorProvider{

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
     * 偏移量，如果是纵向排版，那么代表Y，如果是横向排版，那么代表X
     */
    private var offset = 0

    /**
     * 存放所有item的位置和尺寸
     */
    private val itemsFrames = SparseArray<Rect>()

    /**
     * 记录item是否已经展示
     */
    private val itemsAttached = SparseBooleanArray()

    /**
     * 滑动的状态
     */
    private var scrollState = ScrollState.IDLE

    /**
     * 滑动选中的监听
     */
    var onSelectedListener: OnSelectedListener? = null

    /**
     * 选中的序号
     */
    private var selectedPosition = -1

    /**
     * 获取一个ViewHolder默认的LayoutParams
     * 此LayoutParam将用于ItemView在RecyclerView中的布局
     */
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return if(orientation == Orientation.HORIZONTAL){
            RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }else{
            RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    /**
     * 对Item进行测量，布局
     */
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {

        //如果没有item,那么就清空所有的Item,并且结束
        if (itemCount == 0 || recycler == null || state == null) {
            offset = 0
            removeAndRecycleAllViews(recycler)
            return
        }
        //将所有View移动到回收站
        detachAndScrapAttachedViews(recycler)

        //计算占用的尺寸，Item的测量时，将缩小相应的尺寸，因此将占用部分计算出来
        val usedWidth: Int = getUsedWidth()

        val usedHeight: Int = getUsedHeight()

        //Item的默认位置偏移，默认第一个为居中位置
        var offsetX = (usedWidth * 0.5F).toInt()

        var offsetY = (usedHeight * 0.5F).toInt()

        val view = recycler.getViewForPosition(0)
        measureChildWithMargins(view, usedWidth, usedHeight)

        //view的尺寸
        val viewWidth = getDecoratedMeasuredWidth(view)
        val viewHeight = getDecoratedMeasuredHeight(view)

        for (position in 0 until itemCount) {
            val frame = itemsFrames.get(position)?:Rect()

            frame.set(offsetX, offsetY, offsetX + viewWidth, offsetY + viewHeight)

            itemsFrames.put(position, frame)
            itemsAttached.put(position, false)

            if(orientation == Orientation.VERTICAL){
                offsetY += viewHeight
            }else{
                offsetX += viewWidth
            }

        }

        layoutItems(recycler, state)
    }

    /**
     * 摆放当前状态下要展示的item
     * @param recycler         Recycler to use for fetching potentially cached views for a
     * position
     * @param state            Transient state of RecyclerView
     */
    private fun layoutItems(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (state.isPreLayout) { // 跳过preLayout，preLayout主要用于支持动画
            return
        }

        val offsetX = if(orientation == Orientation.HORIZONTAL){offset}else{0}

        val offsetY = if(orientation == Orientation.VERTICAL){offset}else{0}

        // 当前scroll offset状态下的显示区域
        val displayFrame = Rect(offsetX, offsetY, getHorizontalSpace()+offsetX, getVerticalSpace()+offsetY)

        /**
         * 移除已显示的但在当前scroll offset状态下处于屏幕外的item
         */
        val childFrame = Rect()
        for (i in 0 until childCount) {
            val child = getChildAt(i)?:continue
            childFrame.left = getDecoratedLeft(child)
            childFrame.top = getDecoratedTop(child)
            childFrame.right = getDecoratedRight(child)
            childFrame.bottom = getDecoratedBottom(child)

            if (!Rect.intersects(displayFrame, childFrame)) {
                itemsAttached.put(getPosition(child), false)
                removeAndRecycleView(child, recycler)
            }
        }

        //计算占用的尺寸，Item的测量时，将缩小相应的尺寸，因此将占用部分计算出来
        val usedWidth: Int = getUsedWidth()

        val usedHeight: Int = getUsedHeight()

        /**
         * 摆放需要显示的item
         * 由于RecyclerView实际上并没有scroll，也就是说RecyclerView容器的滑动效果是依赖于LayoutManager对item进行平移来实现的
         * 故在放置item时要将item的计算位置平移到实际位置
         */
        for (i in 0 until itemCount) {
            if (Rect.intersects(displayFrame, itemsFrames.get(i))) {
                /**
                 * 在onLayoutChildren时由于移除了所有的item view，可以遍历全部item进行添加
                 * 在scroll时就不同了，由于scroll时会先将已显示的item view进行平移，
                 * 然后移除屏幕外的item view，此时仍然在屏幕内显示的item view就无需再次添加了
                 */
                if (!itemsAttached.get(i)) {
                    val scrap = recycler.getViewForPosition(i)
                    measureChildWithMargins(scrap,usedWidth,usedHeight)
                    addView(scrap)
                    val frame = itemsFrames.get(i)
                    layoutDecorated(scrap,
                            frame.left - offsetX,
                            frame.top - offsetY,
                            frame.right - offsetX,
                            frame.bottom - offsetY) // Important！布局到RecyclerView容器中，所有的计算都是为了得出任意position的item的边界来布局

                    itemsAttached.put(i, true)
                }
            }
        }

        val borderX = usedWidth/2
        val borderY = usedHeight/2

        displayFrame.set(displayFrame.left + borderX,
                displayFrame.top+borderY,
                displayFrame.right-borderX,
                displayFrame.bottom-borderX)

        var selectedIndex = -1

        //对非主要Item进行缩放处理
        for(i in 0 until itemCount){

            val child = getChildAt(i)?:continue
            val position = getPosition(child)
            val weight = offsetWeight(displayFrame,itemsFrames[position])
            val scale = (1-scaleGap)*weight+scaleGap

            child.scaleY = scale
            child.scaleX = scale

            if(selectedIndex < 0 && weight >= 0.5F){
                selectedIndex = i
            }

        }

        selectedPosition = if(selectedIndex < 0){
            -1
        }else{
            getPosition(getChildAt(selectedIndex))
        }
        onSelectedListener?.onSelected(selectedPosition,scrollState)

    }

    /**
     * 获取位置偏移的比例，以此来计算Item的缩放
     */
    private fun offsetWeight(displayFrame: Rect, itemFrame: Rect): Float{

        return if(orientation == Orientation.HORIZONTAL){
            overlapLength(displayFrame.left,itemFrame.left,displayFrame.right,itemFrame.right) * 1.0F / displayFrame.width()
        }else{
            overlapLength(displayFrame.top,itemFrame.top,displayFrame.bottom,itemFrame.bottom) * 1.0F / displayFrame.height()
        }
    }

    /**
     * 计算两个线段之间的重叠部分的长度
     */
    private fun overlapLength(start1: Int,start2: Int,end1: Int,end2: Int): Int{
        return Math.min(end1,end2) - Math.max(start1,start2)
    }

    /**
     * 获取已使用的宽度，即不可用于Item的缩进宽度
     */
    private fun getUsedWidth(): Int{
        //计算占用的尺寸，Item的测量时，将缩小相应的尺寸，因此将占用部分计算出来
        return if(orientation == Orientation.HORIZONTAL){
            if(secondaryExposed == 0){
                (width * (secondaryExposedWeight * 2)).toInt()
            }else{
                secondaryExposed * 2
            }
        }else{
            0
        }
    }

    /**
     * 获取已使用的高度，即不可用于Item的缩进高度
     */
    private fun getUsedHeight(): Int{
        return if(orientation == Orientation.VERTICAL){
            if(secondaryExposed == 0){
                (height * (secondaryExposedWeight * 2)).toInt()
            }else{
                secondaryExposed * 2
            }
        }else{
            0
        }
    }

    /**
     * 容器去除padding后的宽度
     * @return 实际可摆放item的空间
     */
    private fun getHorizontalSpace(): Int {
        return width - paddingRight - paddingLeft
    }

    /**
     * 容器去除padding后的高度
     * @return 实际可摆放item的空间
     */
    private fun getVerticalSpace(): Int {
        return height - paddingBottom - paddingTop
    }

    /**
     * 横向滑动时，回调的方法
     */
    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {

        val tempX = offset + dx

        var scrollLength = dx

        val horizontalSpace = getHorizontalSpace()

        val usedWidth = getUsedWidth()

        val minOffset = horizontalSpace/2 * -1
        val maxOffset = itemCount * (horizontalSpace - usedWidth) + usedWidth - horizontalSpace/2

        if( tempX < minOffset ){
            scrollLength = minOffset - offset
        }else if(tempX > maxOffset){
            scrollLength = maxOffset - offset
        }

        offset += scrollLength

        offsetChildrenHorizontal(-scrollLength)
        if(recycler != null && state != null){
            layoutItems(recycler,state)
        }

        return scrollLength
    }

    /**
     * 获取当前位置到指定位置的距离间隔
     */
    override fun computeScrollVectorForPosition(targetPosition: Int): PointF {

        val leftOff = getUsedWidth() / 2
        val topOff = getUsedHeight() / 2

        val itemHeight = getVerticalSpace() - getUsedHeight()
        val itemWidth = getHorizontalSpace() - getUsedWidth()

        val left = if(orientation == Orientation.HORIZONTAL){
            1.0F * targetPosition * itemWidth + leftOff - offset
        }else{
            0F
        }
        val top = if(orientation == Orientation.VERTICAL){
            1.0F * targetPosition * itemHeight + topOff  - offset
        }else{
            0F
        }

        return PointF(left,top)
    }

    /**
     * 纵向滑动时，回调的方法
     */
    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        val tempY = offset + dy

        var scrollLength = dy

        val verticalSpace = getVerticalSpace()

        val usedHeight = getUsedHeight()

        val minOffset = verticalSpace/2 * -1
        val maxOffset = itemCount * (verticalSpace - usedHeight) + usedHeight - verticalSpace/2

        if( tempY < minOffset ){
            scrollLength = minOffset - offset
        }else if(tempY > maxOffset){
            scrollLength = maxOffset - offset
        }

        offset += scrollLength

        offsetChildrenVertical(-scrollLength)
        if(recycler != null && state != null){
            layoutItems(recycler,state)
        }

        return scrollLength
    }

    /**
     * 滑动至指定为的方法
     */
    override fun scrollToPosition(position: Int) {
        val pos = Math.min(Math.max(position, 0),itemCount)

        offset = if(orientation == Orientation.HORIZONTAL){
            itemsFrames.get(pos).left
        }else{
            itemsFrames.get(pos).top
        }

        requestLayout()
    }

    /**
     * 以动画的形式，带有中间过程的滑动到指定位置
     */
    override fun smoothScrollToPosition(recyclerView: RecyclerView?, state: RecyclerView.State?, position: Int) {
        if(recyclerView == null || state == null){
            return
        }

        val pos = Math.min(Math.max(position, 0),itemCount)

        val scroller = LinearSmoothScroller(recyclerView.context)
        scroller.targetPosition = pos
        startSmoothScroll(scroller)
    }

    /**
     * 是否允许横向滑动
     */
    override fun canScrollHorizontally(): Boolean {
        return orientation == Orientation.HORIZONTAL
    }

    /**
     * 是否允许纵向滑动
     */
    override fun canScrollVertically(): Boolean {
        return orientation == Orientation.VERTICAL
    }

    /**
     * 滑动状态改变的回调方法
     */
    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        scrollState = when(state){

            ScrollState.SETTLING.value -> ScrollState.SETTLING

            ScrollState.DRAGGING.value -> ScrollState.DRAGGING

            else -> ScrollState.IDLE

        }

        onSelectedListener?.onSelected(selectedPosition,scrollState)

    }

}