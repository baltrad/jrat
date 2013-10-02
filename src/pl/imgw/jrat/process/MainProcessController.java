/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import static pl.imgw.jrat.AplicationConstans.APS_DESC;
import static pl.imgw.jrat.AplicationConstans.DATE;
import static pl.imgw.jrat.AplicationConstans.REL_DATE;
import static pl.imgw.jrat.process.CommandLineArgsParser.*;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;

import pl.imgw.jrat.AplicationConstans;
import pl.imgw.jrat.calid.data.CalidParametersFileHandler;
import pl.imgw.jrat.calid.data.CalidParametersParser;
import pl.imgw.jrat.calid.proc.CalidController;
import pl.imgw.jrat.data.parsers.GlobalParser;

import pl.imgw.jrat.scansun.proc.ScansunParametersParser;
import pl.imgw.jrat.scansun.proc.ScansunController;
import pl.imgw.jrat.scansun.proc.ScansunRadarParametersFileHandler;
import pl.imgw.jrat.scansun.proc.ScansunSolarFluxFileHandler;

import pl.imgw.jrat.tools.out.ProductInfoPrinter;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class MainProcessController {

	private static Log log = LogManager.getLogger();
	private CommandLine cmd;
	private List<File> files = new LinkedList<File>();
	private List<File> folders = new LinkedList<File>();
	public static File root = new File(System.getProperty("user.dir"));

	public List<File> getFiles() {
		return files;
	}

	public MainProcessController(String[] args) {

		CommandLineArgsParser parser = new CommandLineArgsParser();
		if (parser.parseArgs(args))
			cmd = parser.getCmd();

		if (cmd == null)
			return;

		if (cmd.hasOption(QUIET)) {
			// System.out.println("ustawia quiet");
			LogManager.getInstance().setLogMode(Log.MODE_SILENT);
		} else if (cmd.hasOption(VERBOSE)) {
			// System.out.println("ustawia verbose");
			LogManager.getInstance().setLogMode(Log.MODE_VERBOSE);
		} else
			LogManager.getInstance().setLogMode(Log.MODE_NORMAL);

	}

	/**
	 * Starting main process, parsing Command line arguments and run proper
	 * processes
	 * 
	 * @return returns true if all goes well, without errors, otherwise return
	 *         false
	 */
	public boolean start() {

		if (cmd == null) {
			return false;
		}

		/* Setting output path */
		File output = root;
		if (cmd.hasOption(O)) {
			FileProcessController.setOutputFile(cmd.getOptionValue(O), output);
		}

		/* display help message */
		if (cmd.getOptions().length == 0 || cmd.hasOption(H)) {
			printHelp();
			return true;
		}

		/* display version message */
		if (cmd.hasOption(VERSION)) {
			printVersion();
			return true;
		}

		/* display CALID help message */
		if (cmd.hasOption(CALID_HELP)) {
			CalidParametersParser.printHelp();
			return true;
		}

		/* set input file global format */
		if (cmd.hasOption(FORMAT)) {
			GlobalParser.getInstance().setParser(cmd.getOptionValue(F));
		}

		/* print CALID results */
		if (cmd.hasOption(CALID_RESULT)) {
			CalidController.processResult(cmd);
			return true;
		}

		/* print list of available results of CALID */
		if (cmd.hasOption(CALID_LIST)) {
			CalidController.processList(cmd);
			return true;
		}

		/* print list of available results of CALID */
		if (cmd.hasOption(CALID_PLOT)) {

			CalidController.processPlot(cmd, output);
			return true;
		}

		/* set CALID option file */
		if (cmd.hasOption(CALID_OPT)) {
			CalidParametersFileHandler.getOptions().setOptionFile(
					cmd.getOptionValue(CALID_OPT));
		}

		/* display SCANSUN help message */
		if (cmd.hasOption(SCANSUN_HELP)) {
			ScansunParametersParser.printHelp();
			return true;
		}
		/* set SCANSUN option file */
		if (cmd.hasOption(SCANSUN_OPT)) {
			ScansunRadarParametersFileHandler.getHandler().setOptionFile(
					cmd.getOptionValue(SCANSUN_OPT));
		}

		/* set SCANSUN DRAO results file */
		if (cmd.hasOption(SCANSUN_DRAO)) {
			ScansunSolarFluxFileHandler.getHandler().setDatafile(
					cmd.getOptionValue(SCANSUN_DRAO));
		}

		/* print SCANSUN results */
		if (cmd.hasOption(SCANSUN_RESULT)) {
			ScansunController.processResult(cmd);
			return true;
		}

		if (cmd.hasOption(SCANSUN_PLOT)) {
			ScansunController.processPlots(cmd);
			return true;
		}

		/* Loading list of files to process or setting input folder path */
		FileProcessController.setInputFilesAndFolders(files, folders,
				cmd.getOptionValues(I));

		/* Print information about all input files */
		if (cmd.hasOption(PRINT)) {
			ProductInfoPrinter.print(files, cmd.hasOption(VERBOSE));
			return true;
		}

		/* Print image from all input files */
		if (cmd.hasOption(PRINTIMAGE)) {
			PrintingImageProcessController.printImage(files, output,
					cmd.getOptionValues(PRINTIMAGE));
			return true;
		}

		/* =========== setting processes =============== */
		VolumeProcessorManager volProc = new VolumeProcessorManager();
		FilesProcessor proc = null;

		/* CALID */
		if (cmd.hasOption(CALID)) {
			proc = CalidController.setCalidProcessor(cmd);
			volProc.addProcess((VolumesProcessor) proc);
		}

		/* SCANSUN */
		if (cmd.hasOption(SCANSUN)) {
			proc = ScansunController.setScansunProcessor(cmd);
			volProc.addProcess((VolumesProcessor) proc);
		}

		// test process, prints files name
		if (cmd.hasOption(TEST)) {
			proc = new FilesProcessor() {
				@Override
				public void processFile(List<File> files) {
					for (File file : files)
						log.printMsg("" + file, Log.MODE_SILENT);
					if (files.isEmpty())
						log.printMsg("No files to process", Log.MODE_SILENT);
				}

				@Override
				public String getProcessName() {
					// TODO Auto-generated method stub
					return "TEST process";
				}

			};
		}

		if (proc == null) {
			log.printMsg("No valid process has been set", Log.TYPE_WARNING,
					Log.MODE_VERBOSE);
			return false;
		}

		/* =========== setting working mode =============== */
		if (cmd.hasOption(WATCH)) {
			/* Starting continues mode */

			FileWatchingProcess watcher = new FileWatchingProcess(volProc,
					folders);

			if (!watcher.isValid())
				return false;

			if (!cmd.hasOption(QUIET))
				if (!FolderManager.continueWithDeletingFiles(folders)) {
					// decline to delete files, exiting
					return true;
				}

			Thread t = new Thread(watcher);
			t.start();
			if (t.isAlive()) {
				log.printMsg("Watching process started", Log.TYPE_NORMAL,
						Log.MODE_VERBOSE);
				return true;
			}
		} else if (cmd.hasOption(SEQ)) {
			/* Starting sequence mode */

			SequentialProcess seq = new SequentialProcess(volProc, folders,
					cmd.getOptionValue(SEQ));

			if (!seq.isValid())
				return false;
			if (!cmd.hasOption(QUIET))
				if (!FolderManager.continueWithDeletingFiles(folders)) {
					// decline to delete files, exiting
					return true;
				}

			Thread t = new Thread(seq);
			t.start();
			if (t.isAlive()) {
				return true;
			}
		} else {
			SingleRunProcessor single = new SingleRunProcessor(volProc,
					folders, files);

			if (!single.isValid())
				return false;
			// shouldn't be run as a separate thread, this is why I'm using
			// run() not start()
			single.run();
			return true;
		}

		return false;
	}

	public void printVersion() {
		log.printMsg("\n" + APS_DESC + "\nversion:\t"
				+ AplicationConstans.VERSION + "\nreleased date:\t" + REL_DATE
				+ "\ncompiled on:\t" + DATE, Log.TYPE_NORMAL, Log.MODE_SILENT);
	}

}