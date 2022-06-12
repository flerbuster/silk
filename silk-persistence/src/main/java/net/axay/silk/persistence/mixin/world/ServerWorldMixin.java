package net.axay.silk.persistence.mixin.world;

import net.axay.silk.persistence.CompoundProvider;
import net.axay.silk.persistence.PersistentCompound;
import net.axay.silk.persistence.PersistentCompoundImpl;
import net.axay.silk.persistence.internal.CompoundPersistentState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin implements CompoundProvider {
    @Unique
    private final PersistentCompound compound = new PersistentCompoundImpl();

    @Shadow public abstract DimensionDataStorage getDataStorage();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        getDataStorage().computeIfAbsent(
            nbt -> CompoundPersistentState.Companion.load(nbt, compound),
            () -> new CompoundPersistentState(compound),
            PersistentCompoundImpl.CUSTOM_DATA_KEY
        );
    }

    @NotNull
    @Override
    public PersistentCompound getCompound() {
        return compound;
    }
}