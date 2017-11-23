package retrofit2.converter.gson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import retrofit2.converter.gson.AbstractResponseConverter;

/**
 * Created by Administrator on 2017/5/23.
 *
 *注解类测试
 */

public class data {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Converter {

        Class<? extends AbstractResponseConverter> converter();
    }
}
