Events.on(ContentInitEvent, (event) => {
const tantGen = extend(PlanetGenerator, {
    // Noise scale factor
    scl: 6,
    
    // Water level offset
    waterOffset: 0.07,
    
    // Seed for noise function
    seed: 8642,

    // Define blocks array
    arr: [
        [Blocks.stone, Blocks.sand, Blocks.darksand, Blocks.grass, Blocks.water],
        [Blocks.stone, Blocks.sand, Blocks.darksand, Blocks.grass, Blocks.water],
        [Blocks.stone, Blocks.sand, Blocks.darksand, Blocks.grass, Blocks.water],
        [Blocks.stone, Blocks.sand, Blocks.darksand, Blocks.grass, Blocks.water]
    ],

    // Custom noise function to generate raw temperature
    rawTemp(position) {
        return position.dst(0, 0, 1) * 2.2 - Simplex.noise3d(this.seed, 8, 0.54, 1.4, 10 + position.x, 10 + position.y, 10 + position.z) * 2.9;
    },

    // Generate raw height using custom noise function
    rawHeight(position) {
        position = Tmp.v33.set(position).scl(this.scl);
        return (Mathf.pow(this.rawTemp(position), 2.3) + this.waterOffset) / (1 + this.waterOffset);
    },

    // Get height of the terrain at a given position
    getHeight(position) {
        let height = this.rawHeight(position);
        return Math.max(height, this.water);
    },

    // Get color of the terrain at a given position
    getColor(position) {
        let block = this.getBlock(position);
        if (block == null) return Blocks.stone.mapColor;
        Tmp.c1.set(block.mapColor).a = 1 - block.albedo;
        return Tmp.c1;
    },

    // Generate tile at a given position
    genTile(position, tile) {
        tile.floor = this.getBlock(position);
        tile.block = Blocks.air; // No walls
    },

    // Get block type at a given position
    getBlock(position) {
        let height = this.rawHeight(position);
        return this.arr[Mathf.clamp(Math.floor(height * this.arr.length), 0, this.arr[0].length - 1)][Mathf.clamp(Math.floor(height * this.arr[0].length), 0, this.arr[0].length - 1)];
    },

    // Generate terrain for the sector
    generate(tiles, sec) {
        this.tiles = tiles;
        this.sector = sec;
        const rand = this.rand;
        rand.setSeed(sec.id);

        // Generate each tile
        let gen = new TileGen();
        this.tiles.each((x, y) => {
            gen.reset();
            let position = this.sector.rect.project(x / tiles.width, y / tiles.height);
            this.genTile(position, gen);
            tiles.set(x, y, new Tile(x, y, gen.floor, gen.overlay, gen.block));
        });

        Schematics.placeLaunchLoadout(tiles.width / 3, tiles.height / 3);
    }
});
Vars.content.planet("aquarion-tantros").generator = tantGen;
});