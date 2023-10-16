package me.melontini.dark_matter.impl.enums;

import me.melontini.dark_matter.api.base.util.Mapper;
import me.melontini.dark_matter.api.base.util.mixin.AsmUtil;
import me.melontini.dark_matter.api.base.util.mixin.ExtendablePlugin;
import me.melontini.dark_matter.api.base.util.mixin.IPluginPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.lang.reflect.Modifier;
import java.util.Set;

@ApiStatus.Internal
public class DarkMatterEnumsPlugin extends ExtendablePlugin {

    @Override
    protected void collectPlugins(Set<IPluginPlugin> plugins) {
        plugins.add(DefaultPlugins.publicizePlugin());
    }

    @Override
    public void beforeApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (mixinClassName.contains("enhanced_enums")) {
            if (mixinClassName.contains("enhanced_enums.EnchantmentTargetMixin")) {
                hackEnchantmentTarget(targetClass);//hacking the enum by implementing isAcceptableItem and removing "abstract".
                //Will this break something? Probably yes. But this was a fun experiment nonetheless.
            }
        }
    }

    private void hackEnchantmentTarget(ClassNode targetClass) {
        //final String resolvedEnchantmentTarget = MAPPING_RESOLVER.mapClassName("intermediary", "net.minecraft.class_1886");
        final String resolvedItem = getMappingResolver().mapClassName("intermediary", "net.minecraft.class_1792");
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
