package org.kapi.skyboxManager.client.mixin;

import com.mojang.blaze3d.vertex.VertexFormat;
import org.kapi.skyboxManager.client.SkyboxTextureManager;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.WorldRenderer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalDouble;
import java.util.OptionalInt;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Shadow
    private DefaultFramebufferSet framebufferSet;

    @Inject(
            method = "renderSky(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/client/render/Camera;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onRenderSky(FrameGraphBuilder frameGraphBuilder, Camera camera,
                             GpuBufferSlice fogBuffer, CallbackInfo ci) {
        if (SkyboxTextureManager.SKYBOX_TEXTURES.size() < 6) return;
        ci.cancel();

        FramePass framePass = frameGraphBuilder.createPass("custom_skybox");
        this.framebufferSet.mainFramebuffer = framePass.transfer(this.framebufferSet.mainFramebuffer);
        Handle<Framebuffer> handle = this.framebufferSet.mainFramebuffer;

        framePass.setRenderer(() -> {
            Framebuffer fb = handle.get();
            GpuTextureView colorView = fb.getColorAttachmentView();
            GpuTextureView depthView = fb.getDepthAttachmentView();
            renderSkybox(colorView, depthView);
        });
    }

    private void renderSkybox(GpuTextureView colorView, GpuTextureView depthView) {
        String[] faces = {"north", "south", "west", "east", "top", "bottom"};
        float s = 100.0F;

        RenderSystem.ShapeIndexBuffer indexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
        GpuBuffer ib = indexBuffer.getIndexBuffer(6);

        for (String face : faces) {
            Identifier id = SkyboxTextureManager.SKYBOX_TEXTURES.get(face);
            if (id == null) continue;

            AbstractTexture abstractTexture = MinecraftClient.getInstance()
                    .getTextureManager().getTexture(id);
            if (abstractTexture == null) continue;

            GpuTextureView view = abstractTexture.getGlTextureView();

            int vertexSize = VertexFormats.POSITION_TEXTURE.getVertexSize();
            try (BufferAllocator allocator = BufferAllocator.fixedSized(4 * vertexSize)) {
                BufferBuilder bufferBuilder = new BufferBuilder(allocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
                Matrix4f matrix = new Matrix4f();

                switch (face) {
                    case "north" -> {
                        bufferBuilder.vertex(matrix, -s, -s, -s).texture(0f, 1f);
                        bufferBuilder.vertex(matrix,  s, -s, -s).texture(1f, 1f);
                        bufferBuilder.vertex(matrix,  s,  s, -s).texture(1f, 0f);
                        bufferBuilder.vertex(matrix, -s,  s, -s).texture(0f, 0f);
                    }
                    case "south" -> {
                        bufferBuilder.vertex(matrix,  s, -s,  s).texture(0f, 1f);
                        bufferBuilder.vertex(matrix, -s, -s,  s).texture(1f, 1f);
                        bufferBuilder.vertex(matrix, -s,  s,  s).texture(1f, 0f);
                        bufferBuilder.vertex(matrix,  s,  s,  s).texture(0f, 0f);
                    }
                    case "west" -> {
                        bufferBuilder.vertex(matrix, -s, -s,  s).texture(0f, 1f);
                        bufferBuilder.vertex(matrix, -s, -s, -s).texture(1f, 1f);
                        bufferBuilder.vertex(matrix, -s,  s, -s).texture(1f, 0f);
                        bufferBuilder.vertex(matrix, -s,  s,  s).texture(0f, 0f);
                    }
                    case "east" -> {
                        bufferBuilder.vertex(matrix,  s, -s, -s).texture(0f, 1f);
                        bufferBuilder.vertex(matrix,  s, -s,  s).texture(1f, 1f);
                        bufferBuilder.vertex(matrix,  s,  s,  s).texture(1f, 0f);
                        bufferBuilder.vertex(matrix,  s,  s, -s).texture(0f, 0f);
                    }
                    case "top" -> {
                        bufferBuilder.vertex(matrix, -s,  s, -s).texture(0f, 0f);
                        bufferBuilder.vertex(matrix,  s,  s, -s).texture(1f, 0f);
                        bufferBuilder.vertex(matrix,  s,  s,  s).texture(1f, 1f);
                        bufferBuilder.vertex(matrix, -s,  s,  s).texture(0f, 1f);
                    }
                    case "bottom" -> {
                        bufferBuilder.vertex(matrix, -s, -s,  s).texture(0f, 1f);
                        bufferBuilder.vertex(matrix,  s, -s,  s).texture(1f, 1f);
                        bufferBuilder.vertex(matrix,  s, -s, -s).texture(1f, 0f);
                        bufferBuilder.vertex(matrix, -s, -s, -s).texture(0f, 0f);
                    }
                }

                try (BuiltBuffer builtBuffer = bufferBuilder.end()) {
                    GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(
                            () -> "Skybox " + face, 40, builtBuffer.getBuffer()
                    );

                    GpuBufferSlice transforms = RenderSystem.getDynamicUniforms().write(
                            RenderSystem.getModelViewMatrix(),
                            new Vector4f(1f, 1f, 1f, 1f),
                            new Vector3f(),
                            new Matrix4f(),
                            0f
                    );

                    try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder()
                            .createRenderPass(() -> "Skybox " + face, colorView, OptionalInt.empty(), depthView, OptionalDouble.empty())) {
                        renderPass.setPipeline(RenderPipelines.POSITION_TEX_COLOR_CELESTIAL);
                        RenderSystem.bindDefaultUniforms(renderPass);
                        renderPass.setUniform("DynamicTransforms", transforms);
                        renderPass.bindSampler("Sampler0", view);
                        renderPass.setVertexBuffer(0, gpuBuffer);
                        renderPass.setIndexBuffer(ib, indexBuffer.getIndexType());
                        renderPass.drawIndexed(0, 0, 6, 1);
                    }

                    gpuBuffer.close();
                }
            }
        }
    }
}