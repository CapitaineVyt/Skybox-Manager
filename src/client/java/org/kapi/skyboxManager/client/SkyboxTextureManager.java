package org.kapi.skyboxManager.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SkyboxTextureManager {
    public static final Map<String, Identifier> SKYBOX_TEXTURES = new HashMap<>();
    private static final String[] FACES = {"top", "bottom", "north", "south", "east", "west"};

    public static void loadSkybox(String folderName, Path configPath) {
        // Détruire les anciennes textures
        SKYBOX_TEXTURES.forEach((face, id) ->
                MinecraftClient.getInstance()
                        .getTextureManager()
                        .destroyTexture(id)
        );
        SKYBOX_TEXTURES.clear();

        File folder = configPath.resolve(folderName).toFile();

        for (String face : FACES) {
            File file = new File(folder, face + ".png");
            if (!file.exists()) continue;

            try (FileInputStream is = new FileInputStream(file)) {
                NativeImage image = NativeImage.read(is);
                NativeImageBackedTexture texture = new NativeImageBackedTexture(
                        () -> "skybox-manager:" + face, image
                );
                Identifier texId = Identifier.of("skybox-manager", "dynamic_" + face);
                MinecraftClient.getInstance()
                        .getTextureManager()
                        .registerTexture(texId, texture);
                SKYBOX_TEXTURES.put(face, texId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Loaded textures: " + SKYBOX_TEXTURES.size());
        SKYBOX_TEXTURES.forEach((k, v) -> System.out.println("  " + k + " -> " + v));
    }
}