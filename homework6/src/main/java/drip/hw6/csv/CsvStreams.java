package drip.hw6.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

final class CsvStreams {
    private CsvStreams() {
    }

    static BufferedReader openReader(String filename) throws IOException {
        Path path = Path.of(filename);
        String lower = filename.toLowerCase(Locale.ROOT);
        InputStream raw = Files.newInputStream(path);
        try {
            InputStream wrapped = decorateInput(raw, lower, filename);
            return new BufferedReader(new InputStreamReader(wrapped, StandardCharsets.UTF_8));
        } catch (IOException exception) {
            raw.close();
            throw exception;
        }
    }

    static PrintWriter openWriter(String filename) throws IOException {
        Path path = Path.of(filename);
        String lower = filename.toLowerCase(Locale.ROOT);
        OutputStream raw = Files.newOutputStream(path);
        try {
            OutputStream wrapped = decorateOutput(raw, lower, path);
            return new PrintWriter(new BufferedWriter(new OutputStreamWriter(wrapped, StandardCharsets.UTF_8)));
        } catch (IOException exception) {
            raw.close();
            throw exception;
        }
    }

    private static InputStream decorateInput(InputStream raw, String lower, String filename) throws IOException {
        if (lower.endsWith(".zip")) {
            ZipInputStream zis = new ZipInputStream(raw);
            ZipEntry entry = zis.getNextEntry();
            if (entry == null) {
                zis.close();
                throw new IOException("ZIP archive contains no entries: " + filename);
            }
            return zis;
        }
        if (lower.endsWith(".gz")) {
            return new GZIPInputStream(raw);
        }
        return raw;
    }

    private static OutputStream decorateOutput(OutputStream raw, String lower, Path path) throws IOException {
        if (lower.endsWith(".zip")) {
            ZipOutputStream zos = new ZipOutputStream(raw);
            zos.putNextEntry(new ZipEntry(zipEntryName(path)));
            return zos;
        }
        if (lower.endsWith(".gz")) {
            return new GZIPOutputStream(raw);
        }
        return raw;
    }

    private static String zipEntryName(Path path) {
        String fileName = path.getFileName().toString();
        String lower = fileName.toLowerCase(Locale.ROOT);
        String trimmed = lower.endsWith(".zip")
            ? fileName.substring(0, fileName.length() - ".zip".length())
            : fileName;
        if (!trimmed.toLowerCase(Locale.ROOT).endsWith(".csv")) {
            trimmed = trimmed + ".csv";
        }
        return trimmed;
    }
}
