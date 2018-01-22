package com.aixianshengxian.view;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by CWJ on 2016/11/23 0023.
 */

public class ViewHolder {
    private SparseArray<View> views; // 存储ViewHolder中所有的控件（SparseArray类似于HashMap但性能强于HashMap）
    private View convertView; // 以前用过的布局

    /**
     * 构造函数，因为ViewHolder之后一个，因此声明为private私有类型，通过下面的getInstance()方法获取
     */
    private ViewHolder(Context context, int layoutId, ViewGroup parent) {
        this.views = new SparseArray<View>(); // 初始化控件集合
        this.convertView = LayoutInflater.from(context).inflate(layoutId, parent, false); // 第一次用布局
        this.convertView.setTag(this); // 初始化布局之后将一个ViewHolder放入布局中作为缓存
    }

    /**
     * 单例模式获取ViewHolder实例

     */

    public static ViewHolder getInstance(View convertView, Context context, int layoutId, ViewGroup parent) {
        //TODO:若使用缓存，则该方法出现数据重复问题
//        if (convertView == null) { // 如果convertView不存在，则需要new一个ViewHolder并返回
            return new ViewHolder(context, layoutId, parent);
//        } else { // 如果convertView存在，则只需要从convertView中将ViewHolder去出来返回即可
//            return (ViewHolder) convertView.getTag();
//        }
    }

    /**
     * 通过ID找到控件并返回
     */
    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId); // 先尝试着从控件集合中取出控件
        if (view == null) { // 如果view==null则说明控件没有加载到控件集合中，这时就需要手动查找控件并放到集合中
            view = this.convertView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 设置一个为TextView设置文本的方法
     */
    public ViewHolder setTextToTextView(int viewId,String text){
        View view = getView(viewId);

        if (view != null && view instanceof TextView) {
            ((TextView) view).setText(text);
        }

        return this;
    }

    public ViewHolder setViewSelectStatus(int viewId, boolean isSelected) {
        View view = getView(viewId);
        if (view != null) {
            view.setSelected(isSelected);
        }

        return this;
    }
    /***
     * 设置一个为Button设置文本的方法
     */
    public  ViewHolder setTextToButton(int viewId,String text){
        Button button = getView(viewId);
        if(button !=null){
            button.setText(text);
        }

        return this;
    }
    /**
     * 设置一个为ImageView设置图片资源的方法
     */
    public ViewHolder setResourceToImageView(int viewId,int resourceId){
        ImageView imageView = getView(viewId);
        if(imageView != null){
            imageView.setImageResource(resourceId);
        }
        return this;
    }

    public ViewHolder setUrlToImageView(int viewId,String url){
        ImageView imageView =getView(viewId);
        if(imageView != null){
           //ImageUtil.setImage(imageView,url);
        }
        return this;
    }
    /**
     * 设置一个为View设置监听的方法
     */
    public ViewHolder setListenerToView(int viewId, View.OnClickListener listener){
        View view = getView(viewId);
        if(view != null){
            view.setOnClickListener(listener);
        }

        return this;
    }
    /**
     * 设置一个为View设置可见的方法
     */
    public ViewHolder setVisableToView(int viewId,int visibility){
        View view = getView(viewId);
        if( view != null){
            view.setVisibility(visibility);
        }

        return this;
    }
    /**
     * 设置一个为View设置字体颜色的方法
     */
    public ViewHolder setTextColor(int viewId,int colorResources){
        TextView view =getView(viewId);
        if(view != null){
            view.setTextColor(view.getResources().getColor(colorResources));
        }

        return this;
    }







    /**
     * 开放一个返回convertView的方法，在适配器中会用到
     */
    public View getConvertView() {
        return this.convertView;
    }
}

