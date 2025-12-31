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
            if (!"blockbuster.emitter.BedrockEmitter".equals(transformedName)) {
                return basicClass;
            }
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            ClassReader reader = new ClassReader(basicClass);
            reader.accept(new CV(Opcodes.ASM5, writer), ClassReader.EXPAND_FRAMES);
            byte[] enhancedClass = writer.toByteArray();
            try {
                File file = new File("./transformed-classes/" + transformedName.replace(".", "/") + ".class");
                if (file.getParentFile().mkdirs()) {
                    Files.write(file.toPath(), enhancedClass);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return enhancedClass;
        }
    }

    private static final class CV extends ClassVisitor {
        public CV(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (!"setScheme".equals(name) || !"(Lblockbuster/BedrockScheme;Ljava/util/Map;)V".equals(desc)) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
            return new MV(api, super.visitMethod(access, name, desc, signature, exceptions), access, name, desc);
        }
    }

    private static final class MV extends AdviceAdapter {
        private volatile boolean once = true;

        public MV(int api, MethodVisitor mv, int access, String name, String desc) {
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
                        Method.getMethod("blockbuster.BedrockScheme hookBedrockEmitterSetScheme (blockbuster.emitter.BedrockEmitter, blockbuster.BedrockScheme, java.util.Map)")
                );
                dup();
            }
            super.visitVarInsn(opcode, var);
        }
    }
}
