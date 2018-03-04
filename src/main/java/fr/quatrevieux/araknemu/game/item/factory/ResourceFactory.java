package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectMappers;
import fr.quatrevieux.araknemu.game.item.type.Resource;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;

import java.util.List;

/**
 * Factory for resources.
 * Will be used as default factory
 */
final public class ResourceFactory implements ItemFactory {
    final private EffectMappers mappers;

    public ResourceFactory(EffectMappers mappers) {
        this.mappers = mappers;
    }

    @Override
    public Item create(ItemTemplate template, GameItemSet set, boolean maximize) {
        return create(template, template.effects(), maximize);
    }

    @Override
    public Item retrieve(ItemTemplate template, GameItemSet set, List<ItemTemplateEffectEntry> effects) {
        return create(template, effects, false);
    }

    @Override
    public Type[] types() {
        return new Type[] {
            Type.NONE,
            Type.RESSOURCE,
            Type.QUETES,
            Type.DOCUMENT,
            Type.CEREALE,
            Type.FLEUR,
            Type.PLANTE,
            Type.BOIS,
            Type.MINERAIS,
            Type.ALLIAGE,
            Type.POISSON,
            Type.FRUIT,
            Type.OS,
            Type.POUDRE,
            Type.COMESTI_POISSON,
            Type.PIERRE_PRECIEUSE,
            Type.PIERRE_BRUTE,
            Type.FARINE,
            Type.PLUME,
            Type.POIL,
            Type.ETOFFE,
            Type.CUIR,
            Type.LAINE,
            Type.GRAINE,
            Type.PEAU,
            Type.HUILE,
            Type.PELUCHE,
            Type.POISSON_VIDE,
            Type.VIANDE,
            Type.VIANDE_CONSERVEE,
            Type.QUEUE,
            Type.METARIA,
            Type.LEGUME,
            Type.TEINTURE,
            Type.EQUIP_ALCHIMIE,
            Type.OEUF_FAMILIER,
            Type.OBJET_MISSION,
            Type.CLEFS,
            Type.BOUFTOU,
            Type.PLANCHE,
            Type.ECORCE,
            Type.RACINE,
            Type.SAC_RESSOURCE,
            Type.PATTE,
            Type.AILE,
            Type.OEUF,
            Type.OREILLE,
            Type.CARAPACE,
            Type.BOURGEON,
            Type.OEIL,
            Type.GELEE,
            Type.COQUILLE,
        };
    }

    private Item create(ItemTemplate template, List<ItemTemplateEffectEntry> effects, boolean maximize) {
        return new Resource(
            template,
            mappers.get(SpecialEffect.class).create(effects, maximize)
        );
    }
}
