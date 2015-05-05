package org.fhissen.utils;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public class FileTimestamp {
    public static final void resetTimes(String filepath, long millis) {
    	resetTimes(filepath, millis, millis);
    }

    public static final void resetTimes(String filepath, long millis_c, long millis_m) {
    	resetTimes(Paths.get(filepath), millis_c, millis_m);
    }
    
    public static final void resetTimes(Path path, long millis) {
    	resetTimes(path, millis, millis);
    }


    public static final void resetTimes(Path path, long millis_c, long millis_m) {
        try {
            Files.setAttribute(path, "basic:creationTime", FileTime.fromMillis(millis_c), NOFOLLOW_LINKS);
		} catch (Exception e) {}
        try {
            Files.setAttribute(path, "basic:lastModifiedTime", FileTime.fromMillis(millis_m), NOFOLLOW_LINKS);
		} catch (Exception e) {}
        try {
            Files.setAttribute(path, "basic:lastAccessTime", FileTime.fromMillis(millis_m), NOFOLLOW_LINKS);
		} catch (Exception e) {}
    }
    
    public static final void resetTimes(String filepath, long millis_c, long millis_m, long millis_a) {
        Path path = Paths.get(filepath);
        
        try {
            Files.setAttribute(path, "basic:creationTime", FileTime.fromMillis(millis_c), NOFOLLOW_LINKS);
		} catch (Exception e) {}
        try {
            Files.setAttribute(path, "basic:lastModifiedTime", FileTime.fromMillis(millis_m), NOFOLLOW_LINKS);
		} catch (Exception e) {}
        try {
            Files.setAttribute(path, "basic:lastAccessTime", FileTime.fromMillis(millis_a), NOFOLLOW_LINKS);
		} catch (Exception e) {}
    }
    
    public static class FileTimestamp_Info{
    	public long creation, modification;
    }
    public static final FileTimestamp_Info getTimes(String filepath){
    	Path file = Paths.get(filepath);
    	
    	try {
    		FileTimestamp_Info i = new FileTimestamp_Info();

        	BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);

        	i.creation = attr.creationTime().toMillis();
        	i.modification = attr.lastModifiedTime().toMillis();

        	return i;
		} catch (Exception e) {
			return null;
		}
    }

    public static final long lastModi(Path file){
    	try {
        	return Files.readAttributes(file, BasicFileAttributes.class).lastModifiedTime().toMillis();
		} catch (Exception e) {
			return 0;
		}
    }

    public static final void copyTimes(String filepath_src, String filepath_dst) {
        Path path_src = Paths.get(filepath_src);
        Path path_dst = Paths.get(filepath_dst);
        
        try {
        	BasicFileAttributes attr = Files.readAttributes(path_src, BasicFileAttributes.class);

            Files.setAttribute(path_dst, "basic:creationTime", attr.creationTime(), NOFOLLOW_LINKS);
            Files.setAttribute(path_dst, "basic:lastModifiedTime", attr.lastModifiedTime(), NOFOLLOW_LINKS);
            Files.setAttribute(path_dst, "basic:lastAccessTime", attr.lastAccessTime(), NOFOLLOW_LINKS);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public static final void copyTimes(Path path_src, Path path_dst) {
        try {
        	BasicFileAttributes attr = Files.readAttributes(path_src, BasicFileAttributes.class);

            Files.setAttribute(path_dst, "basic:creationTime", attr.creationTime(), NOFOLLOW_LINKS);
            Files.setAttribute(path_dst, "basic:lastModifiedTime", attr.lastModifiedTime(), NOFOLLOW_LINKS);
            Files.setAttribute(path_dst, "basic:lastAccessTime", attr.lastAccessTime(), NOFOLLOW_LINKS);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
