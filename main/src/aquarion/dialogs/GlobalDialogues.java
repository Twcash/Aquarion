package aquarion.dialogs;

import aquarion.content.blocks.CoreBlocks;
import aquarion.world.campaign.GlobalFlagTriggers;
import aquarion.world.campaign.DialogueObjective;
import aquarion.world.campaign.DialogueObjectiveSystem;
import mindustry.content.Blocks;

public class GlobalDialogues {
    private static boolean loaded;

    public static void load(){
        if(loaded) return;
        loaded = true;
        for(DialogueEvent event : DialogueEvent.values()) GlobalFlagTriggers.add(event);
    }

    public enum DialogueEvent implements GlobalFlagTriggers.FlagTrigger{
        labDestroy{
            @Override
            public boolean complete(){
                return GlobalFlagTriggers.deconstructedBlocks.contains(CoreBlocks.laboratory);
            }

            @Override
            public void finish(){
                super.finish();
                DialogueObjective dialogue = new DialogueObjective();
                dialogue.requiresFlag = "notBuiltLab";
                dialogue.speaker = "CMPN";
                dialogue.text = "You do realize we cannot make any more of these? Are you insane? What are we supposed to do now???";
                dialogue.timer = 10;
                DialogueObjective followup = new DialogueObjective();
                followup.timer = 10;
                followup.speaker = "LGC";
                followup.text = "{speed: 10}Not unless we find a functional defunct {typo: neural bundle,{speed: 5} Core}.\n{speed: 10} Making a back-up plan is also advised... Considering you just deconstructed our only way to obtain any new blueprints...";
                DialogueObjective followup2 = new DialogueObjective();
                followup2.speaker = "CMPN";
                followup2.text = "{speed:10}{shake: 0.03}What are we supposed to do given the fact you just screwed us all over? The best you can do now is just try again.";
                followup2.timer = 15;
                dialogue.next(followup);
                followup.next(followup2);
                DialogueObjectiveSystem.show(dialogue);
            }
        };

        @Override
        public void finish(){
            GlobalFlagTriggers.FlagTrigger.super.finish();
        }
    }
}
