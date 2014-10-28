package storage;

import java.util.List;

/**
 * This interface represent the storage
 * 
 * Please use this for implementing FileStorage
 * 
 * @author: Tran Cong Thien
 *
 */

public interface Storage {

	/**
	 * This method return a list of String (generated by Task object)
	 * 
	 * @return ArrayList<String>
	 */
	List<String> read();
	List<String> readArchive();

	/**
	 * This method store the list of string into the storage
	 * 
	 * @param list
	 *            : list of string representation of Tasks
	 */
	void write(List<String> list);
	void writeArchive(List<String> archiveList);
}

