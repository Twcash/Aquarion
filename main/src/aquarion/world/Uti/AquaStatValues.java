package aquarion.world.Uti;

import aquarion.world.entities.bullet.GambleBulletType;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Collapser;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.Scaling;
import arc.util.Strings;
import mindustry.content.StatusEffects;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValue;

import static mindustry.Vars.tilesize;
import static mindustry.world.meta.StatValues.fixValue;
import static mindustry.world.meta.StatValues.withTooltip;
public class AquaStatValues {
    public static <T extends UnlockableContent> StatValue ammo(ObjectMap<T, BulletType> map){
        return ammo(map, false, false);
    }

    public static <T extends UnlockableContent> StatValue ammo(ObjectMap<T, BulletType> map, boolean showUnit){
        return ammo(map, false, showUnit);
    }
    public static <T extends UnlockableContent> StatValue ammo(ObjectMap<T, BulletType> map, boolean nested, boolean showUnit){
        return table -> {

            table.row();

            var orderedKeys = map.keys().toSeq();
            orderedKeys.sort();

            for(T t : orderedKeys){
                boolean compact = t instanceof UnitType && !showUnit || nested;

                BulletType type = map.get(t);

                if(type.spawnUnit != null && type.spawnUnit.weapons.size > 0){
                    ammo(ObjectMap.of(t, type.spawnUnit.weapons.first().bullet), nested, false).display(table);
                    continue;
                }

                table.table(Styles.grayPanel, bt -> {
                    bt.left().top().defaults().padRight(3).left();
                    //no point in displaying unit icon twice
                    if(!compact && !(t instanceof Turret)){
                        bt.table(title -> {
                            title.image(icon(t)).size(3 * 8).padRight(4).right().scaling(Scaling.fit).top().with(i -> withTooltip(i, t, false));

                            title.add(t.localizedName).padRight(10).left().top();

                            if(type.displayAmmoMultiplier && type.statLiquidConsumed > 0f){
                                title.add("[stat]" + fixValue(type.statLiquidConsumed / type.ammoMultiplier * 60f) + " [lightgray]" + StatUnit.perSecond.localized());
                            }
                        });
                        bt.row();
                    }
                    if (type.statLiquidConsumed <= 0f && !compact && !Mathf.equal(type.ammoMultiplier, 1f) && type.displayAmmoMultiplier && (!(t instanceof Turret turret) || turret.displayAmmoMultiplier)) {
                        sep(bt, Core.bundle.format("bullet.multiplier", (int) type.ammoMultiplier));
                    }

                    if (!compact && !Mathf.equal(type.reloadMultiplier, 1f)) {
                        int val = (int) (type.reloadMultiplier * 100 - 100);
                        sep(bt, Core.bundle.format("bullet.reload", ammoStat(val)));
                    }
                    if (type.rangeChange != 0 && !compact) {
                        sep(bt, Core.bundle.format("bullet.range", ammoStat(type.rangeChange / tilesize)));
                    }
                    if(type instanceof GambleBulletType gamble){
                        bt.row();

                        Table gc = new Table();
                        for(int i = 0; i < gamble.bullets.length; i++){
                            BulletType gb = gamble.bullets[i];
                            float bias = gamble.bias.length > i ? gamble.bias[i] : 1f;
                            float total = 0f;
                            for(float b : gamble.bias) total += b;
                            float chance = (bias / total) * 100f;

                            gc.table(Styles.grayPanel, gbt -> {
                                gbt.left().top().defaults().padRight(3).left();

                                gbt.table(title -> {
                                    title.add(Core.bundle.format("bullet.random",
                                            Strings.autoFixed(chance, 1)+"%"));
                                });
                                gbt.row();

                                Table sub = new Table();
                                ammo(ObjectMap.of(t, gb), true, false).display(sub);
                                Collapser coll = new Collapser(sub, false);
                                coll.setDuration(0.1f);

                                gbt.table(ct -> {
                                    ct.left().defaults().left();
                                });
                                gbt.row();
                                gbt.add(coll);
                            }).padTop(3).growX().margin(5);
                            gc.row();
                        }

                        Collapser gambleColl = new Collapser(gc, true);
                        gambleColl.setDuration(0.15f);

                        bt.table(gt -> {
                            gt.left().defaults().left();
                            gt.add(Core.bundle.get("bullet.gamblelist", "Possible Bullets")).color(Pal.accent.cpy());
                            gt.button(Icon.downOpen, Styles.emptyi, () -> gambleColl.toggle(false))
                                    .update(i -> i.getStyle().imageUp = (!gambleColl.isCollapsed() ? Icon.upOpen : Icon.downOpen))
                                    .size(8).padLeft(16f).expandX();
                        });
                        bt.row();
                        bt.add(gambleColl);
                    } else {
                        if (type.damage > 0 && (type.collides || type.splashDamage <= 0)) {
                            if (type.continuousDamage() > 0) {
                                bt.add(Core.bundle.format("bullet.damage", type.continuousDamage()) + StatUnit.perSecond.localized());
                            } else {
                                bt.add(Core.bundle.format("bullet.damage", type.damage));
                            }
                        }

                        if (type.buildingDamageMultiplier != 1) {
                            sep(bt, Core.bundle.format("bullet.buildingdamage", ammoStat((int) (type.buildingDamageMultiplier * 100 - 100))));
                        }



                        if (type.shieldDamageMultiplier != 1) {
                            sep(bt, Core.bundle.format("bullet.shielddamage", ammoStat((int) (type.shieldDamageMultiplier * 100 - 100))));
                        }

                        if (type.splashDamage > 0) {
                            sep(bt, Core.bundle.format("bullet.splashdamage", (int) type.splashDamage, Strings.fixed(type.splashDamageRadius / tilesize, 1)));
                        }



                        if (type.knockback > 0) {
                            sep(bt, Core.bundle.format("bullet.knockback", Strings.autoFixed(type.knockback, 2)));
                        }

                        if (type.healPercent > 0f) {
                            sep(bt, Core.bundle.format("bullet.healpercent", Strings.autoFixed(type.healPercent, 2)));
                        }

                        if (type.healAmount > 0f) {
                            sep(bt, Core.bundle.format("bullet.healamount", Strings.autoFixed(type.healAmount, 2)));
                        }

                        if (type.pierce || type.pierceCap != -1) {
                            sep(bt, type.pierceCap == -1 ? "@bullet.infinitepierce" : Core.bundle.format("bullet.pierce", type.pierceCap));
                        }

                        if (type.incendAmount > 0) {
                            sep(bt, "@bullet.incendiary");
                        }

                        if (type.homingPower > 0.01f) {
                            sep(bt, "@bullet.homing");
                        }

                        if (type.lightning > 0) {
                            sep(bt, Core.bundle.format("bullet.lightning", type.lightning, type.lightningDamage < 0 ? type.damage : type.lightningDamage));
                        }

                        if (type.pierceArmor) {
                            sep(bt, "@bullet.armorpierce");
                        }

                        if (type.maxDamageFraction > 0) {
                            sep(bt, Core.bundle.format("bullet.maxdamagefraction", (int) (type.maxDamageFraction * 100)));
                        }

                        if (type.suppressionRange > 0) {
                            sep(bt, Core.bundle.format("bullet.suppression", Strings.autoFixed(type.suppressionDuration / 60f, 2), Strings.fixed(type.suppressionRange / tilesize, 1)));
                        }

                        if (type.status != StatusEffects.none) {
                            sep(bt, (type.status.hasEmoji() ? type.status.emoji() : "") + "[stat]" + type.status.localizedName + (type.status.reactive ? "" : "[lightgray] ~ [stat]" +
                                    Strings.autoFixed(type.statusDuration / 60f, 1) + "[lightgray] " + Core.bundle.get("unit.seconds"))).with(c -> withTooltip(c, type.status));
                        }

                        if (!type.targetMissiles) {
                            sep(bt, "@bullet.notargetsmissiles");
                        }

                        if (!type.targetBlocks) {
                            sep(bt, "@bullet.notargetsbuildings");
                        }

                        if (type.intervalBullet != null) {
                            bt.row();

                            Table ic = new Table();
                            ammo(ObjectMap.of(t, type.intervalBullet), true, false).display(ic);
                            Collapser coll = new Collapser(ic, true);
                            coll.setDuration(0.1f);

                            bt.table(it -> {
                                it.left().defaults().left();

                                it.add(Core.bundle.format("bullet.interval", Strings.autoFixed(type.intervalBullets / type.bulletInterval * 60, 2)));
                                it.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
                            });
                            bt.row();
                            bt.add(coll);
                        }

                        if (type.fragBullet != null) {
                            bt.row();

                            Table fc = new Table();
                            ammo(ObjectMap.of(t, type.fragBullet), true, false).display(fc);
                            Collapser coll = new Collapser(fc, true);
                            coll.setDuration(0.1f);

                            bt.table(ft -> {
                                ft.left().defaults().left();

                                ft.add(Core.bundle.format("bullet.frags", type.fragBullets));
                                ft.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
                            });
                            bt.row();
                            bt.add(coll);
                        }
                        // Show gamble bullet info (biased random bullet list)

                    }
                }).padLeft(5).padTop(5).padBottom(compact ? 0 : 5).growX().margin(compact ? 0 : 10);
                table.row();
            }
        };
    }
    //Simply copied vanilla code
    private static TextureRegion icon(UnlockableContent t){
        return t.uiIcon;
    }
    private static Cell<?> sep(Table table, String text){
        table.row();
        return table.add(text);
    }

    //for AmmoListValue
    private static String ammoStat(float val){
        return (val > 0 ? "[stat]+" : "[negstat]") + Strings.autoFixed(val, 1);
    }
}
