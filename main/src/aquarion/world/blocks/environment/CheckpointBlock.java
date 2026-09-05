package aquarion.world.blocks.environment;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Strings;
import mindustry.editor.EditorTile;
import mindustry.gen.Unit;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.OverlayFloor;

import static mindustry.Vars.world;

public class CheckpointBlock extends OverlayFloor {
    public static final Seq<Tile> checkpoints = new Seq<>();
    //So I don't crash. Could have problably used the original glyph system but eugh
    protected static final byte[][] digitGlyphs = {
        {0b111, 0b101, 0b101, 0b101, 0b111},
        {0b010, 0b110, 0b010, 0b010, 0b111},
        {0b111, 0b001, 0b111, 0b100, 0b111},
        {0b111, 0b001, 0b111, 0b001, 0b111},
        {0b101, 0b101, 0b111, 0b001, 0b001},
        {0b111, 0b100, 0b111, 0b001, 0b111},
        {0b111, 0b100, 0b111, 0b101, 0b111},
        {0b111, 0b001, 0b010, 0b010, 0b010},
        {0b111, 0b101, 0b111, 0b101, 0b111},
        {0b111, 0b101, 0b111, 0b001, 0b111},
    };

    public CheckpointBlock(String name) {
        super(name);
        variants = 0;
        needsSurface = false;
        saveData = true;
        saveConfig = true;
        editorConfigurable = true;
        instantBuild = true;
        placeableLiquid = true;
        obstructsLight = false;
        ignoreBuildDarkness = true;
    }

    public static void rebuildCheckpoints() {
        checkpoints.clear();
        if (world == null || world.tiles == null) return;
        for (Tile tile : world.tiles) {
            if (tile.overlay() instanceof CheckpointBlock) {
                checkpoints.add(tile);
            }
        }
        checkpoints.sort(t -> t.extraData);
    }

    @Override
    public void drawBase(Tile tile) {
        if (!(tile instanceof EditorTile)) return;
        Draw.rect(region, tile.worldx(), tile.worldy());
        drawNumber(tile);
    }

    protected void drawNumber(Tile tile) {
        String text = Integer.toString(tile.extraData);
        int digits = text.length();
        float pix = Math.min(0.9f, 6f / (digits * 4f - 1f));
        float charWidth = 3f * pix, gap = pix;
        float totalWidth = digits * charWidth + (digits - 1) * gap;
        float x0 = tile.worldx() - totalWidth / 2f;
        float yTop = tile.worldy() + 2.5f * pix;

        Draw.color(Color.valueOf("24252d"));
        for (int i = 0; i < digits; i++) {
            byte[] glyph = digitGlyphs[text.charAt(i) - '0'];
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 3; col++) {
                    if ((glyph[row] & (1 << (2 - col))) != 0) {
                        Fill.rect(x0 + i * (charWidth + gap) + col * pix + pix / 2f, yTop - row * pix - pix / 2f, pix, pix);
                    }
                }
            }
        }
        Draw.color();
    }

    @Override
    public Object getConfig(Tile tile) {
        return tile.extraData;
    }

    @Override
    public void placeEnded(Tile tile, @Nullable Unit builder, int rotation, @Nullable Object config) {
        if (config instanceof Integer i) {
            tile.extraData = i;
            lastConfig = i + 1;
        }
    }

    @Override
    public void editorPicked(Tile tile) {
        lastConfig = tile.extraData;
    }

    @Override
    public void buildEditorConfig(Table table) {
        int value = lastConfig instanceof Integer i ? i : 0;
        TextField field = table.field(Integer.toString(value), val -> {
            if (Strings.canParseInt(val)) {
                lastConfig = Strings.parseInt(val);
            }
        }).valid(val -> Strings.canParseInt(val) && Strings.parseInt(val) >= 0).maxTextLength(6).width(120f).get();
        field.update(() -> {
            String current = Integer.toString(lastConfig instanceof Integer i ? i : 0);
            if (!field.hasKeyboard() && !field.getText().toString().equals(current)) {
                field.setText(current);
            }
        });
    }
}
