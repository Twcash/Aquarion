Events.on(ClientLoadEvent, e => {
const manganese = Vars.content.item("tantros-test-manganese");
const godhelpme = Vars.content.item("tantros-test-bauxite");
const duralumin = Vars.content.item("tantros-test-duralumin");
const scrap = Items.scrap
const sand = Items.sand
const coal = Items.coal
const oxide = Items.oxide
const silicon = Items.silicon
const lead = Items.lead
const thorium = Items.thorium
const sodium = Vars.content.item("tantros-test-sodium");
const fissile = Vars.content.item("tantros-test-fissile");
const plutonium = Vars.content.item("tantros-test-plutonium");
const metaglass = Items.metaglass
const tantrosOnlyItems = Seq.with(sand, manganese, godhelpme, oxide,  lead, sodium, coal, scrap, silicon, metaglass, duralumin, plutonium, fissile );

Planets.tantros.hiddenItems.addAll(Vars.content.items()).removeAll(tantrosOnlyItems)
})
