package com.gildedrose;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GildedRoseTest {

    private static final String AGED_BRIE = "Aged Brie";
    private static final String BACKSTAGE_PASS =
            "Backstage passes to a TAFKAL80ETC concert";
    private static final String SULFURAS = "Sulfuras, Hand of Ragnaros";

    @ParameterizedTest(name = "{0}: sellIn {1}, quality {2} -> sellIn {3}, quality {4}")
    @MethodSource("itemUpdates")
    void shouldUpdateItemAccordingToItsRules(
            String name,
            int initialSellIn,
            int initialQuality,
            int expectedSellIn,
            int expectedQuality
    ) {
        Item item = update(new Item(name, initialSellIn, initialQuality));

        assertAll(
                () -> assertEquals(name, item.name, "name"),
                () -> assertEquals(expectedSellIn, item.sellIn, "sellIn"),
                () -> assertEquals(expectedQuality, item.quality, "quality")
        );
    }

    private static Stream<Arguments> itemUpdates() {
        return Stream.of(
                // Ordinary items
                Arguments.of("Elixir", 5, 10, 4, 9),
                Arguments.of("Elixir", 0, 10, -1, 8),
                Arguments.of("Elixir", 5, 0, 4, 0),
                Arguments.of("Elixir", 0, 1, -1, 0),

                // Aged Brie
                Arguments.of(AGED_BRIE, 2, 10, 1, 11),
                Arguments.of(AGED_BRIE, 0, 10, -1, 12),
                Arguments.of(AGED_BRIE, 2, 50, 1, 50),
                Arguments.of(AGED_BRIE, 0, 49, -1, 50),

                // Backstage passes
                Arguments.of(BACKSTAGE_PASS, 11, 20, 10, 21),
                Arguments.of(BACKSTAGE_PASS, 10, 20, 9, 22),
                Arguments.of(BACKSTAGE_PASS, 5, 20, 4, 23),
                Arguments.of(BACKSTAGE_PASS, 0, 20, -1, 0),
                Arguments.of(BACKSTAGE_PASS, 5, 49, 4, 50),

                // Legendary items
                Arguments.of(SULFURAS, 0, 80, 0, 80),
                Arguments.of(SULFURAS, -1, 80, -1, 80),

                // Conjured items
                Arguments.of("Conjured Mana Cake", 3, 6, 2, 4),
                Arguments.of("Conjured Mana Cake", 0, 6, -1, 2),
                Arguments.of("Conjured Mana Cake", 3, 1, 2, 0),
                Arguments.of("Conjured Sword", 0, 3, -1, 0)
        );
    }

    @Test
    void shouldUpdateEveryItemInTheInventory() {
        Item ordinary = new Item("Elixir", 5, 10);
        Item brie = new Item(AGED_BRIE, 2, 10);
        Item conjured = new Item("Conjured Mana Cake", 3, 6);

        new GildedRose(new Item[]{ordinary, brie, conjured}).updateQuality();

        assertAll(
                () -> assertEquals(9, ordinary.quality),
                () -> assertEquals(11, brie.quality),
                () -> assertEquals(4, conjured.quality)
        );
    }

    @Test
    void shouldHandleAnEmptyInventory() {
        new GildedRose(new Item[0]).updateQuality();
    }

    private Item update(Item item) {
        GildedRose app = new GildedRose(new Item[]{item});
        app.updateQuality();
        return item;
    }
}
