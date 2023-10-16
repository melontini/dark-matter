package me.melontini.dark_matter.impl.base.util.mixin;

import me.melontini.dark_matter.api.base.util.Mapper;
import me.melontini.dark_matter.api.base.util.mixin.AsmUtil;
import me.melontini.dark_matter.api.base.util.mixin.IPluginPlugin;
import me.melontini.dark_matter.api.base.util.mixin.annotations.ConstructDummy;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.util.Annotations;

import java.util.Map;

import static me.melontini.dark_matter.api.base.util.Utilities.cast;

@ApiStatus.Internal
public class ConstructDummyPlugin implements IPluginPlugin {

    @Override //I have a feeling this is a bad idea.
    public void beforeApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        ClassNode mixinNode = mixinInfo.getClassNode(ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES | ClassReader.SKIP_CODE);

        for (MethodNode method : mixinNode.methods) {
            AnnotationNode node = Annotations.getVisible(method, ConstructDummy.class);
            if (node == null) continue;

            if (targetClass.superName == null) throw new IllegalStateException("Target class does not have a super class!");
            if (Annotations.getVisible(method, Inject.class) == null) throw new IllegalStateException("@ConstructDummy can only be applied to methods annotated with @Inject!");

            Map<String, Object> values = AsmUtil.mapAnnotationNode(node);
            String owner = cast(values.get("owner"));
            String name = cast(values.get("name"));
            String desc = cast(values.get("desc"));
            int access = cast(values.getOrDefault("access", Opcodes.ACC_PUBLIC));

            Type descType = Type.getType(desc);

            String mappedName = Mapper.mapMethod(owner, name, desc);
            String mappedDesc = Mapper.mapMethodDescriptor(descType);
            Type mappedDescType = Type.getType(mappedDesc);

            for (MethodNode methodNode : targetClass.methods) {
                if (mappedName.equals(methodNode.name) && mappedDesc.equals(methodNode.desc)) {
                    DarkMatterLog.debug("Skipped creating dummy method {}.{}{} as it already exists", targetClass.name, mappedName, mappedDesc);
                    return;
                }
            }

            AsmUtil.insAdapter(targetClass, access, mappedName, mappedDesc, ia -> {
                ia.load(0, Type.getType(Object.class));
                Type[] args = mappedDescType.getArgumentTypes();
                for (int i = 0; i < args.length; i++) {
                    ia.load(i + 1, args[i]);
                }
                ia.invokespecial(targetClass.superName.replace(".", "/"), mappedName, mappedDesc, false);
                ia.areturn(mappedDescType.getReturnType());
                ia.visitLocalVariable("this", "L" + targetClass.name + ";", null, new Label(), new Label(), 0);
                ia.visitMaxs(args.length + 1, args.length + 1);
            });
            DarkMatterLog.debug("Successfully created and added a dummy method {}.{}{}", targetClass.name, mappedName, mappedDesc);
        }
    }
}
