package rars.venus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Manages recently opened files for the editor.
 * Stores up to MAX_RECENT_FILES file paths in Java Preferences.
 * 
 * @author Jesse Dushime
 */
public class RecentFilesManager {
    private static final String PREFS_KEY = "RecentFiles";
    private static final String SEPARATOR = "|";
    private static final int MAX_RECENT_FILES = 10;
    
    private Preferences prefs;
    private List<String> recentFiles;
    
    public RecentFilesManager() {
        prefs = Preferences.userNodeForPackage(RecentFilesManager.class);
        recentFiles = new ArrayList<>();
        loadRecentFiles();
    }
    
    /**
     * Load recent files from preferences
     */
    private void loadRecentFiles() {
        String stored = prefs.get(PREFS_KEY, "");
        recentFiles.clear();
        
        if (stored != null && !stored.isEmpty()) {
            String[] paths = stored.split("\\" + SEPARATOR);
            for (String path : paths) {
                if (!path.isEmpty()) {
                    // Only add if file still exists
                    File file = new File(path);
                    if (file.exists() && file.isFile()) {
                        recentFiles.add(path);
                    }
                }
            }
        }
    }
    
    /**
     * Save recent files to preferences
     */
    private void saveRecentFiles() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < recentFiles.size(); i++) {
            if (i > 0) {
                sb.append(SEPARATOR);
            }
            sb.append(recentFiles.get(i));
        }
        prefs.put(PREFS_KEY, sb.toString());
    }
    
    /**
     * Add a file to the recent files list.
     * If the file is already in the list, move it to the top.
     * 
     * @param filePath Full path to the file
     */
    public void addRecentFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return;
        }
        
        // Remove if already exists (to move it to top)
        recentFiles.remove(filePath);
        
        // Add to beginning of list
        recentFiles.add(0, filePath);
        
        // Limit to MAX_RECENT_FILES
        while (recentFiles.size() > MAX_RECENT_FILES) {
            recentFiles.remove(recentFiles.size() - 1);
        }
        
        saveRecentFiles();
    }
    
    /**
     * Get the list of recent files
     * 
     * @return List of file paths, most recent first
     */
    public List<String> getRecentFiles() {
        // Refresh list to remove files that no longer exist
        loadRecentFiles();
        return new ArrayList<>(recentFiles);
    }
    
    /**
     * Clear all recent files
     */
    public void clearRecentFiles() {
        recentFiles.clear();
        saveRecentFiles();
    }
    
    /**
     * Check if there are any recent files
     * 
     * @return true if there are recent files
     */
    public boolean hasRecentFiles() {
        loadRecentFiles();
        return !recentFiles.isEmpty();
    }
}
