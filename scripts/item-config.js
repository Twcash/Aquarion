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
const sodium = Vars.content.item("tantros-test-sodium");
const metaglass = Items.metaglass
const tantrosOnlyItems = Seq.with(sand, manganese,  godhelpme, oxide,  lead, sodium, coal, scrap, silicon, metaglass, duralumin);

Planets.tantros.hiddenItems.addAll(Vars.content.items()).removeAll(tantrosOnlyItems)
})