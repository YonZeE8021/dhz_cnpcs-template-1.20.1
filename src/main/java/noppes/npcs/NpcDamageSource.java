/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Identifier
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.entity.damage.DamageType
 */
package noppes.npcs;

import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.entity.damage.DamageType;

public class NpcDamageSource {
    public static final RegistryKey<DamageType> NPC = RegistryKey.of((RegistryKey)RegistryKeys.DAMAGE_TYPE, (Identifier)new Identifier("customnpcs", "npc"));
}

