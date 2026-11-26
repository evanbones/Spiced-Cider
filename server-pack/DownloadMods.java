import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;

public class DownloadMods {
    public static void main(String[] args) throws Exception {
        Path manifest = Path.of(args.length > 0 ? args[0] : "downloads.txt");
        if (!Files.exists(manifest)) return;
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        List<String> lines = Files.readAllLines(manifest);
        for (String line : lines) {
            String[] parts = line.trim().split(" ", 3);
            if (parts.length < 3) continue;
            String sha512 = parts[0];
            String url = parts[1];
            Path target = Path.of(parts[2]);
            if (Files.exists(target) && sha512.equals(hash(target))) continue;
            System.out.println("Downloading " + target);
            Path tmp = Path.of(target + ".part");
            if (target.getParent() != null) Files.createDirectories(target.getParent());
            HttpResponse<InputStream> response = client.send(HttpRequest.newBuilder(URI.create(url)).build(), HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() != 200) throw new IllegalStateException(url + " returned " + response.statusCode());
            try (InputStream in = response.body()) {
                Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING);
            }
            if (!sha512.equals(hash(tmp))) {
                Files.delete(tmp);
                throw new IllegalStateException("Hash mismatch for " + url);
            }
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    static String hash(Path file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        try (InputStream in = Files.newInputStream(file)) {
            byte[] buffer = new byte[65536];
            int read;
            while ((read = in.read(buffer)) > 0) digest.update(buffer, 0, read);
        }
        return HexFormat.of().formatHex(digest.digest());
    }
}
