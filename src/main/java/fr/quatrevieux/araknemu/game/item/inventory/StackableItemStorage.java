package fr.quatrevieux.araknemu.game.item.inventory;

import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.item.inventory.exception.ItemNotFoundException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * Add stack capabilities to an item storage
 */
final public class StackableItemStorage<E extends ItemEntry> implements ItemStorage<E> {
    final private ItemStorage<E> storage;
    final private int stackPosition;

    final private Map<Item, E> stackMap = new HashMap<>();

    public StackableItemStorage(ItemStorage<E> storage) {
        this(storage, ItemEntry.DEFAULT_POSITION);
    }

    public StackableItemStorage(ItemStorage<E> storage, int stackPosition) {
        this.storage = storage;
        this.stackPosition = stackPosition;

        for (E entry : storage) {
            if (entry.position() == stackPosition) {
                stackMap.put(entry.item(), entry);
            }
        }
    }

    @Override
    public E get(int id) throws ItemNotFoundException {
        return storage.get(id);
    }

    @Override
    public E add(Item item, int quantity, int position) throws InventoryException {
        if (position == stackPosition) {
            E entry = stackMap.get(item);

            if (entry != null && entry.position() == stackPosition) {
                entry.add(quantity);

                return entry;
            }
        }

        E entry = storage.add(item, quantity, position);

        if (position == stackPosition) {
            stackMap.put(entry.item(), entry);
        }

        return entry;
    }

    @Override
    public E delete(int id) throws InventoryException {
        E entry = storage.delete(id);

        if (entry.position() == stackPosition) {
            stackMap.remove(entry.item());
        }

        return entry;
    }

    @Override
    public Iterator<E> iterator() {
        return storage.iterator();
    }

    /**
     * Find entry with same item
     *
     * @param item Item to search
     *
     * @return The entry, or null if there is not corresponding entry
     */
    public Optional<E> find(Item item) {
        E entry = stackMap.get(item);

        if (entry == null || entry.position() != stackPosition) {
            return Optional.empty();
        }

        return Optional.of(entry);
    }

    /**
     * Add item for indexing
     *
     * /!\ Use carefully : can break stacking system if overriding entry
     */
    public void indexing(E entry) {
        stackMap.put(entry.item(), entry);
    }
}
