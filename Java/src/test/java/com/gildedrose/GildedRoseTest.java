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
    private static final String SULFURAS =
        "Sulfuras, Hand of Ragnaros";

    @ParameterizedTest
    @MethodSource("ordinaryItemCases")
    void shouldUpdateAnOrdinaryItem(
        int initialSellIn,
        int initialQuality,
        int expectedSellIn,
        int expectedQuality
    ) {
        assertUpdate(
            "Elixir",
            initialSellIn,
            initialQuality,
            expectedSellIn,
            expectedQuality
        );
    }

    private static Stream<Arguments> ordinaryItemCases() {
        return Stream.of(
            Arguments.of(5, 10, 4, 9),  // Dégradation normale (-1) avant péremption
            Arguments.of(0, 10, -1, 8), // Dégradation doublée (-2)
            Arguments.of(5, 0, 4, 0)  // La qualité ne descend jamais sous 0
        );
    }

    @ParameterizedTest
    @MethodSource("agedBrieCases")
    void shouldUpdateAgedBrie(
        int initialSellIn,
        int initialQuality,
        int expectedSellIn,
        int expectedQuality
    ) {
        assertUpdate(
            AGED_BRIE,
            initialSellIn,
            initialQuality,
            expectedSellIn,
            expectedQuality
        );
    }

    private static Stream<Arguments> agedBrieCases() {
        return Stream.of(
            Arguments.of(2, 10, 1, 11), // Amélioration normale (+1)
            Arguments.of(0, 10, -1, 12), // doublée
            Arguments.of(0, 49, -1, 50)  // ne depasse jamais le max 50
        );
    }

    @ParameterizedTest
    @MethodSource("backstagePassCases")
    void shouldUpdateABackstagePass(
        int initialSellIn,
        int initialQuality,
        int expectedSellIn,
        int expectedQuality
    ) {
        assertUpdate(
            BACKSTAGE_PASS,
            initialSellIn,
            initialQuality,
            expectedSellIn,
            expectedQuality
        );
    }

    private static Stream<Arguments> backstagePassCases() {
        return Stream.of(
            Arguments.of(11, 20, 10, 21), // Concert éloigné : +1
            Arguments.of(10, 20, 9, 22),  // Concert dans 10 jours : +2
            Arguments.of(5, 20, 4, 23),   // Concert dans 5 jours : +3
            Arguments.of(0, 20, -1, 0),   // Concert dépassé : qualité nulle
            Arguments.of(5, 49, 4, 50)    // La qualité ne dépasse jamais 50
        );
    }

    @Test
    void shouldNotUpdateSulfuras() {
        assertUpdate(SULFURAS, 0, 80, 0, 80); // ne change pas
    }

    @ParameterizedTest
    @MethodSource("conjuredItemCases")
    void shouldUpdateAConjuredItem(
        int initialSellIn,
        int initialQuality,
        int expectedSellIn,
        int expectedQuality
    ) {
        assertUpdate(
            "Conjured Mana Cake",
            initialSellIn,
            initialQuality,
            expectedSellIn,
            expectedQuality
        );
    }

    private static Stream<Arguments> conjuredItemCases() {
        return Stream.of(
            Arguments.of(3, 6, 2, 4),  // Dégradation doublée
            Arguments.of(0, 6, -1, 2), // Après expiration : -4
            Arguments.of(3, 1, 2, 0)   // La qualité ne descend jamais sous 0
        );
    }

    @Test
    void shouldRecognizeAnyConjuredItemName() {
        assertUpdate("Conjured Sword", 3, 6, 2, 4);
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

    private void assertUpdate(
        String name,
        int initialSellIn,
        int initialQuality,
        int expectedSellIn,
        int expectedQuality
    ) {
        Item item = new Item(name, initialSellIn, initialQuality);
        GildedRose app = new GildedRose(new Item[]{item});

        app.updateQuality();

        assertAll(
            () -> assertEquals(name, item.name, "name"),
            () -> assertEquals(expectedSellIn, item.sellIn, "sellIn"),
            () -> assertEquals(expectedQuality, item.quality, "quality")
        );
    }
}
