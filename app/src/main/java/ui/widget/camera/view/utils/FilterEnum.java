package ui.widget.camera.view.utils;

/**
 * Created by panbin on 2017/12/10.
 */

public class FilterEnum {

    public static GPUImageFilter getFilter(FilterE e){
        GPUImageFilter filter=null;
        switch (e){
            case NOFILTER:
                filter=new GPUImageFilter();
                break;
            case GRAY:
                filter=new GrayScaleFilter();
                break;
            default:
                filter=new GPUImageFilter();
                break;
        }
        return  filter;
    }

    public enum FilterE{
        NOFILTER,GRAY
    }

}
