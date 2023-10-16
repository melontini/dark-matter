package me.melontini.dark_matter.api.base.util.mixin.annotations;

import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.Opcodes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Have you ever wanted to inject some code into a block, only to find out that neither the block nor its superclasses override the method?
 * <p>
 * Well, that is what this annotation is for. It will inject a dummy method into the block. That way, even if some mod overrides the method itself, your code will still run!
 * <p>
 * Only {@link org.spongepowered.asm.mixin.injection.Inject} is supported. Names must be provided in the intermediary format.
 * <p>
 * This annotation is very hacky and its stability is questionable. There's barely any validation.
 * Also, a lot of things have to go right for this to apply correctly.
 */
@ApiStatus.Experimental
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConstructDummy {

    /**
     * Only used to remap the method name.
     */
    String owner();

    String name();

    String desc();

    int access() default Opcodes.ACC_PUBLIC;

    @ApiStatus.NonExtendable
    String mixin() default "";
    @ApiStatus.NonExtendable
    int priority() default 0;

}
