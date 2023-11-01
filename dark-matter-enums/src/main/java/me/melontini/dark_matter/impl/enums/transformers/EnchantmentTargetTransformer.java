package me.melontini.dark_matter.impl.enums.transformers;

import me.melontini.dark_matter.api.base.util.Mapper;
import me.melontini.dark_matter.api.base.util.mixin.AsmUtil;
import me.melontini.dark_matter.api.base.util.mixin.IAsmTransformer;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.lang.reflect.Modifier;

public class EnchantmentTargetTransformer implements IAsmTransformer {

    @Override
    public void beforeApply(ClassNode targetClass, IMixinInfo mixinInfo) {
        final String resolvedItem = Mapper.mapClass("net.minecraft.class_1792");
        final String resolvedIsAcceptableItem = Mapper.mapMethod("net.minecraft.class_1886", "method_8177", "(Lnet/minecraft/class_1792;)Z");

        if (Modifier.isAbstract(targetClass.access)) {
            targetClass.access = targetClass.access & ~Opcodes.ACC_ABSTRACT;
        }

        for (MethodNode method : targetClass.methods) {
            if (method.name.equals(resolvedIsAcceptableItem)) {
                if (Modifier.isAbstract(method.access)) {
                    method.access = method.access & ~Opcodes.ACC_ABSTRACT;
                    method.instructions = new InsnList();
                    AsmUtil.insAdapter(method, ia -> {
                        ia.iconst(0);
                        ia.areturn(Type.BOOLEAN_TYPE);
                        ia.visitLocalVariable("this", "L" + targetClass.name + ";", null, new Label(), new Label(), 0);
                        ia.visitMaxs(2, 2);
                    });
                    break;
                }
            }
        }
    }
}
