package com.gildedrose;

/**
 * Gère la mise à jour quotidienne de l'inventaire de la Gilded Rose.
 * Chaque type d'article possède ses propres règles d'évolution concernant
 * sa date limite de vente et sa qualité.
 */
class GildedRose {
    // Limites applicables à tous les articles, à l'exception de Sulfuras (qualité 80).
    private static final int MIN_QUALITY = 0;
    private static final int MAX_QUALITY = 50;

    // Constantes permettant d'identifier les articles au comportement particulier.
    private static final String AGED_BRIE = "Aged Brie";
    private static final String BACKSTAGE_PASS = "Backstage passes to a TAFKAL80ETC concert";
    private static final String SULFURAS = "Sulfuras, Hand of Ragnaros";
    private static final String CONJURED_PREFIX = "Conjured";


    Item[] items;

    /**
     * Initialise la Gilded Rose avec l'inventaire fourni.
     * @param items articles à mettre à jour quotidiennement
     */
    public GildedRose(Item[] items) {
        this.items = items;
    }

    /**
     * Met à jour tous les articles pour simuler le passage d'une journée.
     */
    public void updateQuality() {
        for (Item item : items) {
            update(item);
        }
    }

    /**
     * Applique à un article la règle d'évolution correspondant à son type.
     * @param item article à mettre à jour
     */
    private void update(Item item) {
        // Sulfuras est légendaire : ni sa qualité ni sa date limite ne changent.
        if (SULFURAS.equals(item.name)) {
            return;
        }

        if (AGED_BRIE.equals(item.name)) {
            increaseQuality(item, item.sellIn <= 0 ? 2 : 1);
        } else if (BACKSTAGE_PASS.equals(item.name)) {
            updateBackstagePass(item);
        } else {
            // Taux de base : 2 pour Conjured, 1 pour un article ordinaire.
            int degradationRate = item.name.startsWith(CONJURED_PREFIX) ? 2 : 1;
            // Après expiration, la dégradation est à nouveau doublée.
            decreaseQuality(item, item.sellIn <= 0 ? degradationRate * 2 : degradationRate);
        }

        item.sellIn--;
    }

    /**
     * Met à jour un pass selon la proximité ou le dépassement du concert.
     * @param item Backstage pass à mettre à jour
     */
    private void updateBackstagePass(Item item) {
        if (item.sellIn <= 0) {
            item.quality = MIN_QUALITY;
        } else if (item.sellIn <= 5) {
            increaseQuality(item, 3);
        } else if (item.sellIn <= 10) {
            increaseQuality(item, 2);
        } else {
            increaseQuality(item, 1);
        }
    }

    /**
     * Augmente la qualité sans dépasser la limite autorisée.
     */
    private void increaseQuality(Item item, int amount) {
        item.quality = Math.min(MAX_QUALITY, item.quality + amount);
    }

    /**
     * Diminue la qualité sans descendre sous zéro.
     */
    private void decreaseQuality(Item item, int amount) {
        item.quality = Math.max(MIN_QUALITY, item.quality - amount);
    }
}
