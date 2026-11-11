package pl.lugowski.routes;

import org.junit.jupiter.api.Test;
import pl.lugowski.routes.output.DisplayFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DisplayFormatTest {
    @Test
    void formatDurationReturnsCorrectTime(){
        assertEquals("1 h 1 min", DisplayFormat.duration(3660));
    }

    @Test
    void formatKilometersReturnCorrectFormattedString(){
        assertEquals("1.15 km", DisplayFormat.kilometers(1150));
    }
}
