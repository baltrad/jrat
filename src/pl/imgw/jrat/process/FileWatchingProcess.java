/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import static pl.imgw.jrat.tools.out.Logging.ERROR;
import static pl.imgw.jrat.tools.out.Logging.WARNING;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import name.pachler.nio.file.ClosedWatchServiceException;
import name.pachler.nio.file.FileSystems;
import name.pachler.nio.file.Path;
import name.pachler.nio.file.Paths;
import name.pachler.nio.file.StandardWatchEventKind;
import name.pachler.nio.file.WatchEvent;
import name.pachler.nio.file.WatchKey;
import name.pachler.nio.file.WatchService;
import pl.imgw.jrat.tools.out.LogHandler;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class FileWatchingProcess implements Runnable {

    private WatchService watchSvc = FileSystems.getDefault().newWatchService();
    private HashMap<String, Long> fileTimeMap = new HashMap<String, Long>();
    private HashMap<WatchKey, String> pathMap = new HashMap<WatchKey, String>();

    private FilesProcessor proc = null;
    private List<File> files = new LinkedList<File>();
//    private File[] watchedPath = null;

    private boolean valid = false;
    
    public FileWatchingProcess(FilesProcessor proc, List<File> watchedPathList) {
        if (watchedPathList == null || watchedPathList.isEmpty()) {
            LogHandler.getLogs().displayMsg("No valid folders to watch.",
                    WARNING);
            return;
        }
        this.proc = proc;
//        this.watchedPath = watchedPath;
        Path path = null;
        try {
            for (File file : watchedPathList) {
                if(!file.isDirectory())
                    continue;
                path = Paths.get(file.getPath());
                WatchKey key = path.register(watchSvc,
                        StandardWatchEventKind.ENTRY_CREATE,
                        StandardWatchEventKind.ENTRY_MODIFY);
                pathMap.put(key, file.getPath());
            }
        } catch (UnsupportedOperationException uox) {
            LogHandler.getLogs().displayMsg("file watching not supported!",
                    ERROR);
            LogHandler.getLogs().saveErrorLogs(FileWatchingProcess.class.getName(),
                    uox.getMessage());

        } catch (IOException iox) {
            LogHandler.getLogs().displayMsg(
                    "I/O errors while watching " + path, ERROR);
            LogHandler.getLogs().saveErrorLogs(FileWatchingProcess.class.getName(),
                    iox.getMessage());

        }

        valid = true;
        
    }

    public boolean isValid() {
        return valid;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        if (proc == null || pathMap.isEmpty()) {
            return;
        }
        
        LogHandler.getLogs().displayMsg(
                "Watching process started with: " + proc.getProcessName(),
                WARNING);
        
        for (String path : pathMap.values()) {
            LogHandler.getLogs().displayMsg("Start watching " + path, WARNING);
        }
        while (true) {

            // take() will block until a file has been created/deleted
            WatchKey signalledKey;
            try {
                signalledKey = watchSvc.poll(2,
                        java.util.concurrent.TimeUnit.SECONDS);
            } catch (InterruptedException ix) {
                // we'll ignore being interrupted
                continue;
            } catch (ClosedWatchServiceException cwse) {
                // other thread closed watch service
                LogHandler.getLogs().displayMsg(
                        "watch service closed, terminating", ERROR);
                LogHandler.getLogs().saveErrorLogs(FileWatchingProcess.class.getName(),
                        cwse.getMessage());
                break;
            }

            if (signalledKey == null) {
                long time;
                long timeNow = System.currentTimeMillis();
                Iterator<String> itr = fileTimeMap.keySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next();
                    time = fileTimeMap.get(key);
                    /* start processing 1sec after last file modification */
                    if ((timeNow - time) > 1000) {
                        fileTimeMap.remove(key);
                        itr = fileTimeMap.keySet().iterator();
                        File input = new File(key);

                        LogHandler.getLogs().displayMsg(
                                "New file: " + input.getName(),
                                WARNING);
                        files.add(input);
                        proc.processFile(files);
                        files.clear();
                        input.delete();
                        
                    }
                }
                if (fileTimeMap.isEmpty()) {
                    try {
                        signalledKey = watchSvc.take();
                    } catch (ClosedWatchServiceException e1) {
                        LogHandler.getLogs().displayMsg(
                                "watch service closed, terminating",
                                ERROR);
                        LogHandler.getLogs().saveErrorLogs(
                                FileWatchingProcess.class.getName(), e1.getMessage());
                    } catch (InterruptedException e1) {
                        LogHandler.getLogs().displayMsg(
                                "watch service interrupted", ERROR);
                        LogHandler.getLogs().saveErrorLogs(
                                FileWatchingProcess.class.getName(), e1.getMessage());
                    }
                } else {
                    continue;
                }
            }
            List<WatchEvent<?>> list = signalledKey.pollEvents();
            String incomingFile = pathMap.get(signalledKey);
            // VERY IMPORTANT! call reset() AFTER pollEvents() to allow the
            // key to be reported again by the watch service
            signalledKey.reset();

            for (WatchEvent<?> e : list) {
                if (e.kind() == StandardWatchEventKind.ENTRY_CREATE) {
                    Path context = (Path) e.context();
                    fileTimeMap.put(incomingFile + "/" + context.toString(),
                            System.currentTimeMillis());
                } else if (e.kind() == StandardWatchEventKind.ENTRY_MODIFY) {
                    Path context = (Path) e.context();
                    fileTimeMap.put(incomingFile + "/" + context.toString(),
                            System.currentTimeMillis());
                } else if (e.kind() == StandardWatchEventKind.OVERFLOW) {
                    System.out
                            .println("OVERFLOW: more changes happened than we could retreive");
                }
            }

        }

    }

}
