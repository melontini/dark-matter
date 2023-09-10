package me.melontini.dark_matter.impl.base.util.mixin;

import me.melontini.dark_matter.api.base.util.mixin.AsmUtil;
import me.melontini.dark_matter.api.base.util.mixin.IPluginPlugin;
import me.melontini.dark_matter.api.base.util.mixin.annotations.ConstructDummy;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
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

            MappingResolver resolver = FabricLoader.getInstance().getMappingResolver();

            String mappedName = resolver.mapMethodName("intermediary", owner, name, desc);
            String mappedDesc = AsmUtil.mapStringFromDescriptor(descType, resolver);
            Type mappedDescType = Type.getType(mappedDesc);

            for (MethodNode methodNode : targetClass.methods) {
                if (mappedName.equals(methodNode.name) && mappedDesc.equals(methodNode.desc)) {
                    DarkMatterLog.debug("Skipped creating dummy method {}.{}{} as it already exists", targetClass.name, mappedName, mappedDesc);
                    return;
                }
            }

            MethodNode methodNode = new MethodNode(Opcodes.ASM9, access, mappedName, mappedDesc, null, null);

            methodNode.visitVarInsn(Opcodes.ALOAD, 0);
            Type[] args = mappedDescType.getArgumentTypes();
            for (int i = 0; i < args.length; i++) {
                methodNode.visitVarInsn(args[i].getOpcode(Opcodes.ILOAD), i + 1);
            }
            methodNode.visitMethodInsn(Opcodes.INVOKESPECIAL, targetClass.superName.replace(".", "/"), mappedName, mappedDesc, false);
            methodNode.visitInsn(mappedDescType.getReturnType().getOpcode(Opcodes.IRETURN));;

            Label l0 = new Label();
            Label l1 = new Label();
            //Stacks and labels get recalculated, so who cares.
            methodNode.visitLocalVariable("this", "L" + targetClass.name.replace(".", "/") + ";", null, l0, l1, 0);
            for (int i = 0; i < args.length; i++) {
                methodNode.visitLocalVariable("arg" + i, args[i].getDescriptor(), null, l0, l1, i + 1);
            }
            methodNode.visitMaxs(0, args.length + 1);

            targetClass.methods.add(methodNode);
            DarkMatterLog.debug("Successfully created and added a dummy method {}.{}{}", targetClass.name, mappedName, mappedDesc);
        }
    }
}
