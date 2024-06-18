package com.aqutheseal.celestisynth.config.common;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.StringUtils;

public class CSCommonConfig {
    public final ForgeConfigSpec.IntValue solarisDmg, crescentiaDmg;
    
    public final ForgeConfigSpec.DoubleValue solarisSkillDmg;
    public final ForgeConfigSpec.DoubleValue solarisShiftSkillDmg;
    public final ForgeConfigSpec.IntValue solarisSkillCD;
    public final ForgeConfigSpec.IntValue solarisShiftSkillCD;
    public final ForgeConfigSpec.DoubleValue crescentiaSkillDmg;
    public final ForgeConfigSpec.DoubleValue crescentiaShiftSkillDmg;
    public final ForgeConfigSpec.IntValue crescentiaSkillCD;
    public final ForgeConfigSpec.IntValue crescentiaShiftSkillCD;
    public final ForgeConfigSpec.DoubleValue breezebreakerSkillDmg;
    public final ForgeConfigSpec.DoubleValue breezebreakerShiftSkillDmg;
    public final ForgeConfigSpec.DoubleValue breezebreakerSprintSkillDmg;
    public final ForgeConfigSpec.DoubleValue breezebreakerMidairSkillDmg;
    public final ForgeConfigSpec.IntValue breezebreakerSkillCD;
    public final ForgeConfigSpec.IntValue breezebreakerShiftSkillCD;
    public final ForgeConfigSpec.IntValue breezebreakerSprintSkillCD;
    public final ForgeConfigSpec.IntValue breezebreakerMidairSkillCD;
    public final ForgeConfigSpec.DoubleValue poltergeistSkillDmg;
    public final ForgeConfigSpec.DoubleValue poltergeistShiftSkillDmg;
    public final ForgeConfigSpec.IntValue poltergeistSkillCD;
    public final ForgeConfigSpec.IntValue poltergeistShiftSkillCD;
    public final ForgeConfigSpec.DoubleValue aquafloraSkillDmg;
    public final ForgeConfigSpec.DoubleValue aquafloraShiftSkillDmg;
    public final ForgeConfigSpec.DoubleValue aquafloraBloomSkillDmg;
    public final ForgeConfigSpec.DoubleValue aquafloraBloomShiftSkillDmg;
    public final ForgeConfigSpec.IntValue aquafloraSkillCD;
    public final ForgeConfigSpec.IntValue aquafloraShiftSkillCD;
    public final ForgeConfigSpec.IntValue aquafloraBloomSkillCD;
    public final ForgeConfigSpec.IntValue aquafloraBloomShiftSkillCD;
    public final ForgeConfigSpec.DoubleValue frostboundSkillDmg;
    public final ForgeConfigSpec.DoubleValue frostboundShiftSkillDmg;
    public final ForgeConfigSpec.IntValue frostboundSkillCD;
    public final ForgeConfigSpec.IntValue frostboundShiftSkillCD;

    public final ForgeConfigSpec.BooleanValue enablePoltergeistHeightDmg;
    public final ForgeConfigSpec.DoubleValue rainfallSerenityArrowDmg;
    public final ForgeConfigSpec.DoubleValue rainfallSerenityDrawSpeed;

