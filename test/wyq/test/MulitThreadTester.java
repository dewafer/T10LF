package wyq.test;

import java.io.File;
import java.io.FileFilter;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import wyq.appengine2.toolbox.MuliThreadFileFinder;
import wyq.appengine2.toolbox.MuliThreadFileFinder.AsynchronizedFileHandler;
import wyq.appengine2.toolbox.MuliThreadFileFinder.FileHandler;
import wyq.appengine2.toolbox.MuliThreadFileFinder.Result;
import wyq.appengine2.toolbox.MuliThreadFileFinder.SearchResult;

public class MulitThreadTester {

	/**
	 * This test demonstrated the usage of MuliThreadFileFinder.search method.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void Test() throws InterruptedException {

		// Scan C:\\ dir and all it's sub-dirs for .exe files.
		SearchResult search = MuliThreadFileFinder.search(new File("C:\\"),
				new FileFilter() {

					@Override
					public boolean accept(File pathname) {
						return pathname.isFile()
								&& pathname.getName().contains(".exe");
					}
				});

		// sleep for 2 seconds
		System.out.println("sleep for 2s...");
		TimeUnit.SECONDS.sleep(2);

		// check search is finished
		System.out.println("finished?" + search.isFinished());
		for (File f : search.getResult()) {
			System.out.println(f);
		}
		System.out
				.println("============================================================");

		// you can stop searching using following method
		System.out.println("Stopping...");
		search.stop();
		// but it won't stop immediately, you have to wait for finish
		System.out.println("sleep for half a second...");
		TimeUnit.MILLISECONDS.sleep(500);
		System.out.println("finished?" + search.isFinished());
		for (File f : search.getResult()) {
			System.out.println(f);
		}
		System.out
				.println("============================================================");

		// Or, use getResultAwait method to wait until it's finished
		System.out.println("wait for result...");
		search.getResultAwait();
		System.out.println("finished?" + search.isFinished());
		for (File f : search.getResultAwait()) {
			System.out.println(f);
		}
		System.out
				.println("============================================================");
		System.out.println("sleep for 1 sec and execute System.gc().");
		TimeUnit.SECONDS.sleep(1);
		System.gc();

		// This time scan for C: again but we don't specify FileFilter but using
		// FileHandler instead
		System.out.println("new search using FileHandler...");
		Result s2 = MuliThreadFileFinder.search(new File("C:\\"), null,
				new FileHandler() {

					private int count = 0;

					@Override
					public void handle(File f) {
						try {
							// mock heavy operations
							TimeUnit.MILLISECONDS.sleep(new Random()
									.nextInt(100));
							System.out.println(++count + "\t" + f);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

		System.out.println("sleep for 60ms...");
		TimeUnit.MILLISECONDS.sleep(60);
		System.out.println("Stopping...");
		s2.stop();

		System.out.println("wait for stop...");
		s2.waitFinish();

		System.out.println("wait done.");
		System.out
				.println("============================================================");
		System.out.println("sleep for 1 sec and execute System.gc().");
		TimeUnit.SECONDS.sleep(1);
		System.gc();

		// This time we specified an AsynchronizedFile Handler.
		// Each file will be handled in a separated thread.
		System.out.println("new search using AsynchronizedFileHandler...");
		Result s3 = MuliThreadFileFinder.search(new File("C:\\"), null,
				new AsynchronizedFileHandler() {

					private int count = 0;

					@Override
					public void handle(File f) {
						try {
							// mock heavy operations
							TimeUnit.MILLISECONDS.sleep(new Random()
									.nextInt(100));
							System.out.println(++count + "\t" + f);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

		System.out.println("sleep for 60ms, not too many threads...");
		TimeUnit.MILLISECONDS.sleep(60);
		System.out.println("Stopping...");
		s3.stop();

		System.out.println("wait for stop...");
		s3.waitFinish();
		System.out.println("wait end...wait for termination.");
		s3.waitTermination();
		System.out.println("done...");
	}
}
