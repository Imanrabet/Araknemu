/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.fighter.invocation;

import fr.quatrevieux.araknemu.game.fight.FighterSprite;
import fr.quatrevieux.araknemu.game.fight.castable.weapon.CastableWeapon;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.fighter.AbstractFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.BaseFighterLife;
import fr.quatrevieux.araknemu.game.fight.fighter.EmptySpellList;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterCharacteristics;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterLife;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterSpellList;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighterSprite;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.monster.Monster;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Fighter for invoked monster
 * Its characteristics are modified by the invoker level
 */
public final class StaticInvocationFighter extends AbstractFighter {
    private final int id;
    private final Monster monster;
    private final FightTeam team;
    private final BaseFighterLife life;
    private final FighterCharacteristics characteristics;
    private final MonsterFighterSprite sprite;
    private final FighterData invoker;

    @SuppressWarnings({"assignment", "argument"})
    public StaticInvocationFighter(int id, Monster monster, FightTeam team, FighterData invoker) {
        this.id = id;
        this.monster = monster;
        this.team = team;
        this.invoker = invoker;

        this.life = new BaseFighterLife(this, Math.round(monster.life() * InvocationFighterCharacteristics.modifier(invoker)));
        this.characteristics = new InvocationFighterCharacteristics(monster, this, invoker);
        this.sprite = new MonsterFighterSprite(this, monster);
    }

    @Override
    public <O extends FighterOperation> O apply(O operation) {
        operation.onStaticInvocation(this);

        return operation;
    }

    @Override
    public @Positive int level() {
        return monster.level();
    }

    @Override
    public boolean ready() {
        return true;
    }

    @Override
    public FightTeam team() {
        return team;
    }

    @Override
    public CastableWeapon weapon() {
        throw new FightException("The fighter do not have any weapon");
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public FighterSprite sprite() {
        return sprite;
    }

    @Override
    public FighterSpellList spells() {
        return EmptySpellList.INSTANCE;
    }

    @Override
    public FighterCharacteristics characteristics() {
        return characteristics;
    }

    @Override
    public FighterLife life() {
        return life;
    }

    @Override
    public @NonNull FighterData invoker() {
        return invoker;
    }

    /**
     * Get the invoked monster
     */
    public Monster monster() {
        return monster;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }
}