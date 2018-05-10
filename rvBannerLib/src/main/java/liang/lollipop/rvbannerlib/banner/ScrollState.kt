package liang.lollipop.rvbannerlib.banner

import android.support.v7.widget.RecyclerView

/**
 * 滑动状态
 * @author Lollipop
 */
enum class ScrollState(val value: Int) {

    /**
     * RecyclerView目前不在滚动。
     * The RecyclerView is not currently scrolling.
     */
    IDLE(RecyclerView.SCROLL_STATE_IDLE),
    /**
     * RecyclerView目前正在动画到最终位置，而不受外界控制。
     * The RecyclerView is currently animating to a final position while not under
     * outside control.
     */
    SETTLING(RecyclerView.SCROLL_STATE_SETTLING),
    /**
     * RecyclerView目前正在被外部输入如用户触摸输入拖动。
     * The RecyclerView is currently being dragged by outside input such as user touch input.
     */
    DRAGGING(RecyclerView.SCROLL_STATE_DRAGGING),;

    override fun toString(): String{
        return when(this){

            IDLE -> "IDLE"

            SETTLING -> "SETTLING"

            DRAGGING -> "DRAGGING"

        }
    }


}