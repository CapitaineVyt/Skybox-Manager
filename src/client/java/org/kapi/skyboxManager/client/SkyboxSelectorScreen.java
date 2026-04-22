package org.kapi.skyboxManager.client;

import org.kapi.skyboxManager.SkyboxManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class SkyboxSelectorScreen extends Screen {
    public SkyboxSelectorScreen() {
        super(Text.literal("Skybox Selector"));
    }

    @Override
    protected void applyBlur(DrawContext context) {
    }

    @Override
    protected void init() {
        // Bouton Jour
        ButtonWidget[] jourBtn = new ButtonWidget[1];
        ButtonWidget[] nuitBtn = new ButtonWidget[1];

        jourBtn[0] = ButtonWidget.builder(Text.literal("☀ Day"), button -> {
            SkyboxManager.lockTime = true;
            SkyboxManager.lockedTime = 6000L;
            jourBtn[0].active = false;
            nuitBtn[0].active = true;
        }).dimensions(this.width / 2 - 102, 450, 100, 20).build();

        nuitBtn[0] = ButtonWidget.builder(Text.literal("🌙 Night"), button -> {
            SkyboxManager.lockTime = true;
            SkyboxManager.lockedTime = 18000L;
            nuitBtn[0].active = false;
            jourBtn[0].active = true;
        }).dimensions(this.width / 2 + 2, 450, 100, 20).build();

        // État initial
        if (SkyboxManager.lockTime && SkyboxManager.lockedTime == 6000L) {
            jourBtn[0].active = false;
        } else if (SkyboxManager.lockTime && SkyboxManager.lockedTime == 18000L) {
            nuitBtn[0].active = false;
        }

        this.addDrawableChild(jourBtn[0]);
        this.addDrawableChild(nuitBtn[0]);

        // Boutons skybox
        for (int i = 0; i < SkyboxManager.skyboxFolders.size(); i++) {
            String name = SkyboxManager.skyboxFolders.get(i);
            int y = 40 + i * 25;
            this.addDrawableChild(
                    ButtonWidget.builder(Text.literal(name), button -> {
                        SkyboxTextureManager.loadSkybox(name, SkyboxManager.configDir);
                        this.close();
                    }).dimensions(this.width / 2 - 100, y, 200, 20).build()
            );
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fillGradient(0, 0, this.width, this.height, 0x80000000, 0x80000000);
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                this.title,
                this.width / 2,
                15,
                0xFFFFFF
        );
        super.render(context, mouseX, mouseY, delta);
    }
}