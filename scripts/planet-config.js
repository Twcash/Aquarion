Events.on(ClientLoadEvent, e => {
Planets.tantros.accessible = Planets.tantros.alwaysUnlocked = Planets.tantros.visible = true
Planets.tantros.allowLaunchToNumbered = false
Planets.tantros.allowLaunchLoadout = false
Planets.tantros.defaultCore = Vars.content.block("tantros-test-core-pike")
Planets.tantros.generator.defaultLoadout = Schematics.read(Vars.tree.get("schematics/core-pike.msch"));
});


