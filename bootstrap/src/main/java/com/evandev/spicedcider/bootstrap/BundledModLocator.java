package com.evandev.spicedcider.bootstrap;

import cpw.mods.jarhandling.JarContents;
import net.neoforged.neoforgespi.ILaunchContext;
import net.neoforged.neoforgespi.locating.IDiscoveryPipeline;
import net.neoforged.neoforgespi.locating.IModFileCandidateLocator;
import net.neoforged.neoforgespi.locating.IncompatibleFileReporting;
import net.neoforged.neoforgespi.locating.ModFileDiscoveryAttributes;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.Map;
import java.util.stream.Stream;

public class BundledModLocator implements IModFileCandidateLocator {
    private static final String BUNDLED_SUFFIX = "-mod.jar";

    @Override
    public void findCandidates(ILaunchContext context, IDiscoveryPipeline pipeline) {
        Path bundled = locateRoot().resolve("META-INF/jarjar");
        try (Stream<Path> jars = Files.list(bundled)) {
            jars.filter(path -> path.getFileName().toString().endsWith(BUNDLED_SUFFIX))
                    .forEach(path -> add(path, pipeline));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to list bundled Spiced Cider jars in " + bundled, e);
        }
    }

    private static void add(Path jar, IDiscoveryPipeline pipeline) {
        try {
            URI uri = new URI("jij:" + jar.toAbsolutePath().toUri().getRawSchemeSpecificPart()).normalize();
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Map.of("packagePath", jar));
            JarContents contents = JarContents.of(fileSystem.getPath("/"));
            pipeline.addJarContent(contents, ModFileDiscoveryAttributes.DEFAULT, IncompatibleFileReporting.WARN_ALWAYS);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to add bundled jar " + jar.getFileName(), e);
        }
    }

    private static Path locateRoot() {
        CodeSource source = BundledModLocator.class.getProtectionDomain().getCodeSource();
        if (source == null || source.getLocation() == null) {
            throw new IllegalStateException("Cannot locate the Spiced Cider bootstrap jar");
        }
        try {
            return Paths.get(source.getLocation().toURI());
        } catch (Exception e) {
            throw new IllegalStateException("Cannot resolve the Spiced Cider bootstrap jar from " + source.getLocation(), e);
        }
    }
}
