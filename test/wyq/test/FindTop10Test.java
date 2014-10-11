package wyq.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import wyq.appengine2.toolbox.FindTop10LargeFiles;

/**
 * JUnit test for {@linkplain FindTop10LargeFiles}
 * 
 * @author dewafer
 *
 */
public class FindTop10Test {

	Random rand = new Random();

	/**
	 * Test run {@linkplain FindTop10LargeFiles#handle(File)} on mulit thread.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void MulitThreadHandleTest() throws InterruptedException {

		// thread numbers
		int maxThreadsNum = 100;
		ExecutorService exec = Executors.newFixedThreadPool(maxThreadsNum);
		final CountDownLatch latch = new CountDownLatch(maxThreadsNum);

		// test target
		final FindTop10LargeFiles handler = new FindTop10LargeFiles();

		// prepare mock files
		final List<File> files = new ArrayList<File>();
		for (int i = 0; i < maxThreadsNum; i++) {
			files.add(new MockFile());
		}

		// create and run threads
		for (int i = 0; i < maxThreadsNum; i++) {
			final int current = i;
			exec.execute(new Runnable() {

				@Override
				public void run() {
					// use CountDownLatch to run all the threads at once
					latch.countDown();
					handler.handle(files.get(current));
				}
			});
		}

		// shutdown and wait for termination
		exec.shutdown();
		exec.awaitTermination(5, TimeUnit.SECONDS);

		// print result
		System.out.println(handler);
		// sort mock files and print
		Collections.sort(files);
		System.out.println(files);

		// the result should be the same as the last 10 entities of the mock
		// file list
		Assert.assertEquals(
				handler.toString(),
				files.subList(maxThreadsNum - FindTop10LargeFiles.MAX_FILE_NUM,
						maxThreadsNum).toString());

	}

	/**
	 * A real run, scan C: dir.
	 */
	@Test
	public void TestMain() {
		FindTop10LargeFiles.main(new String[] { "C:\\" });
	}

	/**
	 * Extends java.io.File to create a MockFile class, which can create fake
	 * File with random length. For test use only.
	 * 
	 * @author dewafer
	 *
	 */
	@SuppressWarnings("serial")
	public class MockFile extends File {

		private long length = 0;

		/**
		 * Create random length fake File
		 */
		public MockFile() {
			this(Math.abs(rand.nextLong()));
		}

		/**
		 * Create a length-specified fake File
		 * 
		 * @param length
		 */
		public MockFile(long length) {
			super(String.valueOf(length));
			this.length = length;
		}

		@Override
		public long length() {
			return length;
		}

		@Override
		public String toString() {
			return String.valueOf(length);
		}

		@Override
		public int compareTo(File pathname) {
			return FindTop10LargeFiles.FileLengthComparator.compareFile(this,
					pathname);
		}

	}
}
