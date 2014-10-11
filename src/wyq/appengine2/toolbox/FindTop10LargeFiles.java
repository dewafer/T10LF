package wyq.appengine2.toolbox;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import wyq.appengine2.toolbox.MuliThreadFileFinder.AsynchronizedFileHandler;
import wyq.appengine2.toolbox.MuliThreadFileFinder.Result;

/**
 * This class finds out the top10 files from a given directory and all its
 * sub-dirs
 * 
 * @author dewafer
 *
 */
public class FindTop10LargeFiles implements AsynchronizedFileHandler {

	private List<File> bucket = new ArrayList<File>();
	private long minSize = -1;
	private FileLengthComparator comparator = new FileLengthComparator();
	public static final int MAX_FILE_NUM = 10;

	/**
	 * main method, usage: <br/>
	 * <code>java FindTop10LargeFiles &lt;dir&gt</code>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// print usage
		if (args.length == 0 || args[0].length() == 0) {
			System.out.println("Usage: java "
					+ FindTop10LargeFiles.class.getName() + " <dir>");
			System.exit(0);
		}

		// check dir
		File dir = new File(args[0]);
		if (!dir.exists()) {
			System.out.println("dir doesn't exists! " + args[0]);
			System.exit(-1);
		}
		if (!dir.isDirectory()) {
			System.out.println("it is not a dir! " + args[0]);
			System.exit(-1);
		}

		// mulit-thread search
		System.out.println("starting search at: " + args[0]);
		// use this class as handler
		FindTop10LargeFiles top10 = new FindTop10LargeFiles();
		// start search
		Result search = MuliThreadFileFinder.search(new File(args[0]),
				new FileFilter() {

					@Override
					public boolean accept(File pathname) {
						return pathname.isFile();
					}
				}, top10);

		// wait for threads finish
		System.out.println("Search started, wait termination...");
		search.waitTermination();

		// get findings
		List<File> files = top10.getTop10LargeFiles();
		System.out.println("Search finished. Found TOP " + files.size()
				+ " large files:");
		// print out findings
		for (int i = 0; i < files.size(); i++) {
			File f = files.get(i);
			System.out.println("TOP " + (files.size() - i) + " size:"
					+ formatSize(f.length()) + f);
		}

	}

	/**
	 * Handles each file provided by <code>MuliThreadFileFinder</code>
	 * 
	 * @see wyq.appengine2.toolbox.MuliThreadFileFinder.FileHandler#handle(java.io.File)
	 */
	@Override
	public void handle(File f) {

		// use bucket as a lock
		// Notice, the bucket is not synchronized!
		synchronized (bucket) {

			long size = f.length();

			if (bucket.size() < MAX_FILE_NUM) {
				// if bucket is not full
				bucket.add(f);
				if (bucket.size() == MAX_FILE_NUM) {
					// bucket is full
					Collections.sort(bucket, comparator);
					minSize = bucket.get(0).length();
				}
			} else {
				// we don't need file smaller than minSize
				if (size > minSize) {
					minSize = size;
					bucket.add(f);
				}

				if (bucket.size() > MAX_FILE_NUM) {

					Collections.sort(bucket, comparator);

					// remove the smallest
					bucket.remove(0);

					minSize = bucket.get(0).length();

				}
			}
		}
	}

	@Override
	public String toString() {
		return bucket.toString();
	}

	/**
	 * Get the top 10 large files. Notice this method is not thread safe!
	 * 
	 * @return an unmodifiable list of findings
	 */
	public List<File> getTop10LargeFiles() {
		return Collections.unmodifiableList(bucket);
	}

	/**
	 * This comparator is used to compare files. Note: this comparator imposes
	 * orderings that are inconsistent with equals
	 */
	public static class FileLengthComparator implements Comparator<File> {
		@Override
		public int compare(File o1, File o2) {
			return compareFile(o1, o2);
		}

		public static int compareFile(File o1, File o2) {
			// return long directly may cause overflow
			long d = o1.length() - o2.length();
			return d > 0 ? 1 : (d < 0 ? -1 : 0);
		}
	}

	/**
	 * format a file's length as a human readable string
	 * 
	 * @param size
	 *            file's length
	 * @return readable string
	 */
	private static String formatSize(long size) {
		StringBuffer sb = new StringBuffer();
		long[] calc = divide(size);
		int d = (int) calc[0];
		long calcSize = calc[1];
		String surffix = SIZE_SURFFIX[d];

		sb.append(calcSize);
		sb.append(surffix);
		if (d > 0)
			sb.append("(" + size + ")");

		return sb.toString();

	}

	private static final String[] SIZE_SURFFIX = { "B", "K", "M", "G", "T" };

	private static long[] divide(long size) {
		int i = 0;
		while (size >= 1024 && i < SIZE_SURFFIX.length) {
			size /= 1024;
			i++;
		}
		return new long[] { i, size };
	}

}