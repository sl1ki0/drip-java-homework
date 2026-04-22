package drip.hw5.cli;

import drip.hw5.config.AffineTransformConfig;
import drip.hw5.config.VariationType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CliArgumentParserTest {
    private final CliArgumentParser parser = new CliArgumentParser();

    @Test
    void parsesArgumentsFromShortAndLongFlags() {
        CommandLineOptions options = parser.parse(new String[] {
            "-w", "800",
            "--height=600",
            "--seed", "32.123531",
            "-i", "5000",
            "-o", "output/result.png",
            "-t", "4",
            "-s", "3"
        });

        assertEquals(800, options.width());
        assertEquals(600, options.height());
        assertEquals(Double.doubleToLongBits(32.123531d), options.seed());
        assertEquals(5000, options.iterationCount());
        assertEquals("output/result.png", options.outputPath().toString());
        assertEquals(4, options.threads());
        assertEquals(3, options.symmetryLevel());
    }

    @Test
    void parsesAffineParamsAndFunctions() {
        CommandLineOptions options = parser.parse(new String[] {
            "--affine-params", "0.1,0.2,0.3,0.4,0.5,0.6/1.0,1.1,1.2,1.3,1.4,1.5",
            "--functions", "swirl:1.0,horseshoe:0.5"
        });

        assertEquals(2, options.affineParams().size());
        AffineTransformConfig first = options.affineParams().getFirst();
        assertEquals(0.1, first.a());
        assertEquals(0.6, first.f());
        assertEquals(VariationType.SWIRL, options.functions().get(0).name());
        assertEquals(0.5, options.functions().get(1).weight());
    }

    @Test
    void rejectsUnknownVariation() {
        CommandLineException exception = assertThrows(
            CommandLineException.class,
            () -> parser.parse(new String[] {"--functions", "unknown:1.0"})
        );

        assertEquals("Unsupported variation: unknown", exception.getMessage());
    }

    @Test
    void rejectsMissingArgumentValue() {
        CommandLineException exception = assertThrows(
            CommandLineException.class,
            () -> parser.parse(new String[] {"--width"})
        );

        assertEquals("Missing value for argument: --width", exception.getMessage());
    }
}
