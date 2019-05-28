package com.kiran.fileexplorer.rest;

import com.kiran.fileexplorer.core.NonWatchingRealtimeFileDetailsProvider;
import com.kiran.fileexplorer.core.RealtimeFileDetailsProvider;
import com.kiran.fileexplorer.core.model.FileLocation;
import com.kiran.fileexplorer.util.LoggerUtil;
import org.slf4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Path("realtime")
public class RealtimeFileExplorerService {

    private static Logger log = LoggerUtil.contextLogger();

    private final RealtimeFileDetailsProvider fileDetailsProvider = new NonWatchingRealtimeFileDetailsProvider();

    @GET
    @Path("getAllFiles")
    @Produces(MediaType.APPLICATION_JSON)
    public List<FileLocation> getAllFiles() {
        List<FileLocation> locations = new ArrayList<>();
        for (File root : File.listRoots()) {
            try {
                locations.add(fileDetailsProvider.retrieveFileTree(Paths.get(root.getAbsolutePath())));
            } catch (IllegalAccessException | IOException e) {
                log.error("Error while processing details for : {}", root.getAbsolutePath(), e);
            }
        }
        log.info("Sending Response");
        return locations;
    }

    @POST
    @Path("getForPath")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public List<FileLocation> getForPath(String path) {
        log.info("Will respond for path: {}", path);
        List<FileLocation> locations = new ArrayList<>();
        try {
            locations.add(fileDetailsProvider.retrieveFileTree(Paths.get(path)));
        } catch (IllegalAccessException | IOException e) {
            log.error("Error while processing details for : {}", path, e);
        }
        log.info("Sending Response");
        return locations;
    }
}
