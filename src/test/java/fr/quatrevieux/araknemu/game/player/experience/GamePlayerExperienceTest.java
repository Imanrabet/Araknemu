package fr.quatrevieux.araknemu.game.player.experience;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.common.PlayerLevelUp;
import fr.quatrevieux.araknemu.game.event.common.PlayerXpChanged;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.account.NewPlayerLevel;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.NOPLogger;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class GamePlayerExperienceTest extends GameBaseCase {
    private GamePlayerExperience gamePlayerExperience;
    private Player player;
    private ListenerAggregate dispatcher;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushExperience();
        container.get(PlayerExperienceService.class).preload(NOPLogger.NOP_LOGGER);

        player = new Player(1);
        player.setLevel(7);
        player.setExperience(7450);

        gamePlayerExperience = new GamePlayerExperience(
            player,
            container.get(PlayerExperienceService.class),
            dispatcher = new DefaultListenerAggregate()
        );
    }

    @Test
    void values() {
        assertEquals(7, gamePlayerExperience.level());
        assertEquals(7300, gamePlayerExperience.min());
        assertEquals(7450, gamePlayerExperience.current());
        assertEquals(10500, gamePlayerExperience.max());
        assertFalse(gamePlayerExperience.maxLevelReached());
    }

    @Test
    void maxLevelReached() {
        player.setLevel(200);

        assertTrue(gamePlayerExperience.maxLevelReached());
    }

    @Test
    void addExperienceOnMaxLevel() {
        player.setLevel(200);

        AtomicReference<PlayerXpChanged> ref = new AtomicReference<>();
        dispatcher.add(PlayerXpChanged.class, ref::set);

        gamePlayerExperience.add(1000);

        assertEquals(8450, gamePlayerExperience.current());
        assertNotNull(ref.get());
    }

    @Test
    void addExperienceWithoutLevelUp() {
        AtomicReference<PlayerXpChanged> ref = new AtomicReference<>();
        dispatcher.add(PlayerXpChanged.class, ref::set);

        gamePlayerExperience.add(1000);

        assertEquals(8450, gamePlayerExperience.current());
        assertEquals(7, gamePlayerExperience.level());
        assertNotNull(ref.get());
    }

    @Test
    void addExperienceWithOnLevelUp() {
        AtomicReference<PlayerLevelUp> ref = new AtomicReference<>();
        dispatcher.add(PlayerLevelUp.class, ref::set);

        gamePlayerExperience.add(4000);

        assertEquals(11450, gamePlayerExperience.current());
        assertEquals(8, gamePlayerExperience.level());
        assertEquals(10500, gamePlayerExperience.min());
        assertEquals(14500, gamePlayerExperience.max());
        assertEquals(1, player.spellPoints());
        assertEquals(5, player.boostPoints());
        assertNotNull(ref.get());
        assertEquals(8, ref.get().level());
    }

    @Test
    void addExperienceWithMultipleLevel() {
        AtomicReference<PlayerLevelUp> ref = new AtomicReference<>();
        dispatcher.add(PlayerLevelUp.class, ref::set);

        gamePlayerExperience.add(10000);

        assertEquals(17450, gamePlayerExperience.current());
        assertEquals(9, gamePlayerExperience.level());
        assertEquals(14500, gamePlayerExperience.min());
        assertEquals(19200, gamePlayerExperience.max());
        assertEquals(2, player.spellPoints());
        assertEquals(10, player.boostPoints());
        assertNotNull(ref.get());
        assertEquals(9, ref.get().level());
    }

    @Test
    void addExperienceWithMaxLevelReach() {
        AtomicReference<PlayerLevelUp> ref = new AtomicReference<>();
        dispatcher.add(PlayerLevelUp.class, ref::set);

        player.setLevel(199);
        player.setExperience(5703616000L);

        gamePlayerExperience.add(7407232000L);

        assertEquals(5703616000L + 7407232000L, gamePlayerExperience.current());
        assertEquals(200, gamePlayerExperience.level());
        assertEquals(7407232000L, gamePlayerExperience.min());
        assertEquals(7407232000L, gamePlayerExperience.max());
        assertEquals(200, ref.get().level());
    }

    @Test
    void functionalNewLevel() throws SQLException, ContainerException {
        GamePlayer player = gamePlayer(true);
        requestStack.clear();

        player.experience().add(860000);

        assertEquals(51, player.experience().level());
        assertEquals(5, player.characteristics().boostPoints());
        assertEquals(1, player.spells().upgradePoints());

        requestStack.assertAll(
            new NewPlayerLevel(51),
            new Stats(player)
        );
    }

    @Test
    void functionalAddXp() throws SQLException, ContainerException {
        GamePlayer player = gamePlayer(true);
        requestStack.clear();

        player.experience().add(1000);

        assertEquals(50, player.experience().level());

        requestStack.assertAll(
            new Stats(player)
        );
    }
}