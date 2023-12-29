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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.handler.chat;

import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.network.game.SessionAttachmentKey;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;

import java.time.Duration;

/**
 * An attachment for spam check
 */
public final class SpamCheckAttachment {
    public static final SessionAttachmentKey<SpamCheckAttachment> KEY = SpamCheckAttachment::new;

    /**
     * The last check time in milliseconds
     * This value is updated once the check interval is elapsed
     *
     * @see System#currentTimeMillis()
     */
    private long checkTime = 0;

    /**
     * The message count sent since the last check
     */
    private @NonNegative int messageCount = 0;

    private SpamCheckAttachment() {
        // Instantiation only by the attachment key
    }

    /**
     * Check for spam before sending a message or smiley
     *
     * @param interval The check interval {@link GameConfiguration.ChatConfiguration#spamCheckInterval()}
     * @param maxCount Maximum number of message or smiley that can be sent in the interval {@link GameConfiguration.ChatConfiguration#spamCheckMaxCount()}
     *
     * @return true if the limit is not reached, false otherwise
     */
    public boolean check(Duration interval, @Positive int maxCount) {
        final long now = System.currentTimeMillis();

        if (now - checkTime > interval.toMillis()) {
            checkTime = now;
            messageCount = 0;
        }

        if (messageCount >= maxCount) {
            return false;
        }

        ++messageCount;

        return true;
    }
}
