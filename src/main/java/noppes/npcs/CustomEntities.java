/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.EntityType$Builder
 *  net.minecraft.entity.EntityType$EntityFactory
 *  net.minecraft.entity.SpawnGroup
 *  net.minecraft.entity.projectile.thrown.ThrownEntity
 *  net.minecraft.registry.Registry
 *  net.minecraft.util.Identifier
 *  net.minecraft.entity.attribute.DefaultAttributeContainer
 *  net.minecraft.registry.Registries
 */
package noppes.npcs;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.registry.Registries;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityChairMount;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPC64x32;
import noppes.npcs.entity.EntityNPCGolem;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.EntityNpcAlex;
import noppes.npcs.entity.EntityNpcClassicPlayer;
import noppes.npcs.entity.EntityNpcCrystal;
import noppes.npcs.entity.EntityNpcDragon;
import noppes.npcs.entity.EntityNpcPony;
import noppes.npcs.entity.EntityNpcSlime;
import noppes.npcs.entity.EntityProjectile;

public class CustomEntities {
    public static EntityType<? extends EntityNPCInterface> entityNpcPony;
    public static EntityType<? extends EntityNPCInterface> entityNpcCrystal;
    public static EntityType<? extends EntityNPCInterface> entityNpcSlime;
    public static EntityType<? extends EntityNPCInterface> entityNpcDragon;
    public static EntityType<? extends EntityNPCInterface> entityNPCGolem;
    public static EntityType<? extends EntityNPCInterface> entityCustomNpc;
    public static EntityType<? extends EntityNPCInterface> entityNPC64x32;
    public static EntityType<? extends EntityNPCInterface> entityNpcAlex;
    public static EntityType<? extends EntityNPCInterface> entityNpcClassicPlayer;
    public static EntityType<?> entityChairMount;
    public static EntityType<? extends ThrownEntity> entityProjectile;
    private static List<EntityType<?>> types;

    public static void registerEntities() {
        types.clear();
        entityNpcPony = CustomEntities.registerNpc(EntityNpcPony.class, "npcpony", EntityNpcPony::new);
        entityNpcCrystal = CustomEntities.registerNpc(EntityNpcCrystal.class, "npccrystal", EntityNpcCrystal::new);
        entityNpcSlime = CustomEntities.registerNpc(EntityNpcSlime.class, "npcslime", EntityNpcSlime::new);
        entityNpcDragon = CustomEntities.registerNpc(EntityNpcDragon.class, "npcdragon", EntityNpcDragon::new);
        entityNPCGolem = CustomEntities.registerNpc(EntityNPCGolem.class, "npcgolem", EntityNPCGolem::new);
        entityCustomNpc = CustomEntities.registerNpc(EntityCustomNpc.class, "customnpc", EntityCustomNpc::new);
        entityNPC64x32 = CustomEntities.registerNpc(EntityNPC64x32.class, "customnpc64x32", EntityNPC64x32::new);
        entityNpcAlex = CustomEntities.registerNpc(EntityNpcAlex.class, "customnpcalex", EntityNpcAlex::new);
        entityNpcClassicPlayer = CustomEntities.registerNpc(EntityNpcClassicPlayer.class, "customnpcclassic", EntityNpcClassicPlayer::new);
        entityChairMount = CustomEntities.registerNewentity(EntityChairMount.class, "customnpcchairmount", EntityChairMount::new, 64, 10, false, 0.001f, 0.001f);
        entityProjectile = CustomEntities.registerNewentity(EntityProjectile.class, "customnpcprojectile", EntityProjectile::new, 64, 20, true, 0.5f, 0.5f);
    }

    @SuppressWarnings("unchecked")
    public static void attribute() {
        for (EntityType<?> type : types) {
            FabricDefaultAttributeRegistry.register((EntityType<? extends LivingEntity>)type, (DefaultAttributeContainer)EntityNPCInterface.createMobAttributes().build());
        }
    }

    private static <T extends Entity> EntityType<T> buildNpcEntityType(EntityType.EntityFactory<T> factoryIn) {
        return FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, factoryIn).trackedUpdateRate(3).trackRangeChunks(10).dimensions(EntityDimensions.changing(1.0f, 1.0f)).build();
    }

    private static <T extends Entity> EntityType<T> registerNpc(Class<? extends Entity> c, String name, EntityType.EntityFactory<T> factoryIn) {
        Identifier registryName = new Identifier("customnpcs", name);
        EntityType<T> type = CustomEntities.buildNpcEntityType(factoryIn);
        types.add(type);
        Registry.register((Registry)Registries.ENTITY_TYPE, registryName, type);
        if (CustomNpcs.FixUpdateFromPre_1_12) {
            Identifier legacyId = new Identifier("customnpcs." + name);
            Registry.register((Registry)Registries.ENTITY_TYPE, legacyId, CustomEntities.buildNpcEntityType(factoryIn));
        }
        return type;
    }

    @SuppressWarnings("unused")
    private static <T extends Entity> EntityType<T> registerNewentity(Class<? extends Entity> c, String name, EntityType.EntityFactory<T> factoryIn, int range, int update, boolean velocity, float width, float height) {
        EntityType<T> type = FabricEntityTypeBuilder.create(SpawnGroup.MISC, factoryIn).trackedUpdateRate(update).trackRangeChunks(4).dimensions(EntityDimensions.changing(width, height)).build();
        Identifier registryName = new Identifier("customnpcs", name);
        Registry.register((Registry)Registries.ENTITY_TYPE, registryName, type);
        return type;
    }

    static {
        types = new ArrayList();
    }
}

