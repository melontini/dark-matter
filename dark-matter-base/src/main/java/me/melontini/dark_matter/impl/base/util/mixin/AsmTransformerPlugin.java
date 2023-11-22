package me.melontini.dark_matter.impl.base.util.mixin;

import me.melontini.dark_matter.api.base.reflect.Reflect;
import me.melontini.dark_matter.api.base.util.classes.Tuple;
import me.melontini.dark_matter.api.base.util.mixin.AsmUtil;
import me.melontini.dark_matter.api.base.util.mixin.IAsmTransformer;
import me.melontini.dark_matter.api.base.util.mixin.IPluginPlugin;
import me.melontini.dark_matter.api.base.util.mixin.annotations.AsmTransformers;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.util.Annotations;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AsmTransformerPlugin implements IPluginPlugin {

    private final HashMap<Tuple<String, String>, Set<IAsmTransformer>> transformers = new HashMap<>();

    @Override
    public void beforeApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        ClassNode mixinNode = mixinInfo.getClassNode(ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES | ClassReader.SKIP_CODE);

        AnnotationNode node = Annotations.getVisible(mixinNode, AsmTransformers.class);
        if (node == null) return;

        List<Type> types = AsmUtil.getAnnotationValue(node, "value", null);
        if (types == null) return;

        for (Type type : types) {
            try {
                Class<?> cls = Class.forName(type.getClassName());
                if (IAsmTransformer.class.isAssignableFrom(cls)) {
                    Set<IAsmTransformer> transformers = this.transformers.computeIfAbsent(Tuple.of(mixinInfo.getClassName(), targetClassName), k -> new HashSet<>());
                    transformers.add((IAsmTransformer) Reflect.setAccessible(cls.getDeclaredConstructor()).newInstance());
                    DarkMatterLog.debug("Added transformer {} to mixin {}", type.getClassName(), mixinInfo.getClassName());
                } else throw new IllegalStateException("javac failed me. %s is not a transformer!".formatted(type.getClassName()));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Specified transformer class not found! " + type.getClassName(), e);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {
                throw new RuntimeException("Failed to construct transformer class! " + type.getClassName(), e);
            }
        }

        Set<IAsmTransformer> transformers = this.transformers.get(Tuple.of(mixinInfo.getClassName(), targetClassName));
        if (transformers != null) transformers.forEach(transformer -> transformer.beforeApply(targetClass, mixinInfo));
    }

    @Override
    public void afterApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        Set<IAsmTransformer> transformers = this.transformers.get(Tuple.of(mixinInfo.getClassName(), targetClassName));
        if (transformers != null) transformers.forEach(transformer -> transformer.afterApply(targetClass, mixinInfo));
    }
}
