package fr.quatrevieux.araknemu.game.world.item.inventory;

import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.ItemNotFoundException;

/**
 * Base type for store items
 */
public interface ItemStorage<E extends ItemEntry> extends Iterable<E> {
    /**
     * Get an entry by its id
     *
     * @param id The entry id
     */
    public E get(int id) throws ItemNotFoundException;

    /**
     * Add a new item to the storage
     *
     * @param item Item to add
     *
     * @return The related entry
     */
    default public E add(Item item) throws InventoryException {
        return add(item, 1, ItemEntry.DEFAULT_POSITION);
    }

    /**
     * Add a new item to the storage
     *
     * @param item Item to add
     * @param quantity Quantity to set
     *
     * @return The related entry
     */
    default public E add(Item item, int quantity) throws InventoryException {
        return add(item, quantity, ItemEntry.DEFAULT_POSITION);
    }

    /**
     * Add a new item to the storage
     *
     * @param item Item to add
     * @param quantity The quantity
     * @param position The item position
     *
     * @return The related entry
     */
    public E add(Item item, int quantity, int position) throws InventoryException;
}
