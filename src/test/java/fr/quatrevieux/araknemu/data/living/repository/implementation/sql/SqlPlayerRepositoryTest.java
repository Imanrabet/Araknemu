package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.DatabaseTestCase;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.transformer.ChannelsTransformer;
import fr.quatrevieux.araknemu.data.transformer.MutableCharacteristicsTransformer;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.value.ServerCharacters;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class SqlPlayerRepositoryTest extends DatabaseTestCase {
    private fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        repository = new SqlPlayerRepository(
            connection,
            new MutableCharacteristicsTransformer(),
            new ChannelsTransformer()
        );

        repository.initialize();
    }

    @AfterEach
    void tearDown() {
        repository.destroy();
    }

    @Test
    void addWillSetId() {
        Player player = new Player(-1, 5, 1, "name", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null);

        Player inserted = repository.add(player);

        assertNotSame(player, inserted);

        assertEquals(1, inserted.id());
        assertEquals(5, inserted.accountId());
        assertEquals(1, inserted.serverId());
        assertEquals("name", inserted.name());
        assertEquals(Race.FECA, inserted.race());
        assertEquals(Sex.MALE, inserted.sex());
        assertEquals(-1, inserted.colors().color1());
        assertEquals(1, inserted.level());
    }

    @Test
    void addAndGet() {
        Player player = repository.add(new Player(-1, 5, 1, "name", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null));

        Player get = repository.get(player);

        assertEquals(1, get.id());
        assertEquals(5, get.accountId());
        assertEquals(1, get.serverId());
        assertEquals("name", get.name());
        assertEquals(Race.FECA, get.race());
        assertEquals(Sex.MALE, get.sex());
        assertEquals(-1, get.colors().color1());
        assertEquals(1, get.level());
    }

    @Test
    void has() {
        Player player = new Player(-1, 5, 1, "name", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null);

        assertFalse(repository.has(player));

        player = repository.add(player);

        assertTrue(repository.has(player));
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(new Player(-1, 5, 1, "name", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null)));
    }

    @Test
    void delete() {
        Player player = new Player(-1, 5, 1, "name", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null);
        player = repository.add(player);

        assertTrue(repository.has(player));

        repository.delete(player);

        assertFalse(repository.has(player));
    }

    @Test
    void deleteNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.delete(new Player(15)));
    }

    @Test
    void nameExists() {
        assertFalse(repository.nameExists(new Player(-1, 5, 1, "name", null, null, null, 1, null)));
        repository.add(new Player(-1, 5, 1, "name", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null));
        assertTrue(repository.nameExists(new Player(-1, 5, 1, "name", null, null, null, 1, null)));
    }

    @Test
    void accountCharactersCount() {
        repository.add(new Player(-1, 5, 1, "One", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null));
        repository.add(new Player(-1, 5, 1, "Two", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null));
        repository.add(new Player(-1, 5, 2, "Other", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null));

        assertEquals(2, repository.accountCharactersCount(new Player(-1, 5, 1, null, null, null, null, 0, null)));
    }

    @Test
    void getForGamePlayerNotFound() {
        int id = repository.add(new Player(-1, 5, 1, "One", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null)).id();

        assertThrows(EntityNotFoundException.class, () -> repository.getForGame(Player.forGame(-1, 123, 2)));
    }

    @Test
    void getForGameBadAccount() {
        int id = repository.add(new Player(-1, 5, 1, "One", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null)).id();

        assertThrows(EntityNotFoundException.class, () -> repository.getForGame(Player.forGame(id, 123, 1)));
    }

    @Test
    void getForGameBadServer() {
        int id = repository.add(new Player(-1, 5, 1, "One", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null)).id();

        assertThrows(EntityNotFoundException.class, () -> repository.getForGame(Player.forGame(id, 5, 2)));
    }

    @Test
    void getForGameSuccess() {
        int id = repository.add(new Player(-1, 5, 1, "One", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, null)).id();

        Player player = repository.getForGame(Player.forGame(id, 5, 1));

        assertEquals("One", player.name());
        assertEquals(Race.FECA, player.race());
    }

    @Test
    void insertWithStats() {
        MutableCharacteristics characteristics = new DefaultCharacteristics();

        characteristics.set(Characteristic.ACTION_POINT, 12);
        characteristics.set(Characteristic.STRENGTH, 5);

        Player player = repository.add(new Player(-1, 5, 1, "One", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, characteristics));

        assertEquals(characteristics, repository.get(player).stats());
    }

    @Test
    void insertWithPoints() {
        Player player = repository.add(new Player(-1, 5, 1, "One", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, new DefaultCharacteristics(), new Position(123, 456), EnumSet.noneOf(ChannelType.class), 10, 15, 75, 125, new Position(321, 251), 127));

        player = repository.get(player);

        assertEquals(10, player.boostPoints());
        assertEquals(15, player.spellPoints());
        assertEquals(75, player.life());
        assertEquals(125, player.experience());
    }

    @Test
    void insertWithSavedPositionAndKamas() {
        Player player = repository.add(new Player(-1, 5, 1, "One", Race.FECA, Sex.MALE, new Colors(-1, -1, -1), 1, new DefaultCharacteristics(), new Position(123, 456), EnumSet.noneOf(ChannelType.class), 10, 15, 75, 125, new Position(321, 251), 127));

        player = repository.get(player);

        assertEquals(new Position(321, 251), player.savedPosition());
        assertEquals(127, player.kamas());
    }

    @Test
    void allServersCharactersCount() {
        repository.add(Player.forCreation(1, 1, "bob", Race.CRA, Sex.FEMALE, new Colors(-1, -1, -1)));
        repository.add(Player.forCreation(1, 1, "cc", Race.CRA, Sex.FEMALE, new Colors(-1, -1, -1)));
        repository.add(Player.forCreation(1, 1, "dd", Race.CRA, Sex.FEMALE, new Colors(-1, -1, -1)));
        repository.add(Player.forCreation(1, 3, "other", Race.CRA, Sex.FEMALE, new Colors(-1, -1, -1)));

        Collection<ServerCharacters> serverCharacters = repository.accountCharactersCount(1);

        assertEquals(2, serverCharacters.size());

        ServerCharacters[] arr = serverCharacters.toArray(new ServerCharacters[]{});

        assertEquals(1, arr[0].serverId());
        assertEquals(3, arr[0].charactersCount());

        assertEquals(3, arr[1].serverId());
        assertEquals(1, arr[1].charactersCount());
    }

    @Test
    void saveNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.save(Player.forCreation(5, 2, "bob", Race.CRA, Sex.FEMALE, new Colors(-1, -1, -1))));
    }

    @Test
    void saveSuccess() {
        Player player = repository.add(Player.forCreation(1, 1, "bob", Race.CRA, Sex.FEMALE, new Colors(-1, -1, -1)));

        player.setPosition(new Position(1234, 56));
        player.stats().set(Characteristic.ACTION_POINT, 12);
        player.channels().add(ChannelType.INFO);
        player.setBoostPoints(15);
        player.setLife(36);
        player.setExperience(741);
        player.setKamas(4589);
        player.setSavedPosition(new Position(4568, 123));

        repository.save(player);

        Player savedPlayer = repository.get(player);

        assertEquals(new Position(1234, 56), savedPlayer.position());
        assertEquals(12, savedPlayer.stats().get(Characteristic.ACTION_POINT));
        assertEquals(EnumSet.of(ChannelType.INFO), player.channels());
        assertEquals(15, savedPlayer.boostPoints());
        assertEquals(36, savedPlayer.life());
        assertEquals(741, savedPlayer.experience());
        assertEquals(4589, savedPlayer.kamas());
        assertEquals(new Position(4568, 123), savedPlayer.savedPosition());
    }
}