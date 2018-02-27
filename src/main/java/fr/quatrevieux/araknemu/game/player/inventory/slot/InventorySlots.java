package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.item.type.Equipment;

import java.util.ArrayList;
import java.util.Collection;

/**
 * All inventory slots
 */
final public class InventorySlots {
    final private Dispatcher dispatcher;
    final private ItemStorage<InventoryEntry> storage;

    final private InventorySlot defaultSlot;
    final private InventorySlot[] slots = new InventorySlot[58];

    public InventorySlots(Dispatcher dispatcher, ItemStorage<InventoryEntry> storage) {
        this.dispatcher  = dispatcher;
        this.storage     = storage;
        this.defaultSlot = new DefaultSlot(storage);
    }

    /**
     * Init all slots the the owner
     *
     * @param owner The inventory owner
     */
    public void init(GamePlayer owner) {
        initSlots(owner);
        fillSlots();
    }

    /**
     * Get an inventory slot
     */
    public InventorySlot get(int id) throws InventoryException {
        if (id == ItemEntry.DEFAULT_POSITION) {
            return defaultSlot;
        }

        if (id < -1 || id >= slots.length) {
            throw new InventoryException("Invalid slot");
        }

        return slots[id];
    }

    /**
     * Get current equipments
     */
    public Collection<Equipment> equipments() {
        Collection<Equipment> equipments = new ArrayList<>();

        for (InventorySlot slot : slots) {
            if (slot.hasEquipment()) {
                equipments.add(slot.equipment());
            }
        }

        return equipments;
    }

    private void add(InventorySlot slot) {
        slots[slot.id()] = slot;
    }

    private void initSlots(GamePlayer owner) {
        add(new AmuletSlot(dispatcher, storage, owner));
        add(new WeaponSlot(dispatcher, storage, owner));
        add(new RingSlot(dispatcher, storage, owner, RingSlot.RING1));
        add(new RingSlot(dispatcher, storage, owner, RingSlot.RING2));
        add(new BeltSlot(dispatcher, storage, owner));
        add(new BootsSlot(dispatcher, storage, owner));
        add(new HelmetSlot(dispatcher, storage, owner));
        add(new MantleSlot(dispatcher, storage, owner));
        add(new NullSlot(8)); // pet

        for (int id : DofusSlot.SLOT_IDS) {
            add(new DofusSlot(dispatcher, storage, owner, id));
        }

        for (int i = 15; i < UsableSlot.SLOT_ID_START; ++i) {
            add(new NullSlot(i)); // Add null slot for all unhandled slots
        }

        for (int i = UsableSlot.SLOT_ID_START; i <= UsableSlot.SLOT_ID_END; ++i) {
            add(new UsableSlot(storage, i));
        }
    }

    private void fillSlots() {
        for (InventoryEntry entry : storage) {
            try {
                get(entry.position()).uncheckedSet(entry);
            } catch (InventoryException e) {
                entry.entity().setPosition(ItemEntry.DEFAULT_POSITION);
            }
        }
    }
}