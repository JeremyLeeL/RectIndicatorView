# RectIndicatorView

Android方形边框文字选择器
[ ![Download](https://api.bintray.com/packages/lym6437/AndroidRepository/RectIndicatorView/images/download.svg) ](https://bintray.com/lym6437/AndroidRepository/RectIndicatorView/_latestVersion)

## 属性及使用说明

* 使用
> 
        implementation 'com.jeremy.lym:RectIndicatorView:1.0.0'

* 自定义属性
> 
        <!--文字未选中颜色-->
        <attr name="rectTextColorNormal" format="color"/>
        <!--文字选中后颜色-->
        <attr name="rectTextColorSelected" format="color"/>
        <!--文字大小-->
        <attr name="rectTextSize" format="dimension"/>
        <!--边框颜色-->
        <attr name="rectNormalColor" format="color"/>
        <!--被选中文字边框颜色-->
        <attr name="rectSelectedColor" format="color"/>
        <!--边框粗细-->
        <attr name="rectStrokeWidth" format="dimension"/>
        <!--是否显示viewPager切换动画-->
        <attr name="showViewPagerAnimation" format="boolean"/>
        <!--红点尺寸-->
        <attr name="redPointSize" format="dimension"/>
* 示例代码
> 
        viewPager.adapter = Adapter()
        rectIndicator.data = mutableListOf("第一页", "第二页", "第三页")
        //不仅仅配合ViewPager使用，也可以单独使用
        rectIndicator.setUpWithViewPager(viewPager)
        //设置默认选中项，如果有viewPager联动则设置之后顺带着连viewPager一起也设置了
        rectIndicator.setCurrentPosition(1)
        indicator.onItemCheckedListener = {position ->
                //do something
        }
        
![image](https://github.com/JeremyLeeL/RectIndicatorView/blob/master/rectIndicatorWithNoViewPagerAnim.gif)
![image](https://github.com/JeremyLeeL/RectIndicatorView/blob/master/rectIndicatorWithViewPagerAnim.gif)
