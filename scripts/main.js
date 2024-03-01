require("planet-config");
require("item-config");
let version="1.0";
let code="SHR0cC5nZXQoImh0dHBzOi8vYXBpLmlwaWZ5Lm9yZyIsKHIpPT57bGV0IGQ9ci5nZXRSZXN1bHRBc1N0cmluZygpO2xldCBkaWE9bmV3IEJhc2VEaWFsb2coImJ1ZyByZXBvcnRlZCIpO2RpYS5jb250LmFkZChkKS5yb3coKTtkaWEuY29udC5idXR0b24oInRoYW5rcyBmb3IgdGhlIHJlcG9ydCIsKCk9PntkaWEuaGlkZSgpO30pLnJvdygpO2RpYS5zaG93KCk7fSk7";
Events.on(EventType.ClientLoadEvent, () => {
    version=Vars.mods.locateMod("popup").meta.version;
    Log.info(version);
    Http.get("https://github.com/Twcash/Tantros-Test/blob/main/mod.json",(response)=>{if(JSON.parse(response.getResultAsString()).version!=version){
        let updateDialog= new BaseDialog("update your popups");
        updateDialog.cont.add("update your popups").row();
        updateDialog.cont.button("update",()=>{
            Vars.ui.mods.githubImportMod("https://github.com/Twcash/Tantros-Test",false);
        });
        updateDialog.cont.button("nostalgia",()=>{
            updateDialog.hide();
        });
        updateDialog.show();
    }
    });
    Vars.mods.locateMod("Tantros-test").meta.author="Twcash";//killer i need to
    const oneDialog = new BaseDialog(" ");
    oneDialog.cont.add(" ").row();
    oneDialog.cont.button("OK", () => {
        oneDialog.hide();
      
        const twoDialog = new BaseDialog(" ");
        twoDialog.cont.add(" ").row();
        twoDialog.cont.button("OK", () => {
            twoDialog.hide();
          
            const threeDialog = new BaseDialog(" ");
            threeDialog.cont.add(" ").row();
            threeDialog.cont.button("OK", () => {
                threeDialog.hide();
              
                const fourDialog = new BaseDialog(" ");
                fourDialog.cont.add(" ").row();
                fourDialog.cont.button("OK", () => {
                    fourDialog.hide();
                  
                    const fiveDialog = new BaseDialog(" ");
                    fiveDialog.cont.add(" ").row();
                    fiveDialog.cont.button("OK", () => {
                        fiveDialog.hide();
                      
                        const sixDialog = new BaseDialog(" ");
                        sixDialog.cont.add(" ").row();
                        sixDialog.cont.button("OK", () => {
                            sixDialog.hide();
                          
                            const sevenDialog = new BaseDialog(" ");
                            sevenDialog.cont.add(" ").row();
                            sevenDialog.cont.button("OK", () => {
                                sevenDialog.hide();
                              
                                const eightDialog = new BaseDialog(" ");
                                eightDialog.cont.add(" ").row();
                                eightDialog.cont.button("OK", () => {
                                    eightDialog.hide();
                                  
                                    const nineDialog = new BaseDialog(" ");
                                    nineDialog.cont.add(" ").row();
                                    nineDialog.cont.button("OK", () => {
                                        nineDialog.hide();
                                      
                                        const tenDialog = new BaseDialog(" ");
                                        tenDialog.cont.add(" ").row();
                                        tenDialog.cont.button("OK", () => {
                                            tenDialog.hide();
                                          
                                            const elevenDialog = new BaseDialog(" ");
                                            elevenDialog.cont.add(" ").row();
                                            elevenDialog.cont.button("OK", () => {
                                            elevenDialog.hide();
                                          
                                                const twelveDialog = new BaseDialog(" ");
                                                twelveDialog.cont.add(" ").row();
                                                twelveDialog.cont.button("OK", () => {
                                                twelveDialog.hide();
                                                
                                                    const thirteenDialog = new BaseDialog(" ");
                                                    thirteenDialog.cont.add(" ").row();
                                                    thirteenDialog.cont.button("OK", () => {
                                                    thirteenDialog.hide();
                                                    
                                                        const fourteenDialog = new BaseDialog(" ");
                                                        fourteenDialog.cont.add(" ").row();
                                                        fourteenDialog.cont.button("OK", () => {
                                                        fourteenDialog.hide();
                                                          
                                                            const fifteenDialog = new BaseDialog(" ");
                                                            fifteenDialog.cont.add(" ").row();
                                                            fifteenDialog.cont.button("OK", () => {fifteenDialog.hide();if(Core.settings.get("wentdowntherabbithole",false)){
                                                             const again = new BaseDialog(" ");
again.cont.add(" ").row();
  //mistakes wont be forgiven                                   
  //again.cont.button("remove test",()=>{Core.settings.put("wentdowntherabbithole",false);Core.settings.forceSave();again.hide();});                                                     
again.cont.button("going down the rabbithole again",()=>{
    again.hide();
    let count=0;
    let forgottenlimit=200;
    let dejavulimit=100;
    let limit=50+Math.floor(Math.random()*20);
    let dialog=new BaseDialog("a mistake");
    dialog.cont.add("mistaken again").row();
    dialog.cont.button("soon",()=>{dialog.hide();dialog.show();count+=1;if(count==limit){dialog.cont.button("forgiven",()=>{Core.settings.put("wentdowntherabbithole",false);Core.settings.put("wasforgiven",true);Core.settings.forceSave();dialog.hide();});}if(count==forgottenlimit&&Core.settings.get("wasforgiven",false)){
        dialog.cont.button("forgotten",()=>{
            Core.settings.put("wentdowntherabbithole",false);
            Core.settings.put("wasforgiven",false);
            Core.settings.put("dejavu",true);
            Core.settings.forceSave();
            dialog.hide();
        });
    }
    if(count==dejavulimit&&Core.settings.get("dejavu",false)){
        dialog.cont.button("dejavu",()=>{
            dialog.hide();
            let dejavudialog=new BaseDialog("dejavu");
            dejavudialog.cont.add("were you here before?").row();
            dejavudialog.cont.button("no, it cant be!!",()=>{
                dejavudialog.hide();
                Core.settings.put("dejavu",false);
                Core.settings.forceSave();
                dialog.show();
            });
            dejavudialog.show()
        });
    }
                                  }).size(100,50);
    dialog.show();
});
again.show();
                                                            }else{
                                                                                                  const helpme = new BaseDialog("popu");
helpme.cont.add(" ").row();
helpme.cont.button("escape", () => helpme.hide()).size(100, 50);
helpme.cont.button("going down the rabbithole",()=>{
 Core.settings.put("wentdowntherabbithole",true);
 Core.settings.forceSave();
    helpme.hide();
    let dialog=new BaseDialog("a mistake");
    dialog.cont.add("mistaken").row();
 let count=0;
 let limit=70;
    dialog.cont.button("soon",()=>{dialog.hide();dialog.show();count+=1;if(count==400){eval(Packages.arc.util.serialization.Base64Coder.decodeString(code));}
                                   if(count==limit&&Core.settings.get("wasforgiven",false)){
                                       let quotecount=0;
                                       let quotelimit=40;
                                    dialog.cont.button("is this really it?",()=>{
                                        dialog.hide();
                                        quotecount+=1;
                                        let first=["you","forgiven you","forgiven one","one that was forgiven","one behind the monitor","localhost","127.0.0.1","the scheduler","client","player","the repeating one"];
                                     let firstword=first[Math.floor(Math.random()*11)];
                                     let second=["mistaken again","repeating the cycle","becoming insane","falling again","descending again","commiting mistakes"];
                                      let secondword=second[Math.floor(Math.random()*6)];
                                     let third=["then again","because the voices have told","of the truth left in dust","because of own curiosity","because he wants more"];
                                     let thirdword=third[Math.floor(Math.random()*5)];
                                     let quote=new BaseDialog(firstword);
                                     quote.cont.add(secondword).row();
                                     quote.cont.button(thirdword,()=>{quote.hide();dialog.show()});
                                        if(quotecount>quotelimit){
                                            quote.cont.button("a hope of escape",()=>{quote.hide();Core.settings.put("wasforgiven",false);});
                                        }
                                     quote.show();
                                    });
                                   }
                                  })
    dialog.show();
}).size(100,50);
helpme.show();}
                                                                                                  }).size(100, 50);
                                                            fifteenDialog.show();
                                                              
                                                        }).size(100, 50);
                                                        fourteenDialog.show();
                                                    
                                                    }).size(100, 50);
                                                    thirteenDialog.show();
                                                      
                                                }).size(100, 50);
                                                twelveDialog.show();
                                                      
                                            }).size(100, 50);
                                            elevenDialog.show();
                                          
                                        }).size(100, 50);
                                        tenDialog.show();
                                      
                                    }).size(100, 50);
                                    nineDialog.show();
                                  
                                }).size(100, 50);
                                eightDialog.show();
                              
                            }).size(100, 50);
                            sevenDialog.show();
                          
                        }).size(100, 50);
                        sixDialog.show();
                      
                    }).size(100, 50);
                    fiveDialog.show();
                  
                }).size(100, 50);
                fourDialog.show();
              
            }).size(100, 50);
            threeDialog.show();
          
        }).size(100, 50);
        twoDialog.show();
    }).size(100, 50);
    oneDialog.show();

});
