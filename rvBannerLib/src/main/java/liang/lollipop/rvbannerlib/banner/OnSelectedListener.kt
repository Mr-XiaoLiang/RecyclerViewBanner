package liang.lollipop.rvbannerlib.banner

/**
 * Item滚动到中心时，得到的回调
 * @author Lollipop
 */
interface OnSelectedListener {

    fun onSelected(position: Int,state: ScrollState)

}