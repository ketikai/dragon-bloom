package pers.ketikai.minecraft.forge.dragonbloom;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;
import pers.ketikai.minecraft.tags.dragonbloom.Tags;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.Name(Tags.NAME)
public class DragonBloomCore implements IFMLLoadingPlugin {

    private static final String[] TRANSFORMERS = new String[]{
            Transformer.class.getName()
    };

    @Override
    public String[] getASMTransformerClass() {
        return TRANSFORMERS;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public @Nullable String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    public static final class Transformer implements IClassTransformer {

        @Override
        public byte[] transform(String name, String transformedName, byte[] basicClass) {
            byte[] enhancedClass;
            ClassWriter writer;
            ClassReader reader;
            switch (transformedName) {
                case "blockbuster.emitter.BedrockEmitter":
                    writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                    reader = new ClassReader(basicClass);
                    reader.accept(new BedrockEmitterHooker(Opcodes.ASM5, writer), ClassReader.EXPAND_FRAMES);
                    enhancedClass = writer.toByteArray();
                    break;
                case "eos.moe.dragoncore.eea":
                    writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                    reader = new ClassReader(basicClass);
                    reader.accept(new EeaHooker(Opcodes.ASM5, writer), ClassReader.EXPAND_FRAMES);
                    enhancedClass = writer.toByteArray();
                    break;
                default:
                        return basicClass;
            }
            try {
                File file = new File("./transformed-classes/" + transformedName.replace(".", "/") + ".class");
                if (!file.getParentFile().mkdirs()) {
                    throw new IOException("Failed to create directory for transformed classes.");
                }
                Files.write(file.toPath(), enhancedClass);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return enhancedClass;
        }
    }

    private static final class BedrockEmitterHooker extends ClassVisitor {
        public BedrockEmitterHooker(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (!"setScheme".equals(name) || !"(Lblockbuster/BedrockScheme;Ljava/util/Map;)V".equals(desc)) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
            return new BedrockEmitterSetSchemeHooker(api, super.visitMethod(access, name, desc, signature, exceptions), access, name, desc);
        }
    }

    private static final class BedrockEmitterSetSchemeHooker extends AdviceAdapter {
        private volatile boolean once = true;

        public BedrockEmitterSetSchemeHooker(int api, MethodVisitor mv, int access, String name, String desc) {
            super(api, mv, access, name, desc);
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
            if (once && var == 0 && opcode == Opcodes.ALOAD) {
                this.once = false;
                loadThis();
                loadArg(0);
                loadArg(1);
                invokeStatic(
                        Type.getType("pers/ketikai/minecraft/forge/dragonbloom/DragonBloomHook"),
                        Method.getMethod("void hookBedrockEmitterSetScheme (blockbuster.emitter.BedrockEmitter, blockbuster.BedrockScheme, java.util.Map)")
                );
            }
            super.visitVarInsn(opcode, var);
        }
    }

    private static final class EeaHooker extends ClassVisitor {
        public EeaHooker(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (!"func_77036_a".equals(name) || !"(Lnet/minecraft/entity/EntityLivingBase;FFFFFF)V".equals(desc)) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
            return new EeaFunc_77036_aHooker(api, super.visitMethod(access, name, desc, signature, exceptions), access, name, desc);
        }
    }

    private static final class EeaFunc_77036_aHooker extends AdviceAdapter {

        public EeaFunc_77036_aHooker(int api, MethodVisitor mv, int access, String name, String desc) {
            super(api, mv, access, name, desc);
        }

        private boolean first = true;

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
//            // invokestatic net/minecraft/client/renderer/GlStateManager.func_179145_e ()V
//            if (opcode == Opcodes.INVOKESTATIC && "net/minecraft/client/renderer/GlStateManager".equals(owner) && "func_179145_e".equals(name) && "()V".equals(desc)) {
//                loadThis();
//                loadArg(0);
//                loadArg(1);
//                loadArg(2);
//                loadArg(3);
//                loadArg(4);
//                loadArg(5);
//                loadArg(6);
//                invokeStatic(
//                        Type.getType("pers/ketikai/minecraft/forge/dragonbloom/DragonBloomHook"),
//                        Method.getMethod("eos.moe.dragoncore.kea hookEeaFunc_77036_a1(eos.moe.dragoncore.kea, eos.moe.dragoncore.eea, net.minecraft.entity.EntityLivingBase, float, float, float, float, float, float)")
//                );
//            }
//            super.visitMethodInsn(opcode, owner, name, desc, itf);
//            // invokestatic net/minecraft/client/renderer/GlStateManager.func_179140_f ()V
//            if (opcode == Opcodes.INVOKESTATIC && "net/minecraft/client/renderer/GlStateManager".equals(owner) && "func_179140_f".equals(name) && "()V".equals(desc)) {
//                loadThis();
//                loadArg(0);
//                loadArg(1);
//                loadArg(2);
//                loadArg(3);
//                loadArg(4);
//                loadArg(5);
//                loadArg(6);
//                invokeStatic(
//                        Type.getType("pers/ketikai/minecraft/forge/dragonbloom/DragonBloomHook"),
//                        Method.getMethod("eos.moe.dragoncore.kea hookEeaFunc_77036_a0(eos.moe.dragoncore.kea, eos.moe.dragoncore.eea, net.minecraft.entity.EntityLivingBase, float, float, float, float, float, float)")
//                );
//            }
            // invokevirtual eos/moe/dragoncore/kea.k ()Z
            if (opcode == Opcodes.INVOKEVIRTUAL && "eos/moe/dragoncore/kea".equals(owner) && "k".equals(name) && "()Z".equals(desc)) {
                loadThis();
                loadArg(0);
                loadArg(1);
                loadArg(2);
                loadArg(3);
                loadArg(4);
                loadArg(5);
                loadArg(6);
                if (first) {
                    invokeStatic(
                            Type.getType("pers/ketikai/minecraft/forge/dragonbloom/DragonBloomHook"),
                            Method.getMethod("eos.moe.dragoncore.kea hookEeaFunc_77036_a0(eos.moe.dragoncore.kea, eos.moe.dragoncore.eea, net.minecraft.entity.EntityLivingBase, float, float, float, float, float, float)")
                    );
                    this.first = false;
                } else {
                    invokeStatic(
                            Type.getType("pers/ketikai/minecraft/forge/dragonbloom/DragonBloomHook"),
                            Method.getMethod("eos.moe.dragoncore.kea hookEeaFunc_77036_a1(eos.moe.dragoncore.kea, eos.moe.dragoncore.eea, net.minecraft.entity.EntityLivingBase, float, float, float, float, float, float)")
                    );
                }
            }
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }
}
