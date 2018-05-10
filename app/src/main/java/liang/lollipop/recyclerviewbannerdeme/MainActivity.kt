package liang.lollipop.recyclerviewbannerdeme

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import liang.lollipop.rvbannerlib.BannerUtil
import liang.lollipop.rvbannerlib.banner.Orientation
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var bannerUtil: BannerUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initView()
    }

    private fun initView(){

        val dataList = ArrayList<Bean>()

        val random = Random()

        for(index in 0..35){
            dataList.add(Bean("Test$index",nextColor(random)))
        }

        bannerUtil = BannerUtil
                .with(recyclerView)
                .attachAdapter(TestAdapter(dataList,layoutInflater))
                .init()
                .isAutoNext(true)

        BannerUtil
                .with(recyclerView2)
                .attachAdapter(TestAdapter(dataList,layoutInflater))
                .setOrientation(Orientation.VERTICAL)
                .setSecondaryExposedWeight(0.15F)
                .init()

        val bannerList = ArrayList<Any>()
        bannerList.add(BannerBean(dataList))
        bannerList.addAll(dataList)
        recyclerView3.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        recyclerView3.adapter = BannerAdapter(bannerList,layoutInflater)
        recyclerView3.adapter.notifyDataSetChanged()

        fab.setOnClickListener{
            recyclerView.smoothScrollToPosition(30)
            recyclerView2.smoothScrollToPosition(30)
            recyclerView3.smoothScrollToPosition(31)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.action_use_java){
            startActivity(Intent(this,Main4JActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun nextColor(random: Random): Int{
        return Color.rgb(random.nextInt(255),random.nextInt(255),random.nextInt(255))
    }

    override fun onResume() {
        super.onResume()
        bannerUtil.onResume()
    }

    override fun onPause() {
        super.onPause()
        bannerUtil.onPause()
    }

    class TestAdapter(private val data: ArrayList<Bean>,
                      private val layoutInflater: LayoutInflater): RecyclerView.Adapter<TestHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestHolder {
            return TestHolder(layoutInflater.inflate(R.layout.item_test,parent,false))
        }

        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: TestHolder, position: Int) {
            holder.onBind(data[position])
        }


    }

    class BannerAdapter(private val data: ArrayList<Any>,
                        private val layoutInflater: LayoutInflater): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            return if(viewType == 0){
                BannerHolder(layoutInflater.inflate(R.layout.item_banner,parent,false))
            }else{
                TestHolder(layoutInflater.inflate(R.layout.item_test,parent,false))
            }

        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val bean = data[position]
            if(holder is BannerHolder && bean is BannerBean){
                holder.onBind(bean)
            }else if(holder is TestHolder && bean is Bean){
                holder.onBind(bean)
            }
        }

        override fun getItemViewType(position: Int): Int {
            return if(data[position] is BannerBean){0}else{1}
        }

    }

    class TestHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        private val textView: TextView = itemView.findViewById(R.id.itemText)

        fun onBind(bean: Bean){
            onBind(bean.text,bean.color)
        }

        fun onBind(text: String,color: Int){
            textView.text = text
            textView.setBackgroundColor(color)
        }

    }

    class BannerHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        private val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)
        private val bannerUtil = BannerUtil.with(recyclerView)
        private val dataList = ArrayList<Bean>()

        init {
            bannerUtil.attachAdapter(TestAdapter(dataList, LayoutInflater.from(itemView.context))).init()
        }

        fun onBind(bean: BannerBean){
            dataList.clear()
            dataList.addAll(bean.data)
            bannerUtil.notifyDataSetChanged()
        }

    }

    data class Bean(val text: String,val color: Int)

    data class BannerBean(val data: ArrayList<Bean>)
}