    public CSCommonConfig(ForgeConfigSpec.Builder builder) {
        builder.push("Base Damage Modifications (Temporarily Unusable)");
        solarisDmg = baseDamage(builder, "solaris", 7);
        crescentiaDmg = baseDamage(builder, "crescentia", 8);
        builder.pop();

        builder.comment("Some attacks deal a specified amount of damage FOR EVERY HIT, so be careful as you tweak these values!");
        builder.comment(" ");

        builder.push("Value Modifiers - Solaris");
        solarisSkillDmg = skillDamage(builder, "solaris", "Spinning Flames - Full Round", 0.85);
        solarisShiftSkillDmg = skillDamage(builder, "solaris", "Spinning Flames - Soul Straight Dash [Shift]", 1.2);
        solarisSkillCD = skillCooldown(builder, "solaris", "Spinning Flames - Full Round", 70);
        solarisShiftSkillCD = skillCooldown(builder, "solaris", "Spinning Flames - Soul Straight Dash [Shift]", 130);
        builder.pop();

        builder.push("Value Modifiers - Crescentia");
        crescentiaSkillDmg = skillDamage(builder, "crescentia", "Lunar Celebration Barrage", 0.6);
        crescentiaShiftSkillDmg = skillDamage(builder, "crescentia", "Dragon Crescent Boom [Shift]", 0.45);
        crescentiaSkillCD = skillCooldown(builder, "crescentia", "Lunar Celebration Barrage", 100);
        crescentiaShiftSkillCD = skillCooldown(builder, "crescentia", "Dragon Crescent Boom [Shift]", 40);
        builder.pop();

        builder.push("Value Modifiers - Breezebreaker");
        breezebreakerSkillDmg = skillDamage(builder, "breezebreaker", "Galestorm + Dual Galestorm", 7.0);
        breezebreakerShiftSkillDmg = skillDamage(builder, "breezebreaker", "Full-Force Whirlwind Extravagance [Shift]", 0.85);
        breezebreakerSprintSkillDmg = skillDamage(builder, "breezebreaker", "Roar of the Wind [Sprint]", 11.5);
        breezebreakerMidairSkillDmg = skillDamage(builder, "breezebreaker", "Zephyr's Death Wheel [Mid-air]", 8.0);
        breezebreakerSkillCD = skillCooldown(builder, "breezebreaker", "Galestorm + Dual Galestorm", 15);
        breezebreakerShiftSkillCD = skillCooldown(builder, "breezebreaker", "Full-Force Whirlwind Extravagance [Shift]", 35);
        breezebreakerSprintSkillCD = skillCooldown(builder, "breezebreaker", "Roar of the Wind [Sprint]", 15);
        breezebreakerMidairSkillCD = skillCooldown(builder, "breezebreaker", "Zephyr's Death Wheel [Mid-air]", 40);
        builder.pop();

        builder.push("Value Modifiers - Poltergeist");
        poltergeistSkillDmg = skillDamage(builder, "Poltergeist", "Cosmic Steel Annihilation", 17.5);
        poltergeistShiftSkillDmg = skillDamage(builder, "Poltergeist", "Barrier Call", 10.0);
        poltergeistSkillCD = skillCooldown(builder, "Poltergeist", "Cosmic Steel Annihilation", 200);
        poltergeistShiftSkillCD = skillCooldown(builder, "Poltergeist", "Barrier Call", 80);

        enablePoltergeistHeightDmg = builder.comment("Enables the increasing of Poltergeist's Cosmic Steel Annihilation damage with smash height.").define("Enable Height Damage", true);
        builder.pop();

        builder.push("Value Modifiers - Frostbound");
        frostboundSkillDmg = skillDamage(builder, "Frostbound", "Dance of the Seven-Thousand Snowflakes", 17.5);
        frostboundShiftSkillDmg = skillDamage(builder, "Frostbound", "Calamity Frost Cast", 10.0);
        frostboundSkillCD = skillCooldown(builder, "Frostbound", "Dance of the Seven-Thousand Snowflakes", 160);
        frostboundShiftSkillCD = skillCooldown(builder, "Frostbound", "Calamity Frost Cast", 100);
        builder.pop();

        builder.push("Value Modifiers - Aquaflora");
        aquafloraSkillDmg = skillDamage(builder, "Aquaflora", "Petal Pierces", 0.35);
        aquafloraShiftSkillDmg = skillDamage(builder, "Aquaflora", "Blasting Off Together", 7.0);
        aquafloraBloomSkillDmg = skillDamage(builder, "Aquaflora", "Exorbitant Slashing Frenzy", 1.15);
        aquafloraBloomShiftSkillDmg = skillDamage(builder, "Aquaflora", "Flowers Away", 0.0);
        aquafloraSkillCD = skillCooldown(builder, "Aquaflora", "Petal Pierces", 20);
        aquafloraShiftSkillCD = skillCooldown(builder, "Aquaflora", "Blasting Off Together", 20);
        aquafloraBloomSkillCD = skillCooldown(builder, "Aquaflora", "Exorbitant Slashing Frenzy", 200);
        aquafloraBloomShiftSkillCD = skillCooldown(builder, "Aquaflora", "Flowers Away", 40);
        builder.pop();

        builder.push("Value Modifiers - Rainfall Serenity");
        rainfallSerenityArrowDmg = builder.comment("Define how much damage does the Rainfall Serenity's Arrow deal.")
                .defineInRange("Damage: Rainfall Serenity Arrow", 4.0, 0, 1000);
        rainfallSerenityDrawSpeed = builder.comment("Tweak the draw speed of the Rainfall Serenity.")
                .defineInRange("Extra: Rainfall Serenity Draw Speed", 7.5, 0, 1000);
        builder.pop();
    }

    public ForgeConfigSpec.DoubleValue skillDamage(ForgeConfigSpec.Builder builder, String weapon, String skillName, Double dmg) {
        return builder.comment("Define how much damage does the " + StringUtils.capitalize(weapon) + " deal in a specified attack skill.").defineInRange("Damage: " + skillName, dmg, 0, 1000);
    }

    public ForgeConfigSpec.IntValue skillCooldown(ForgeConfigSpec.Builder builder, String weapon, String skillName, int cooldown) {
        return builder.comment("Define the duration of the cooldown provided by the " + StringUtils.capitalize(weapon) + " in a particular attack skill, measured in ticks.").defineInRange("Cooldown: " + skillName, cooldown, 0, 1000);
    }

    public ForgeConfigSpec.IntValue baseDamage(ForgeConfigSpec.Builder builder, String weapon, int dmg) {
        return builder.comment("Define the base attack damage of the " + StringUtils.capitalize(weapon) + ".").defineInRange("Base Damage: " + StringUtils.capitalize(weapon), dmg, 0, 1000);
    }
}
