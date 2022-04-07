package analyzerdisk;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class Analyzer {
    private HashMap<String, Long> sizes;

    public Map<String, Long> CalculateDirectorySize(Path path){
        try{
            sizes = new HashMap<>();
            Files.walkFileTree(
                    path,
                    new SimpleFileVisitor<>(){
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            long size = Files.size(file);
                            UpDataSizeDirectory(file, size);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                            return FileVisitResult.SKIP_SUBTREE;
                        }
                    }
            );
            return sizes;
        }
        catch (IOException e){
           throw new RuntimeException(e);
        }
    }

    private void UpDataSizeDirectory(Path path, Long size){
        String key = path.toString();
        sizes.put(key, size + sizes.getOrDefault(key, 0L));

        Path parent = path.getParent();
        if(parent !=  null) UpDataSizeDirectory(parent, size);
    }
}
