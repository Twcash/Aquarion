Events.on(ClientLoadEvent, (event) => {
    Log.info("Aquarion - Sending version check");
    var req = new Http.get(
        "https://raw.githubusercontent.com/Twcash/Tantros-Test/main/mod.hjson",
        (res) => {
            var resp = res.getResultAsString();
            Log.info("Aquaron - response: \n" + resp);

            var json = Jval.read(resp);
            Log.info("Aquarion - remote ver: " + json.get("version"));

            var vers = Vars.mods.getMod("aquarion").meta.version;
            Log.info("Aquarion - local version: " + vers);

            if (!vers.equals(json.get("version"))) {
                Log.warn("Aquarion - not up to date");
                try {
                    Vars.ui.showMenu(
                        "[#22CCFF]Aquarion[white]",
                        Core.bundle.get("scripts.update-aquaria"),
                        [["[gray]Ok"], ["[green]Reinstall"]],
                        (option) => {
                            if (option == 1) {
                                Vars.ui.mods.githubImportMod(
                                    Vars.mods.locateMod("aquarion").getRepo(),
                                    Vars.mods.locateMod("aquarion").isJava()
                                );

                                var shown = false;

                                Timer.schedule(
                                    () => {
                                        if (
                                            Vars.mods.requiresReload() &&
                                            !shown
                                        ) {
                                            shown = true;
                                            Vars.ui.showInfoOnHidden(
                                                "@mods.reloadexit",
                                                () => {
                                                    Core.app.exit();
                                                }
                                            );
                                        }
                                    },
                                    2,
                                    1
                                );
                            }
                        }
                    );
                } catch (err) {
                    Log.info("Error: " + err.toString());
                }
            } else {
                Log.info("Aquarion - up to date");
            }
        },
        (err) => {
            Vars.ui.showOkText(
                "[#22CCFF]Aquarion[white]",
                "[red]ERROR:[white] Cannot check for updates!",
                () => {}
            );
            Log.err("Aquarion - update check failed :(");
        }
    );
});
