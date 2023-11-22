package me.melontini.dark_matter.impl.base.util.mixin;

import me.melontini.dark_matter.api.base.util.mixin.IPluginPlugin;
import me.melontini.dark_matter.api.base.util.mixin.annotations.Publicize;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.util.Annotations;

import java.lang.reflect.Modifier;

public class PublicizePlugin implements IPluginPlugin {

    private static final String PUBLICIZE_DESC = "L" + Publicize.class.getName().replace(".", "/") + ";";

    @Override
    public void afterApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        for (FieldNode fieldNode : targetClass.fields) {
            if (Annotations.getVisible(fieldNode, Publicize.class) == null) continue;

            publicize(fieldNode);
            fieldNode.visibleAnnotations.removeIf(node -> PUBLICIZE_DESC.equals(node.desc));
        }

        for (MethodNode methodNode : targetClass.methods) {
            if (Annotations.getVisible(methodNode, Publicize.class) == null) continue;

            publicize(methodNode);
            methodNode.visibleAnnotations.removeIf(node -> PUBLICIZE_DESC.equals(node.desc));
        }
    }

    private static void publicize(MethodNode methodNode) {
        if (Modifier.isPrivate(methodNode.access) || Modifier.isProtected(methodNode.access)) {
            DarkMatterLog.debug("Publicized method: " + methodNode.name + methodNode.desc);
            methodNode.access = publicize(methodNode.access);
        }
    }

    private static void publicize(FieldNode fieldNode) {
        if (Modifier.isPrivate(fieldNode.access) || Modifier.isProtected(fieldNode.access)) {
            DarkMatterLog.debug("Publicized field: " + fieldNode.name + fieldNode.desc);
            fieldNode.access = publicize(fieldNode.access);
        }
    }

    private static int publicize(int access) {
        return (access & ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED)) | Opcodes.ACC_PUBLIC;
    }
}
