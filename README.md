# RecyclerViewBanner
这是一个基于RecyclerView制作的Banner资源库

本项目基于RecyclerView，通过自定义LayoutManager来实现。<br>
支持
 > * 横向模式
 > * 纵向模式
 > * 单页翻页
 > * 连页翻页
 > * 自动翻页
 > * 间距调整
 > * RecyclerView嵌套
 > * Kotlin项目
 > * Java项目
 > * 页面布局自定义
 > * Adapter自定义
 > * RecyclerView容器尺寸自定义
 > * 缩放大小自定义
 > * 数据源自定义

项目代码使用简单：
Java项目
``` java

//完整参数设置
BannerUtil4J.with(RecyclerView)//关联一个RecyclerView
                .attachAdapter( RecyclerView.Adapter )//传入RecyclerView的Adapter
                .setOrientation( Orientation.VERTICAL )//设置方向
                .setSecondaryExposedWeight( float )//设置两侧露出比例
                .setSecondaryExposed( int )//设置两侧露出距离，当距离为0时,上一行比例生效
                .setAutoNextDelayed( long )//设置自动翻页的间隔时间，单位ms
                .setPagerMode( boolean )//设置是否单页模式，一次只能翻一页
                .setScaleGap( float )//设置最小缩放比例
                .isAutoNext( boolean )//设置是否开启自动翻页
                .init();//执行初始化

//如果使用默认设置，那么可以像下方这样，3行代码
BannerUtil4J.with(RecyclerView)//关联一个RecyclerView
                .attachAdapter( RecyclerView.Adapter )//传入RecyclerView的Adapter
                .init();//执行初始化

//同时，工具还支持下列方法
BannerUtil4J.onResume();//关联页面生命周期，只有关联此方法后，才会触发自动翻页
BannerUtil4J.onPause();//关联页面生命周期，用于自动停止自动翻页
BannerUtil4J.smoothScrollToPosition( int );//带有中间动画的跳转
BannerUtil4J.scrollToPosition(int);//不带有中间动画的跳转
BannerUtil4J.nextPosition();//主动跳转至下一页

```

Kotlin项目
``` kotlin

//完整参数设置
BannerUtil.with(RecyclerView)//关联一个RecyclerView
                .attachAdapter( RecyclerView.Adapter )//传入RecyclerView的Adapter
                .setOrientation( Orientation.VERTICAL )//设置方向
                .setSecondaryExposedWeight( Float )//设置两侧露出比例
                .setSecondaryExposed( Int )//设置两侧露出距离，当距离为0时,上一行比例生效
                .setAutoNextDelayed( Long )//设置自动翻页的间隔时间，单位ms
                .setPagerMode( Boolean )//设置是否单页模式，一次只能翻一页
                .setScaleGap( Float )//设置最小缩放比例
                .isAutoNext( Boolean )//设置是否开启自动翻页
                .init()//执行初始化

//如果使用默认设置，那么可以像下方这样，3行代码
BannerUtil.with( RecyclerView )//关联一个RecyclerView
                .attachAdapter( RecyclerView.Adapter )//传入RecyclerView的Adapter
                .init()//执行初始化

//同时，工具还支持下列方法
BannerUtil.onResume()//关联页面生命周期，只有关联此方法后，才会触发自动翻页
BannerUtil.onPause()//关联页面生命周期，用于自动停止自动翻页
BannerUtil.smoothScrollToPosition( Int )//带有中间动画的跳转
BannerUtil.scrollToPosition( Int )//不带有中间动画的跳转
BannerUtil.nextPosition()//主动跳转至下一页

```

<br>



![demo](https://raw.githubusercontent.com/Mr-XiaoLiang/RecyclerViewBanner/master/video/demo.gif)
