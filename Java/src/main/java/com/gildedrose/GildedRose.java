package com.gildedrose;

class GildedRose {
    private static final int MIN_QUALITY = 0;
    private static final int MAX_QUALITY = 50;

    private static final String AGED_BRIE = "Aged Brie";
    private static final String BACKSTAGE_PASS =
            "Backstage passes to a TAFKAL80ETC concert";
    private static final String SULFURAS = "Sulfuras, Hand of Ragnaros";
    private static final String CONJURED_PREFIX = "Conjured";

    Item[] items;

    public GildedRose(Item[] items) {
        this.items = items;
    }

    public void updateQuality() {
        for (Item item : items) {
            update(item);
        }
    }

    private void update(Item item) {
        if (SULFURAS.equals(item.name)) {
            return;
        }

        if (AGED_BRIE.equals(item.name)) {
            increaseQuality(item, item.sellIn <= 0 ? 2 : 1);
        } else if (BACKSTAGE_PASS.equals(item.name)) {
            updateBackstagePass(item);
        } else {
            int degradationRate = item.name.startsWith(CONJURED_PREFIX) ? 2 : 1;
            decreaseQuality(item, item.sellIn <= 0 ? degradationRate * 2 : degradationRate);
        }

        item.sellIn--;
    }

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

    private void increaseQuality(Item item, int amount) {
        item.quality = Math.min(MAX_QUALITY, item.quality + amount);
    }

    private void decreaseQuality(Item item, int amount) {
        item.quality = Math.max(MIN_QUALITY, item.quality - amount);
    }
}
