package com.github.superz97.server.route;

import java.nio.file.Path;

public class SafeFileResolver {

    public static Path resolve(String directory, String fileName) {
        Path base = Path.of(directory).toAbsolutePath().normalize();
        Path resolved = base.resolve(fileName).normalize();
        if (!resolved.startsWith(base)) {
            throw new IllegalArgumentException("Invalid filename: " + fileName);
        }
        return resolved;
    }

}
