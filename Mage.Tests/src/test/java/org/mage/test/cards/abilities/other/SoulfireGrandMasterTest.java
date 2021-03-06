/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */

package org.mage.test.cards.abilities.other;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Ignore;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 *
 * @author BetaSteward
 */
public class SoulfireGrandMasterTest extends CardTestPlayerBase {

    /** 
     * Soulfire Grand Master
     * Creature â€” Human Monk 2/2, 1W (2)
     * Lifelink
     * Instant and sorcery spells you control have lifelink.
     * {2}{U/R}{U/R}: The next time you cast an instant or sorcery spell from 
     * your hand this turn, put that card into your hand instead of into your 
     * graveyard as it resolves.
     *
     */
       
    @Test
    public void testSpellsGainLifelink() {
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 1);
        addCard(Zone.BATTLEFIELD, playerA, "Soulfire Grand Master");
        addCard(Zone.HAND, playerA, "Lightning Bolt");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", playerB);
        
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertGraveyardCount(playerA, "Lightning Bolt", 1);
        assertHandCount(playerA, "Lightning Bolt", 0);
        assertLife(playerB, 17);
        assertLife(playerA, 23);

    }

    @Test
    public void testSpellsReturnToHand() {
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 5);
        addCard(Zone.BATTLEFIELD, playerA, "Soulfire Grand Master");
        addCard(Zone.HAND, playerA, "Lightning Bolt");

        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "{2}{U/R}{U/R}:");
        
        castSpell(1, PhaseStep.POSTCOMBAT_MAIN, playerA, "Lightning Bolt", playerB);
        
        setStopAt(1, PhaseStep.END_TURN);
        execute();

        assertGraveyardCount(playerA, "Lightning Bolt", 0);
        assertHandCount(playerA, "Lightning Bolt", 1);
        assertLife(playerA, 23);
        assertLife(playerB, 17);

    }
    /**
     * Test with Searing Blood
     * If the delayed triggered ability triggers, it has to give
     * life from lifelink because the source is still Searing Blood
     */
    // @Ignore // Does not work because as the delayed triggered ability resolves, the source card is no longer on the stack and
    @Test
    public void testSearingBlood1() {
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 2);
        // Searing Blood {R}{R}
        // Searing Blood deals 2 damage to target creature. When that creature dies this turn, Searing Blood deals 3 damage to that creature's controller.
        addCard(Zone.HAND, playerA, "Searing Blood");
        addCard(Zone.BATTLEFIELD, playerA, "Soulfire Grand Master", 1);
        addCard(Zone.BATTLEFIELD, playerB, "Silvercoat Lion", 1);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Searing Blood", "Silvercoat Lion");

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Soulfire Grand Master", 1);
        assertGraveyardCount(playerA, "Searing Blood", 1);
        assertGraveyardCount(playerB, "Silvercoat Lion", 1);

        assertLife(playerB, 17); // -3 by Searing blood because Silvercoat Lion dies
        assertLife(playerA, 25); // +2 from damage to Silvercoat Lion + 3 from damage to Player B

    }

    /**
     * Test with Searing Blood
     * If the delayed triggered ability triggers, it has to give
     * life from lifelink because the source is still Searing Blood
     */
    @Test
    public void testSearinBlood2() {
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 3);
        // Searing Blood {R}{R}
        // Searing Blood deals 2 damage to target creature. When that creature dies this turn, Searing Blood deals 3 damage to that creature's controller.
        addCard(Zone.HAND, playerA, "Searing Blood");
        addCard(Zone.HAND, playerA, "Lightning Bolt");
        addCard(Zone.BATTLEFIELD, playerA, "Soulfire Grand Master", 1);
        addCard(Zone.BATTLEFIELD, playerB, "Pillarfield Ox", 1);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Searing Blood", "Pillarfield Ox");

        castSpell(1, PhaseStep.POSTCOMBAT_MAIN, playerA, "Lightning Bolt", "Pillarfield Ox");

        setStopAt(1, PhaseStep.END_TURN);
        execute();

        assertPermanentCount(playerA, "Soulfire Grand Master", 1);
        assertGraveyardCount(playerA, "Searing Blood", 1);
        assertGraveyardCount(playerA, "Lightning Bolt", 1);
        assertGraveyardCount(playerB, "Pillarfield Ox", 1);

        assertLife(playerB, 17);
        assertLife(playerA, 28); // +2 from damage to Silvercoat Lion + 3 from Lighning Bolt + 3 from damage to Player B from Searing Blood

    }    
    
    /**
     * Test copied instant spell gives also life
     * 
     */    
    @Test
    public void testCopySpell() {
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 4);
        addCard(Zone.BATTLEFIELD, playerA, "Island", 1);
        addCard(Zone.HAND, playerA, "Lightning Bolt");
        addCard(Zone.BATTLEFIELD, playerA, "Soulfire Grand Master", 1);
        // {2}{U}{R}: Copy target instant or sorcery spell you control. You may choose new targets for the copy.
        addCard(Zone.BATTLEFIELD, playerA, "Nivix Guildmage", 1);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", playerB);
        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "{2}{U}{R}:");

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Soulfire Grand Master", 1);
        assertPermanentCount(playerA, "Nivix Guildmage", 1);
        assertGraveyardCount(playerA, "Lightning Bolt", 1);

        assertLife(playerB, 14);
        assertLife(playerA, 26); 

    }     
    
    
    /**
     * Test damage of activated ability of a permanent does not gain lifelink
     * 
     */
    
    @Test
    public void testActivatedAbility() {
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 3);
        
        addCard(Zone.HAND, playerA, "Lightning Bolt");
        addCard(Zone.BATTLEFIELD, playerA, "Soulfire Grand Master", 1);
        // {3}, {T}: Rod of Ruin deals 1 damage to target creature or player.
        addCard(Zone.BATTLEFIELD, playerA, "Rod of Ruin", 1);

        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "{3},{T}");

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Soulfire Grand Master", 1);
        assertPermanentCount(playerA, "Rod of Ruin", 1);

        assertLife(playerB, 19);
        assertLife(playerA, 20); 

    }      
    /**
     * Test that if Soulfire Grand Master has left the battlefield 
     * spell have no longer lifelink 
     */
    
    @Test
    public void testSoulfireLeft() {
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 1);
        
        addCard(Zone.HAND, playerA, "Lightning Bolt");
        addCard(Zone.BATTLEFIELD, playerA, "Soulfire Grand Master", 1);
        
        addCard(Zone.HAND, playerB, "Lightning Bolt", 1);
        addCard(Zone.BATTLEFIELD, playerB, "Mountain", 1);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", playerB);
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerB, "Lightning Bolt", "Soulfire Grand Master");

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertGraveyardCount(playerA, "Lightning Bolt", 1);
        assertGraveyardCount(playerB, "Lightning Bolt", 1);
        assertGraveyardCount(playerA, "Soulfire Grand Master", 1);

        assertLife(playerB, 17);
        assertLife(playerA, 20); 

    }      
    
}
