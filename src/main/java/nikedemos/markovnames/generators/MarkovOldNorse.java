/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.text.Text
 */
package nikedemos.markovnames.generators;

import net.minecraft.text.Text;
import nikedemos.markovnames.MarkovDictionary;
import nikedemos.markovnames.generators.MarkovGenerator;

public class MarkovOldNorse
extends MarkovGenerator {
    public MarkovDictionary markov2;

    public MarkovOldNorse(int seqlen) {
        this.markov = new MarkovDictionary("old_norse_bothgenders.txt", seqlen);
        this.name = Text.translatable((String)"markov.oldNorse").toString();
    }

    public MarkovOldNorse() {
        this(4);
    }

    @Override
    public String fetch(int gender) {
        return this.markov.generateWord();
    }
}

