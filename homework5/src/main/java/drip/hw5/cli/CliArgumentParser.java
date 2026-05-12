package drip.hw5.cli;

import drip.hw5.config.AffineTransformConfig;
import drip.hw5.config.VariationConfig;
import drip.hw5.config.VariationType;
import drip.hw5.util.SeedNormalizer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CliArgumentParser {
    public CommandLineOptions parse(String[] args) {
        Integer width = null;
        Integer height = null;
        Long seed = null;
        Integer iterationCount = null;
        Path outputPath = null;
        Integer threads = null;
        List<AffineTransformConfig> affineParams = null;
        List<VariationConfig> functions = null;
        Path configPath = null;
        Integer symmetryLevel = null;

        for (int index = 0; index < args.length; index++) {
            String current = args[index];
            String key = current;
            String inlineValue = null;
            int separatorIndex = current.indexOf('=');
            if (separatorIndex >= 0) {
                key = current.substring(0, separatorIndex);
                inlineValue = current.substring(separatorIndex + 1);
            }

            switch (key) {
                case "-w", "--width" -> width = parseInteger(key, readValue(args, index, inlineValue));
                case "-h", "--height" -> height = parseInteger(key, readValue(args, index, inlineValue));
                case "--seed" -> seed = parseSeed(readValue(args, index, inlineValue));
                case "-i", "--iteration-count" -> iterationCount = parseInteger(key, readValue(args, index, inlineValue));
                case "-o", "--output-path" -> outputPath = Path.of(readValue(args, index, inlineValue));
                case "-t", "--threads" -> threads = parseInteger(key, readValue(args, index, inlineValue));
                case "-ap", "--affine-params" -> affineParams = parseAffineParams(readValue(args, index, inlineValue));
                case "-f", "--functions" -> functions = parseFunctions(readValue(args, index, inlineValue));
                case "--config" -> configPath = Path.of(readValue(args, index, inlineValue));
                case "-s", "--symmetry-level" -> symmetryLevel = parseInteger(key, readValue(args, index, inlineValue));
                default -> throw new CommandLineException("Unknown argument: " + current);
            }
            if (inlineValue == null) {
                index++;
            }
        }

        return new CommandLineOptions(
            width,
            height,
            seed,
            iterationCount,
            outputPath,
            threads,
            affineParams,
            functions,
            configPath,
            symmetryLevel
        );
    }

    private String readValue(String[] args, int index, String inlineValue) {
        if (inlineValue != null) {
            return inlineValue;
        }
        int valueIndex = index + 1;
        if (valueIndex >= args.length) {
            throw new CommandLineException("Missing value for argument: " + args[index]);
        }
        return args[valueIndex];
    }

    private Integer parseInteger(String name, String rawValue) {
        try {
            return Integer.parseInt(rawValue);
        } catch (NumberFormatException exception) {
            throw new CommandLineException("Argument %s must be an integer: %s".formatted(name, rawValue));
        }
    }

    private Long parseSeed(String rawValue) {
        try {
            return SeedNormalizer.normalize(rawValue);
        } catch (IllegalArgumentException exception) {
            throw new CommandLineException(exception.getMessage());
        }
    }

    private List<AffineTransformConfig> parseAffineParams(String rawValue) {
        if (rawValue.isBlank()) {
            throw new CommandLineException("Argument --affine-params must not be empty.");
        }

        List<AffineTransformConfig> result = new ArrayList<>();
        String[] transforms = rawValue.split("/");
        for (String transform : transforms) {
            String[] coefficients = transform.split(",");
            if (coefficients.length != 6) {
                throw new CommandLineException("Each affine transform must contain 6 coefficients: " + transform);
            }
            result.add(new AffineTransformConfig(
                parseDouble("a", coefficients[0]),
                parseDouble("b", coefficients[1]),
                parseDouble("c", coefficients[2]),
                parseDouble("d", coefficients[3]),
                parseDouble("e", coefficients[4]),
                parseDouble("f", coefficients[5])
            ));
        }
        return List.copyOf(result);
    }

    private List<VariationConfig> parseFunctions(String rawValue) {
        if (rawValue.isBlank()) {
            throw new CommandLineException("Argument --functions must not be empty.");
        }

        List<VariationConfig> result = new ArrayList<>();
        String[] entries = rawValue.split(",");
        for (String entry : entries) {
            String[] parts = entry.split(":");
            if (parts.length != 2) {
                throw new CommandLineException("Function entry must have format name:weight - " + entry);
            }
            try {
                VariationType type = VariationType.fromName(parts[0]);
                result.add(new VariationConfig(type, parseDouble("weight", parts[1])));
            } catch (IllegalArgumentException exception) {
                throw new CommandLineException(exception.getMessage());
            }
        }
        return List.copyOf(result);
    }

    private double parseDouble(String name, String rawValue) {
        try {
            return Double.parseDouble(rawValue);
        } catch (NumberFormatException exception) {
            throw new CommandLineException("Value for %s must be a number: %s".formatted(name, rawValue));
        }
    }
}
