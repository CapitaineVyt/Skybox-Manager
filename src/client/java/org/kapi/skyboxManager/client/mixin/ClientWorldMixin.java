package org.kapi.skyboxManager.client.mixin;

import org.kapi.skyboxManager.SkyboxManager;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

    @Shadow
    private ClientWorld.Properties clientWorldProperties;

    @Inject(method = "setTime", at = @At("HEAD"), cancellable = true)
    private void onSetTime(long time, long timeOfDay, boolean shouldTickTimeOfDay, CallbackInfo ci) {
        if (SkyboxManager.lockTime) {
            ci.cancel();
            if (this.clientWorldProperties != null) {
                this.clientWorldProperties.setTimeOfDay(SkyboxManager.lockedTime);
            }
        }
    }

    @Inject(method = "tickTime", at = @At("HEAD"), cancellable = true)
    private void onTickTime(CallbackInfo ci) {
        if (SkyboxManager.lockTime) {
            ci.cancel();
        }
    }
}