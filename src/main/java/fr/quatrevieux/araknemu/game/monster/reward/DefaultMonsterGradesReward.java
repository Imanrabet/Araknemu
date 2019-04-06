package fr.quatrevieux.araknemu.game.monster.reward;

import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardData;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardItem;

import java.util.List;

/**
 * Base implementation for grade set rewards
 */
final public class DefaultMonsterGradesReward implements MonsterGradesReward {
    final private MonsterRewardData data;
    final private List<MonsterRewardItem> items;

    public DefaultMonsterGradesReward(MonsterRewardData data, List<MonsterRewardItem> items) {
        this.data = data;
        this.items = items;
    }

    @Override
    public Interval kamas() {
        return data.kamas();
    }

    @Override
    public long experience(int gradeNumber) {
        return data.experiences()[gradeNumber - 1];
    }

    @Override
    public List<MonsterRewardItem> items() {
        return items;
    }

    @Override
    public MonsterReward grade(int gradeNumber) {
        return new DefaultMonsterReward(this, gradeNumber);
    }
}