package fr.quatrevieux.araknemu.game.fight.castable.effect.buff;

import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.AddBuff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class BuffListTest extends FightBaseCase {
    private BuffList list;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        createFight();

        list = new BuffList(player.fighter());
        requestStack.clear();
    }

    @Test
    void add() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        BuffHook hook = Mockito.mock(BuffHook.class);

        Buff buff = new Buff(effect, Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook);

        list.add(buff);

        requestStack.assertLast(new AddBuff(buff));
        Mockito.verify(hook).onBuffStarted(buff);
        assertArrayEquals(new Buff[] {buff}, list.stream().toArray());
    }

    @Test
    void addWithDurationZero() {
        // #61 Buff removed on end turn
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        BuffHook hook = Mockito.mock(BuffHook.class);

        Mockito.when(effect.duration()).thenReturn(0);

        Buff buff = new Buff(effect, Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook);

        list.add(buff);

        assertEquals(1, buff.remainingTurns());
    }

    @Test
    void addMultiple() {
        Buff buff1 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), Mockito.mock(BuffHook.class));
        Buff buff2 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), Mockito.mock(BuffHook.class));
        Buff buff3 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), Mockito.mock(BuffHook.class));

        list.add(buff1);
        list.add(buff2);
        list.add(buff3);

        assertArrayEquals(new Buff[] {buff1, buff2, buff3}, list.stream().toArray());
    }

    @Test
    void addSelfBuffWillIncrementTurns() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        BuffHook hook = Mockito.mock(BuffHook.class);

        Mockito.when(effect.duration()).thenReturn(1);

        Buff buff = new Buff(effect, Mockito.mock(Spell.class), player.fighter(), player.fighter(), hook);

        list.add(buff);

        assertEquals(2, buff.remainingTurns());
    }

    @Test
    void onStartTurnWithoutBuff() {
        assertTrue(list.onStartTurn());
    }

    @Test
    void onStartTurnWithOneBuffWillReturnTheBuffHookResponse() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        BuffHook hook = Mockito.mock(BuffHook.class);
        Buff buff = new Buff(effect, Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook);

        Mockito.when(hook.onStartTurn(buff)).thenReturn(false);

        list.add(buff);

        assertFalse(list.onStartTurn());
        Mockito.verify(hook).onStartTurn(buff);
    }

    @Test
    void onStartTurnWithMultipleBuff() {
        BuffHook hook1, hook2, hook3;

        Buff buff1 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook1 = Mockito.mock(BuffHook.class));
        Buff buff2 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook2 = Mockito.mock(BuffHook.class));
        Buff buff3 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook3 = Mockito.mock(BuffHook.class));

        Mockito.when(hook1.onStartTurn(buff1)).thenReturn(true);
        Mockito.when(hook2.onStartTurn(buff2)).thenReturn(false);
        Mockito.when(hook3.onStartTurn(buff3)).thenReturn(true);

        list.add(buff1);
        list.add(buff2);
        list.add(buff3);

        assertFalse(list.onStartTurn());

        Mockito.verify(hook1).onStartTurn(buff1);
        Mockito.verify(hook2).onStartTurn(buff2);
        Mockito.verify(hook3).onStartTurn(buff3);
    }

    @Test
    void onEndTurn() {
        BuffHook hook1, hook2, hook3;

        Buff buff1 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook1 = Mockito.mock(BuffHook.class));
        Buff buff2 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook2 = Mockito.mock(BuffHook.class));
        Buff buff3 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook3 = Mockito.mock(BuffHook.class));

        list.add(buff1);
        list.add(buff2);
        list.add(buff3);

        list.onEndTurn();

        Mockito.verify(hook1).onEndTurn(buff1);
        Mockito.verify(hook2).onEndTurn(buff2);
        Mockito.verify(hook3).onEndTurn(buff3);
    }

    @Test
    void onCastTarget() {
        BuffHook hook1, hook2, hook3;

        Buff buff1 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook1 = Mockito.mock(BuffHook.class));
        Buff buff2 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook2 = Mockito.mock(BuffHook.class));
        Buff buff3 = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook3 = Mockito.mock(BuffHook.class));

        list.add(buff1);
        list.add(buff2);
        list.add(buff3);

        CastScope cast = new CastScope(Mockito.mock(Spell.class), player.fighter(), null);

        list.onCastTarget(cast);

        Mockito.verify(hook1).onCastTarget(buff1, cast);
        Mockito.verify(hook2).onCastTarget(buff2, cast);
        Mockito.verify(hook3).onCastTarget(buff3, cast);
    }

    @Test
    void refreshWillDecrementRemaingTurnsAndRemoveExpiredBuffs() {
        BuffHook hook1, hook2, hook3;

        SpellEffect effect1 = Mockito.mock(SpellEffect.class);
        SpellEffect effect2 = Mockito.mock(SpellEffect.class);
        SpellEffect effect3 = Mockito.mock(SpellEffect.class);

        Mockito.when(effect1.duration()).thenReturn(1);
        Mockito.when(effect2.duration()).thenReturn(2);
        Mockito.when(effect3.duration()).thenReturn(3);

        Buff buff1 = new Buff(effect1, Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook1 = Mockito.mock(BuffHook.class));
        Buff buff2 = new Buff(effect2, Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook2 = Mockito.mock(BuffHook.class));
        Buff buff3 = new Buff(effect3, Mockito.mock(Spell.class), other.fighter(), player.fighter(), hook3 = Mockito.mock(BuffHook.class));

        list.add(buff1);
        list.add(buff2);
        list.add(buff3);

        list.refresh();

        assertArrayEquals(new Buff[] {buff2, buff3}, list.stream().toArray());

        Mockito.verify(hook1).onBuffTerminated(buff1);
    }
}
