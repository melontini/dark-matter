package me.melontini.dark_matter.api.base.util.mixin.annotations;

import me.melontini.dark_matter.api.base.util.mixin.IAsmTransformer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmTransformers {

    Class<? extends IAsmTransformer>[] value();
}
